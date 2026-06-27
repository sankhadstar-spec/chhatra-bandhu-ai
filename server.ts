import express from 'express';
import path from 'path';
import fs from 'fs';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = 3000;

// Serve static assets from the public folder
app.use(express.static(path.join(__dirname, 'public')));

// Fallback APK route if someone visits /app-debug.apk or /download
app.get('/download', (req, res) => {
  const apkPath = path.join(__dirname, 'public', 'app-debug.apk');
  if (fs.existsSync(apkPath)) {
    res.download(apkPath, 'chhatra-bandhu-ai.apk');
  } else {
    // Attempt fallback from the build outputs
    const fallbackPath = '/.build-outputs/app-debug.apk';
    if (fs.existsSync(fallbackPath)) {
      res.download(fallbackPath, 'chhatra-bandhu-ai.apk');
    } else {
      res.status(404).send('The Chhatra Bandhu AI Android app is still compiling or not found. Please try again in a few moments!');
    }
  }
});

// Always serve index.html for any other route
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server is running on port ${PORT}`);
});
