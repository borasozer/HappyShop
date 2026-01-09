# Dark Theme Feature

## Overview

The Customer Client now supports a dark theme option, allowing users to switch between light and dark modes for improved visual comfort and personal preference.

## Features

### Theme Toggle Button
- **Location:** Top-left corner of Customer Client window
- **Icon:** 
  - 🌙 (Moon) - Click to switch to Dark Theme
  - ☀️ (Sun) - Click to switch back to Light Theme
- **Default:** Light theme (current classic look)

### Color Schemes

#### Light Theme (Default)
- Background: Light green (#90EE90)
- Labels: Light blue backgrounds with black text
- Buttons: Standard JavaFX styling
- Text fields: White backgrounds
- Receipt page: Light yellow

#### Dark Theme
- Background: Dark gray (#2b2b2b)
- Labels: Dark backgrounds (#424242) with light gray text (#e0e0e0)
- Title labels: Purple accent (#bb86fc)
- Buttons: Dark gray (#424242) with light text
- Text fields: Dark backgrounds (#3c3c3c) with light text
- Receipt page: Dark gray (#3c3c3c)

## Usage

1. **Open Customer Client**
2. **Look at top-left corner** - you'll see a 🌙 button
3. **Click the button** to switch to dark theme
4. **Click again (☀️)** to switch back to light theme

## Technical Implementation

### UIStyle Class Extensions
New dark theme constants added:
- `darkRootStyle` - Dark background for main container
- `darkLabelTitleStyle` - Purple titles on dark background
- `darkLabelStyle` - Light text on dark backgrounds
- `darkButtonStyle` - Dark buttons with light text
- `darkTextFieldStyle` - Dark input fields
- `darkLabelMulLineStyle` - Multi-line labels for dark theme
- `darkRootStyleYellow` - Dark receipt background

### Dynamic Style Switching
The `updateNodeStyles()` method recursively applies theme styles to all UI elements:
- Labels (titles and regular)
- Text fields
- Buttons (excluding special styled buttons)
- Text areas
- List views
- All container elements

### Preserved Elements
Some UI elements maintain their original styling regardless of theme:
- Theme toggle button (transparent background)
- Music toggle button (transparent background)
- Payment dialog buttons (green/red colors preserved)
- Alert buttons (keep their original colors for clarity)

## Benefits

### User Experience
- **Reduced Eye Strain:** Dark theme reduces blue light exposure
- **Better Night Usage:** Comfortable for late-night shopping
- **Personal Preference:** Users can choose their preferred look
- **Modern Look:** Dark themes are popular in modern applications

### Accessibility
- High contrast in both themes
- Clear text visibility
- Consistent spacing and layout
- Easy toggle mechanism

## Sound Effects

Theme switching includes a button click sound effect for feedback, confirming the user's action.

## Future Enhancements

Possible improvements:
- Remember user's theme preference across sessions
- Auto-switch based on system time (dark at night)
- More theme options (high contrast, colorful, etc.)
- Extend to other client windows (Picker, Warehouse, etc.)

---

**Note:** Theme preference is session-based and resets to light theme when the application restarts.

Enjoy your personalized shopping experience! 🌙☀️


