package ci553.happyshop.utility;

/**
 * UIStyle is a centralized Java final class that holds all JavaFX UI-related style and size constants
 * used across all client views in the system.
 *
 * These values are grouped here rather than being hardcoded throughout the codebase:
 * - improves maintainability, ensures style consistency,
 * - avoids hardcoded values scattered across the codebase.
 *
 * Example usages:
 * - UIStyle.HistoryWinHeight for setting the height of the order history window
 * - UIStyle.labelStyle for applying consistent styling to labels
 *
 * Design rationale:
 * - Declared as a final class: prevents inheritance and misuse
 * - Private constructor: prevents instantiation (this is a static-only utility class)
 * - Holds only static constants: ensures minimal memory usage and clean syntax
 *
 * Why a Record is NOT appropriate:
 * - Records are intended for immutable instance data (e.g., DTOs), not static constants
 * - This class has no record components — everything is static
 * - We're using this as a utility container, not a data model
 *
 * Reminder:
 * Just because a class has no behaviour and only data does NOT mean it should be a record.
 * If all members are static constants, use a final utility class like this one.
 *
 * @see Week 3: Beyond Classes - Records vs Utility Classes
 * @see Week 5: Lab Activities - Task 5
 */

public final class UIStyle {
    
    // Week 5: Private constructor prevents instantiation (utility class pattern)
    private UIStyle() {
        throw new UnsupportedOperationException("UIStyle is a utility class and cannot be instantiated");
    }

    // Week 6: Grid layout - all windows aligned in 2 rows
    // Row 1: Customer (left) + Picker (right) - same height
    // Row 2: Warehouse (left) + OrderTracker (middle) + Exit (right) - same height
    
    public static final int customerWinWidth = 550;  // Week 6: Wider for better UX; Week 11: Reduced for compact 9-window layout
    public static final int customerWinHeight = 250; // Week 6: Taller, matches picker; Week 11: Reduced for compact multi-window layout
    public static final int removeProNotifierWinWidth = customerWinWidth/2 +160;
    public static final int removeProNotifierWinHeight = 230;

    public static final int pickerWinWidth = 550;   // Week 6: Matches customer width; Week 11: Aligned with customer for uniform layout (600px)
    public static final int pickerWinHeight = 250;  // Week 6: Same as customer for aligned row; Week 11: Reduced 30% for compact multi-window layout (320 → 224)

    public static final int warehouseWinWidth = 550; // Week 6: Same as customer width; Week 11: Reduced to 600 for compact layout
    public static final int warehouseWinHeight = 250; // Week 6: Uniform height for row 2; Week 11: Reduced for compact layout

    public static final int trackerWinWidth = 280;   // Week 6: Wider for better visibility
    public static final int trackerWinHeight = 250;  // Week 6: Same as warehouse

    public static final int EmergencyExitWinWidth = 180; // Week 6: Wider to match row height; Week 11: Matched with Tracker for uniform layout
    public static final int EmergencyExitWinHeight = 120; // Week 6: Same as warehouse/tracker
    
    // Warehouse dependent windows
    public static final int AlertSimWinWidth = 300;
    public static final int AlertSimWinHeight = 170;
    public static final int HistoryWinWidth = 300;
    public static final int HistoryWinHeight = 140;

    public static final String labelTitleStyle ="-fx-font-weight: bold; " +
            "-fx-font-size: 16px; -fx-text-fill: purple;";

    public static final String labelStyle = "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: black; " +
            "-fx-background-color: lightblue;";

    public static final String comboBoxStyle ="-fx-font-weight: bold; " +
            "-fx-font-size: 14px;";

    public static final String buttonStyle= "-fx-font-size: 15";

    public static final String rootStyle = "-fx-padding: 8px; " +
            "-fx-background-color: lightgreen";

    public static final String rootStyleBlue = "-fx-padding: 8px; " +
            "-fx-background-color: lightblue";

    public static final String rootStyleGray = "-fx-padding: 8px; " +
            "-fx-background-color: lightgray";

    public static final String rootStyleWarehouse = "-fx-padding: 8px; " +
            "-fx-background-color: lightpink";

    public static final String rootStyleYellow = "-fx-padding: 8px; " +
            "-fx-background-color: lightyellow";

    public static final String textFiledStyle = "-fx-font-size: 16";

    public static final String labelMulLineStyle= "-fx-font-size: 16px; " +
            "-fx-background-color: lightpink";

    public static final String listViewStyle = "-fx-border-color: #ccc; " +
            "-fx-border-width: 1px; -fx-background-color: white; -fx-font-size: 14px;";

    public static final String manageStockChildStyle = "-fx-background-color: lightgrey; " +
            "-fx-border-color: lightgrey; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 5px;";

    public static final String manageStockChildStyle1 = "-fx-background-color: lightyellow; " +
            "-fx-border-color: lightyellow; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 5px;";

    public static final String greenFillBtnStyle = "-fx-background-color: green; " +
            "-fx-text-fill: white; -fx-font-size: 14px;";
    public static final String redFillBtnStyle ="-fx-background-color: red; " +
            "-fx-text-fill: white; -fx-font-size: 14px; ";

    public static final String grayFillBtnStyle = "-fx-background-color: gray; " +
            "-fx-text-fill: white; -fx-font-size: 14px; ";

    public static final String blueFillBtnStyle ="-fx-background-color: blue; " +
            "-fx-text-fill: white; -fx-font-size: 14px;";

    public static final String alertBtnStyle ="-fx-background-color: green; " +
            "-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;";

    public static final String alertTitleLabelStyle = "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: red; " + "-fx-background-color: lightblue;";

    public static final String alertContentTextAreaStyle = "-fx-font-size: 14px;" +
            "-fx-font-weight: normal;-fx-control-inner-background: lightyellow; -fx-text-fill: darkblue;";

    public static final String alertContentUserActionStyle = "-fx-font-size: 14px;" +
            "-fx-font-weight: normal; -fx-text-fill: green;";

    // Week 12: Dark Theme Styles for Customer Interface
    public static final String darkRootStyle = "-fx-padding: 8px; " +
            "-fx-background-color: #2b2b2b";
    
    public static final String darkLabelTitleStyle = "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; -fx-text-fill: #bb86fc;";
    
    public static final String darkLabelStyle = "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #e0e0e0; " +
            "-fx-background-color: #424242;";
    
    public static final String darkButtonStyle = "-fx-font-size: 15; " +
            "-fx-background-color: #424242; " +
            "-fx-text-fill: #e0e0e0;";
    
    public static final String darkTextFieldStyle = "-fx-font-size: 16; " +
            "-fx-background-color: #3c3c3c; " +
            "-fx-text-fill: #e0e0e0; " +
            "-fx-control-inner-background: #3c3c3c;";
    
    public static final String darkLabelMulLineStyle = "-fx-font-size: 16px; " +
            "-fx-background-color: #424242; " +
            "-fx-text-fill: #e0e0e0;";
    
    public static final String darkRootStyleYellow = "-fx-padding: 8px; " +
            "-fx-background-color: #3c3c3c";

}
