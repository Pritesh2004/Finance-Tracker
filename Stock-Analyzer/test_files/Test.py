#!/usr/bin/env python
# coding: utf-8

# <h3><B>Preprocessing the data

# In[1]:


import nltk 
nltk.download('punkt')
nltk.download('averaged_perceptron_tagger')
nltk.download('averaged_perceptron_tagger_eng')
nltk.download('wordnet')
nltk.download('stopwords')
nltk.download('vader_lexicon')
nltk.download('punkt_tab')

from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
from nltk.corpus import wordnet
from nltk.tokenize import TreebankWordTokenizer
from nrclex import NRCLex
from transformers import pipeline
ner_pipeline =pipeline('ner',grouped_entities=True)

# In[2]:


import pandas as pd

str_data = ["Elon Musk has often inflamed politically tense moments, raising worries for the US election"]
str_data[0]=str_data[0].lower()
df = pd.DataFrame(str_data, columns=['Text'])

# Define a function to map POS tags to WordNet tags
def get_pos_tag(tag):
    if tag.startswith('J'):
        return wordnet.ADJ
    elif tag.startswith('V'):
        return wordnet.VERB
    elif tag.startswith('N'):
        return wordnet.NOUN
    elif tag.startswith('R'):
        return wordnet.ADV
    else:
        return wordnet.NOUN

# Initialize lemmatizer and tokenizer
lemmatizer = WordNetLemmatizer()
tokenizer = TreebankWordTokenizer()
stop_words = set(stopwords.words('english'))

# Apply lemmatization and stop words removal
df['Text'] = df['Text'].apply(
    lambda x: " ".join([
        lemmatizer.lemmatize(token, get_pos_tag(tag))
        for token, tag in nltk.pos_tag(tokenizer.tokenize(x))
        if token not in stop_words
    ]) if pd.notnull(x) else ""
)

print(df['Text'].head())

# In[3]:


inputdf=pd.DataFrame(columns=['Vader_sentiment_score', 'Blob_polarity', 'BlobSubjectivity',
       'positive_word_count', 'negative_word_count', 'person_count',
       'organization_count', 'location_count', 'anger', 'anticipation',
       'disgust', 'fear', 'joy', 'sadness', 'trust', 'Ticker'])

# <h3><b>VADER SENTIMENT SCORES

# In[4]:


from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer
analyzer=SentimentIntensityAnalyzer()
def extratVaderFeatures(text):
    score=analyzer.polarity_scores(text)
    sentimentScore=score['compound']
    return sentimentScore
inputdf['Vader_sentiment_score']=df['Text'].apply(extratVaderFeatures)

# <h3><b>TEXTBLOB SUBJECTIVITY AND POLARITY

# In[5]:


from textblob import TextBlob
def extractTextBlobSubjectivity(text):
    blob=TextBlob(text)
    polarity=blob.sentiment.polarity
    subjectivity=blob.sentiment.subjectivity
    return polarity,subjectivity
inputdf['Blob_polarity'],inputdf['BlobSubjectivity']=zip(*df['Text'].apply(extractTextBlobSubjectivity))

# <h3><b>POSITIVE AND NEGATIVE WORD COUNT

# In[6]:


from nltk.sentiment.vader import SentimentIntensityAnalyzer

sia = SentimentIntensityAnalyzer()

def posneg_vader(text):
    tokens = text.split()
    positive_count = sum(1 for word in tokens if sia.polarity_scores(word)['compound'] > 0.05)
    negative_count = sum(1 for word in tokens if sia.polarity_scores(word)['compound'] < -0.05)
    return positive_count, negative_count

inputdf['positive_word_count'], inputdf['negative_word_count'] = zip(*df['Text'].apply(posneg_vader))

print(inputdf[['positive_word_count', 'negative_word_count']].head())

# <h3><b>NAME ENTITY RECOGNITION

# In[7]:


def ner__(text):
    entities=ner_pipeline(text)
    person_count=sum(1 for entity in entities if entity['entity_group']=='PER')
    organization_count = sum(1 for entity in entities if entity['entity_group'] == 'ORG')
    location_count = sum(1 for entity in entities if entity['entity_group'] == 'LOC')
    return person_count, organization_count, location_count

# Apply the NER function to your data
inputdf['person_count'], inputdf['organization_count'], inputdf['location_count'] = zip(*df['Text'].apply(ner__))

print(inputdf[['person_count', 'organization_count', 'location_count']].head())

# <h3><b>EMOTION SCORES

# In[8]:


from nrclex import NRCLex
def nrc(text):
    emotion=NRCLex(text)
    scores=emotion.raw_emotion_scores
    return scores.get('anger',0),scores.get('anticipation',0),scores.get('disgust',0),scores.get('fear',0),scores.get('joy',0),scores.get('sadness',0),scores.get('trust',0)
inputdf['anger'],inputdf['anticipation'],inputdf['disgust'],inputdf['fear'],inputdf['joy'],inputdf['sadness'],inputdf['trust']= zip(*df['Text'].apply(nrc))

# <h2><b>General news

# 
# Mapping of categories to numeric values:
# 
# AAPL: 0
# 
# AMZN: 1
# 
# MSFT: 2
# 
# NVDA: 3
# 
# TSLA: 4

# <h3><b>COMPANY??

# In[9]:


inputdf['Ticker']=4

# <h4>XGBOOST classification

# In[10]:


import pickle
with open("general data models/XGB_close_classifier.pkl", 'rb') as file:
    GXGBc_classifier = pickle.load(file)

print("XGB  model loaded from 'XGB_close_classifier.pkl'.")

# In[11]:


probabilities = GXGBc_classifier.predict_proba(inputdf)

prob = probabilities[0,1]
print(prob)

# In[12]:


import pickle
with open("general data models/XGB_trade_classifier.pkl", 'rb') as file:
    GXGBt_classifier = pickle.load(file)

print("XGB model loaded from 'XGB_trade_classifier.pkl'.")

# In[13]:


probabilities = GXGBt_classifier.predict_proba(inputdf)

prob = probabilities[0,1]
print(prob)

# <h3>XGBOOST regression

# In[14]:


import pickle
with open("general data models/xgb_regressor_trade.pkl", 'rb') as file:
    GXGBt_regression = pickle.load(file)

print("XGB model loaded from 'xgb_regressor_trade.pkl'.")

# In[15]:


probabilities = GXGBt_regression.predict(inputdf)

prob = probabilities[0]
print(prob)

# In[16]:


import pickle
with open("general data models/xgb_regressor_close.pkl", 'rb') as file:
    GXGBc_regression = pickle.load(file)

print("XGB model loaded from 'xgb_regressor_close.pkl'.")

# In[17]:


probabilities = GXGBc_regression.predict(inputdf)

prob = probabilities[0]
print(prob)
