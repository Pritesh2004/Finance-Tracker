import joblib
import pickle
import os

# List of model paths
model_files = {
    'XGB_close_classifier': "general_data_models/XGB_close_classifier.pkl",
    'XGB_trade_classifier': "general_data_models/XGB_trade_classifier.pkl",
    'xgb_regressor_trade': "general_data_models/xgb_regressor_trade.pkl",
    'xgb_regressor_close': "general_data_models/xgb_regressor_close.pkl",
}

# Directory to save compressed models
compressed_dir = "compressed_models"
os.makedirs(compressed_dir, exist_ok=True)

# Compress models
for model_name, model_path in model_files.items():
    if os.path.exists(model_path):
        # Load original model
        with open(model_path, 'rb') as f:
            model = pickle.load(f)
        
        # Save model using joblib with compression
        compressed_model_path = os.path.join(compressed_dir, f"{model_name}_compressed.pkl")
        joblib.dump(model, compressed_model_path, compress=('zlib', 3))  # 'zlib' or 'bz2', compression level from 0 to 9
        
        print(f"Compressed and saved {model_name} to {compressed_model_path}")
    else:
        print(f"Model file {model_path} not found.")
