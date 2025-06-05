# Project Management Application

## Email Configuration

This application sends emails with generated documents. To set up email functionality, you need to configure environment variables:

### Setting Environment Variables

#### On Windows:
1. Open Command Prompt as Administrator
2. Set environment variables:
   ```
   setx APP_EMAIL "your.email@gmail.com"
   setx APP_EMAIL_PASSWORD "your-app-password"
   ```
3. Restart your IDE or terminal for changes to take effect

#### On macOS/Linux:
1. Add the following to your `~/.bash_profile` or `~/.zshrc`:
   ```
   export APP_EMAIL="your.email@gmail.com"
   export APP_EMAIL_PASSWORD="your-app-password"
   ```
2. Run `source ~/.bash_profile` or `source ~/.zshrc`
3. Restart your IDE or terminal for changes to take effect

### For Gmail Users:
- You need to use an "App Password" if you have 2-factor authentication enabled
- To generate an App Password:
  1. Go to your Google Account settings
  2. Navigate to Security > App passwords
  3. Select "Mail" and your device
  4. Use the generated 16-character password as APP_EMAIL_PASSWORD

### For Development:
You can create a `.env` file in the project root (this file is ignored by git):
```
APP_EMAIL=your.email@gmail.com
APP_EMAIL_PASSWORD=your-app-password
```

Then load it in your IDE's run configuration or use a library like dotenv-java to load it at runtime.