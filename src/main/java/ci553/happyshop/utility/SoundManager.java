package ci553.happyshop.utility;

import javafx.scene.media.AudioClip;

/**
 * Week 12: Sound Manager for UI sound effects
 * Manages different sound effects for user actions
 * Uses AudioClip for short sound effects (better than MediaPlayer for quick sounds)
 */
public class SoundManager {
    
    // Week 12: Sound effect types
    private static AudioClip buttonClickSound;
    private static AudioClip checkoutSound;
    private static AudioClip notificationSound;
    
    // Week 12: Control flag to enable/disable sounds
    private static boolean soundsEnabled = true;
    
    /**
     * Week 12: Initialize all sound effects
     * Loads sound files from resources
     */
    public static void initialize() {
        try {
            // Week 12: Load button click sound
            String buttonClickPath = SoundManager.class.getResource("/button_click.mp3").toExternalForm();
            buttonClickSound = new AudioClip(buttonClickPath);
            buttonClickSound.setVolume(0.4); // 40% volume
            System.out.println("Week 12: Button click sound loaded");
        } catch (Exception e) {
            System.out.println("Week 12: Button click sound not found (button_click.mp3)");
        }
        
        try {
            // Week 12: Load checkout/payment sound
            String checkoutPath = SoundManager.class.getResource("/checkout_sound.mp3").toExternalForm();
            checkoutSound = new AudioClip(checkoutPath);
            checkoutSound.setVolume(0.5); // 50% volume (more prominent)
            System.out.println("Week 12: Checkout sound loaded");
        } catch (Exception e) {
            System.out.println("Week 12: Checkout sound not found (checkout_sound.mp3)");
        }
        
        try {
            // Week 12: Load notification sound
            String notificationPath = SoundManager.class.getResource("/notification_sound.mp3").toExternalForm();
            notificationSound = new AudioClip(notificationPath);
            notificationSound.setVolume(0.6); // 60% volume (alerts should be noticeable)
            System.out.println("Week 12: Notification sound loaded");
        } catch (Exception e) {
            System.out.println("Week 12: Notification sound not found (notification_sound.mp3)");
        }
    }
    
    /**
     * Week 12: Play button click sound
     * Used for general UI buttons (Search, Add to Trolley, Cancel, etc.)
     */
    public static void playButtonClick() {
        if (soundsEnabled && buttonClickSound != null) {
            buttonClickSound.play();
        }
    }
    
    /**
     * Week 12: Play checkout sound
     * Used for important actions (Checkout, Payment confirmation)
     */
    public static void playCheckout() {
        if (soundsEnabled && checkoutSound != null) {
            checkoutSound.play();
        }
    }
    
    /**
     * Week 12: Play notification sound
     * Used for alerts and notifications (errors, warnings, stock shortage)
     */
    public static void playNotification() {
        if (soundsEnabled && notificationSound != null) {
            notificationSound.play();
        }
    }
    
    /**
     * Week 12: Enable or disable all sound effects
     * @param enabled true to enable sounds, false to disable
     */
    public static void setSoundsEnabled(boolean enabled) {
        soundsEnabled = enabled;
        System.out.println("Week 12: Sound effects " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Week 12: Check if sounds are enabled
     * @return true if sounds are enabled
     */
    public static boolean isSoundsEnabled() {
        return soundsEnabled;
    }
}

