from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import pandas as pd
import joblib
import nltk
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
from nltk.corpus import wordnet
from nltk.tokenize import TreebankWordTokenizer
from transformers import pipeline
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer
from textblob import TextBlob
from nrclex import NRCLex
import os
import warnings
import logging

# Suppress specific warnings
warnings.filterwarnings("ignore", category=FutureWarning)

# Initialize logging
logging.basicConfig(level=logging.INFO)

# Initialize FastAPI
app = FastAPI()

# CORS configuration
origins = [
    "http://localhost:3000",
    "https://soft-stock.netlify.app"
]
app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Model paths
model_files = {
    'XGB_close_classifier': "compressed_models/XGB_close_classifier_compressed.pkl",
    'XGB_trade_classifier': "compressed_models/XGB_trade_classifier_compressed.pkl",
    'xgb_regressor_close': "compressed_models/xgb_regressor_close_compressed.pkl",
}

# Initialize models dictionary
models = {}

# Initialize NLTK and Transformers
nltk.download('punkt')
nltk.download('averaged_perceptron_tagger')
nltk.download('wordnet')
nltk.download('stopwords')
nltk.download('vader_lexicon')

# Initialize NLP tools
ner_pipeline = pipeline('ner', model="dbmdz/bert-large-cased-finetuned-conll03-english", aggregation_strategy="simple")
sia = SentimentIntensityAnalyzer()
lemmatizer = WordNetLemmatizer()
tokenizer = TreebankWordTokenizer()
stop_words = set(stopwords.words('english'))

# Define input data model
class InputData(BaseModel):
    headline: str
    ticker: int

# Helper functions
def load_model(model_name):
    if model_name not in models:
        try:
            models[model_name] = joblib.load(model_files[model_name])
            logging.info(f"Loaded {model_name} successfully.")
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Model loading error: {str(e)}")
    return models[model_name]

def preprocess_text(text):
    tokens = tokenizer.tokenize(text.lower())
    tagged_tokens = nltk.pos_tag(tokens)
    lemmatized_tokens = [lemmatizer.lemmatize(token, get_pos_tag(tag)) for token, tag in tagged_tokens if token not in stop_words]
    return " ".join(lemmatized_tokens)

def get_pos_tag(tag):
    return {
        'J': wordnet.ADJ,
        'V': wordnet.VERB,
        'N': wordnet.NOUN,
        'R': wordnet.ADV
    }.get(tag[0], wordnet.NOUN)  # Return NOUN as the default

def extract_features(text, ticker):
    # Extract various text features
    blob = TextBlob(text).sentiment
    entities = ner_pipeline(text)
    emotion_scores = NRCLex(text).raw_emotion_scores

    tokens = text.split()
    positive_word_count = sum(1 for word in tokens if sia.polarity_scores(word)['compound'] > 0.05)
    negative_word_count = sum(1 for word in tokens if sia.polarity_scores(word)['compound'] < -0.05)

    inputdf = pd.DataFrame([{
        'Vader_sentiment_score': sia.polarity_scores(text)['compound'],
        'Blob_polarity': blob.polarity,
        'BlobSubjectivity': blob.subjectivity,
        'positive_word_count': positive_word_count,
        'negative_word_count': negative_word_count,
        'person_count': sum(1 for entity in entities if entity['entity_group'] == 'PER'),
        'organization_count': sum(1 for entity in entities if entity['entity_group'] == 'ORG'),
        'location_count': sum(1 for entity in entities if entity['entity_group'] == 'LOC'),
        'anger': emotion_scores.get('anger', 0),
        'anticipation': emotion_scores.get('anticipation', 0),
        'disgust': emotion_scores.get('disgust', 0),
        'fear': emotion_scores.get('fear', 0),
        'joy': emotion_scores.get('joy', 0),
        'sadness': emotion_scores.get('sadness', 0),
        'trust': emotion_scores.get('trust', 0),
        'Ticker': ticker
    }])

    return inputdf

@app.get("/")
async def root():
    return {"message": "Server is Running"}

@app.post("/predict")
async def predict(data: InputData):
    logging.info(f"Received prediction request with data: {data}")

    try:
        preprocessed_text = preprocess_text(data.headline)
        logging.info(f"Preprocessed text: {preprocessed_text}")

        features_df = extract_features(preprocessed_text, data.ticker)
        features_df_numeric = features_df.select_dtypes(include=['int64', 'float64'])

        # Load models only when needed
        prob_trade = float(load_model('XGB_trade_classifier').predict_proba(features_df_numeric)[0, 1])
        prob_close = float(load_model('XGB_close_classifier').predict_proba(features_df_numeric)[0, 1])
        pred_close = float(load_model('xgb_regressor_close').predict(features_df_numeric)[0])

        return {
            "probability_trade": prob_trade,
            "probability_close": prob_close,
            "predicted_close": pred_close
        }

    except HTTPException as http_ex:
        raise http_ex  # Reraise HTTPException to maintain proper error response
    except Exception as e:
        logging.error(f"Error during prediction: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Prediction error: {str(e)}")
