# Sound Effects Setup Guide

## Overview

The HappyShop application now includes three types of sound effects to enhance user experience:

1. **Button Click Sound** - For general UI interactions
2. **Checkout Sound** - For important payment actions
3. **Notification Sound** - For alerts and warnings

## How to Add Sound Files

### Steps:

1. **Prepare Your Sound Files**
   - Find or create 3 short MP3 files (recommended: 0.5-2 seconds each)
   - Keep files small for quick playback

2. **Copy Files to Resources Folder**
   Place your MP3 files in: `src/main/resources/`
   
   Required file names:
   - `button_click.mp3` (general buttons)
   - `checkout_sound.mp3` (checkout/payment)
   - `notification_sound.mp3` (alerts/warnings)

3. **File Location Should Be:**
   ```
   HappyShop/
   └── src/
       └── main/
           └── resources/
               ├── background_music.mp3
               ├── button_click.mp3      ← Add here
               ├── checkout_sound.mp3    ← Add here
               ├── notification_sound.mp3 ← Add here
               ├── imageHolder.jpg
               └── ...
   ```

## Sound Effect Usage

### 1. Button Click Sound (40% volume)
Used for general UI interactions:
- Search button
- Add to Trolley button
- Cancel button
- OK & Close button
- Music toggle button
- Payment cancel button
- Notification OK button
- Quantity +/- buttons

### 2. Checkout Sound (50% volume)
Used for important payment actions:
- Check Out button (when clicked)
- Confirm Payment button (in payment dialog)

### 3. Notification Sound (60% volume)
Used for alerts and warnings:
- Stock shortage notifications
- Minimum payment errors
- Excessive quantity warnings
- Product removal alerts

## Volume Levels

- **Button Click**: 40% - Subtle, non-intrusive
- **Checkout**: 50% - More prominent for important actions
- **Notification**: 60% - Most noticeable for alerts

## Where to Find Free Sounds

### Recommended Sites:
- **Freesound.org** - Large library of CC-licensed sounds
- **Zapsplat.com** - Free sound effects
- **Mixkit.co** - Free sound effects and music
- **Soundbible.com** - Public domain sounds

### Suggested Search Terms:
- Button Click: "click", "button", "ui click", "soft click"
- Checkout: "cash register", "cha-ching", "coin", "success"
- Notification: "notification", "alert", "ding", "bell"

## Technical Details

### Implementation:
- Uses JavaFX `AudioClip` class for short sound effects
- Better than `MediaPlayer` for quick, repeated sounds
- Non-blocking playback (doesn't freeze UI)
- Managed by `SoundManager` utility class

### Features:
- Graceful fallback if sound files missing
- Sounds can be enabled/disabled programmatically
- Independent volume control per sound type
- Efficient resource usage (sounds loaded once)

## Troubleshooting

### Sound Not Playing?
1. Check file names are **exactly** as specified (case-sensitive)
2. Ensure files are in `src/main/resources/` directory
3. Verify files are valid MP3 format
4. Check console for "sound loaded" messages

### Application Works Without Sounds
Yes! If sound files are not found, the application will:
- Print messages to console indicating missing files
- Continue working normally
- Simply skip sound playback

## Example Console Output

```
Week 12: Background music loaded and playing automatically
Week 12: Button click sound loaded
Week 12: Checkout sound loaded
Week 12: Notification sound loaded
```

Enjoy your enhanced audio experience! 🔊🎵

