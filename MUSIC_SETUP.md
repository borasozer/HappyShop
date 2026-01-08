# Background Music Setup

## How to Add Background Music to Customer Client

The customer interface now supports background music for an enhanced shopping experience!

### Steps to Enable Music:

1. **Find or Create an MP3 File**
   - Any MP3 audio file will work
   - Recommended: Calm, pleasant background music
   - Suggested length: 2-5 minutes (it will loop automatically)

2. **Add the File to Resources**
   - Copy your MP3 file
   - Paste it into: `src/main/resources/`
   - **Rename it to:** `background_music.mp3`

3. **File Location Should Be:**
   ```
   HappyShop/
   └── src/
       └── main/
           └── resources/
               ├── imageHolder.jpg
               ├── ShutDown.jpg
               ├── WarehouseImageHolder.jpg
               └── background_music.mp3  ← Add here
   ```

4. **Run the Application**
   - The music will be loaded and **starts playing automatically**
   - A 🔊 button appears in the top-right corner
   - Click to toggle music on/off (pauses/resumes)
   - Music plays at 30% volume by default

### Features:

- **Toggle Button:** 🔊 (playing) / 🔇 (muted)
- **Auto-Loop:** Music repeats indefinitely
- **Volume:** Set to 30% for comfortable background listening
- **Graceful Fallback:** If no music file found, button is disabled

### Where to Find Free Music:

- YouTube Audio Library (royalty-free)
- FreeMusicArchive.org
- Incompetech.com (by Kevin MacLeod)
- Bensound.com

### Note:

If you don't add a music file, the application will still work normally. The music button will be disabled and slightly faded out.

Enjoy your enhanced shopping experience! 🎵🛒

