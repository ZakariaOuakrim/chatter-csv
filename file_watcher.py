import os
import time
import sys
from pathlib import Path

class FileWatcher:
    def __init__(self, directory):
        self.directory = directory
        self.files = {}  # hadi list katstocker lik les fichiers o lwe9t
        
    def scan_directory(self):
        # Get current files
        current_files = {}
        try:
            for entry in os.scandir(self.directory):
                if entry.is_file():
                    current_files[entry.name] = entry.stat().st_mtime
        except FileNotFoundError:
            print(f"Directory {self.directory} not found. Creating it...")
            os.makedirs(self.directory, exist_ok=True)
            return
            
        # Check for new and modified files
        for filename, mtime in current_files.items():
            if filename not in self.files:
                print(f"File created: {filename}")
            elif mtime != self.files[filename]:
                print(f"File modified: {filename}")
                
        # Check for deleted files
        for filename in list(self.files.keys()):
            if filename not in current_files:
                print(f"File deleted: {filename}")
                
        # Update our file list
        self.files = current_files
        
    def watch(self, interval=1.0):
        print(f"Watching directory: {self.directory}")
        print("Press Ctrl+C to stop")
        
        # Initial scan
        self.scan_directory()
        
        try:
            while True:
                time.sleep(interval)
                self.scan_directory()
        except KeyboardInterrupt:
            print("\nWatcher stopped.")

if __name__ == "__main__":
    # Use command line argument for directory or default to "./watched_folder"
    watch_dir = sys.argv[1] if len(sys.argv) > 1 else "./watched_folder"
    
    # Create the directory if it doesn't exist
    Path(watch_dir).mkdir(exist_ok=True)
    
    watcher = FileWatcher(watch_dir)
    watcher.watch()