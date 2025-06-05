import pandas as pd
import requests
import json
import os
import sys
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Load the Excel file
def load_excel(file_path):
    try:
        df = pd.read_excel(file_path)
        return df
    except Exception as e:
        print(f"Error loading Excel file: {e}")
        return None

# Function to query OpenRouter with DeepSeek-R1
def query_openrouter(prompt, api_key):
    url = "https://openrouter.ai/api/v1/chat/completions"
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    payload = {
        "model": "deepseek/deepseek-r1-distill-qwen-32b:free",  
        "messages": [
            {"role": "user", "content": prompt}
        ]
    }
    
    try:
        response = requests.post(url, headers=headers, data=json.dumps(payload))
        return response.json()
    except Exception as e:
        print(f"Error calling OpenRouter API: {e}")
        return {"error": str(e)}

# Process a single question
def process_question(question, df):
    # Get API key from environment variable
    api_key = os.environ.get("OPENROUTER_API_KEY")
    if not api_key:
        print("Error: OPENROUTER_API_KEY not found in .env file")
        return "Error: API key not found"
    
    # Convert the dataframe to a string representation for context
    data_context = df.to_string()
    
    # Create a prompt with data context but instruct the AI not to return the raw data
    prompt = f"""I have an Excel file with the following data:
    
{data_context}

My question is: {question}

IMPORTANT: Do NOT return the raw data in your response. Instead, analyze the data and provide a direct answer to my question without showing the original data."""
    
    # Query OpenRouter
    response = query_openrouter(prompt, api_key)
    
    # Extract and return the response
    try:
        answer = response['choices'][0]['message']['content']
        print("Answer:")
        print(answer)
        return answer
    except KeyError as e:
        print(f"Error in response format: {e}")
        print(response)
        return f"Error processing your question: {str(e)}"

# Main function
def main():
    # Check if a command-line argument was provided
    if len(sys.argv) > 1:
        # Get the question from command-line argument
        question = sys.argv[1]
        
        # Load the Excel file
        script_dir = os.path.dirname(os.path.abspath(__file__))
        excel_path = os.path.join(script_dir, "Tracker.xlsx")
        df = load_excel(excel_path)
        
        if df is not None:
            # Process the question and print the answer
            process_question(question, df)
        else:
            print("Answer:")
            print("Error: Could not load Excel file")
    else:
        # Interactive mode
        # Load the Excel file
        script_dir = os.path.dirname(os.path.abspath(__file__))
        excel_path = os.path.join(script_dir, "Tracker.xlsx")
        df = load_excel(excel_path)
        
        if df is not None:
            print(f"Successfully loaded Excel file with {len(df)} rows and {len(df.columns)} columns")
            print("Ready to answer questions about your data. Type 'exit' to quit.")
            
            # Interactive loop for questions
            while True:
                question = input("\nYour question: ")
                if question.lower() == 'exit':
                    break
                
                process_question(question, df)
        else:
            print("Error: Could not load Excel file")

if __name__ == "__main__":
    main()


