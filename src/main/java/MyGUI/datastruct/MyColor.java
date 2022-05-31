package MyGUI.datastruct;

public class MyColor {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Change color of String using ANSI
     *
     * @param s String
     * @param color Text color
     * @return Colored string
     */
    public static String colorize(String s, String color) {
        String ansi;
        switch (color.toLowerCase()) {
            case "black":
                ansi = ANSI_BLACK;
                break;
            case "red":
                ansi = ANSI_RED;
                break;
            case "green":
                ansi = ANSI_GREEN;
                break;
            case "yellow":
                ansi = ANSI_YELLOW;
                break;
            case "blue":
                ansi = ANSI_BLUE;
                break;
            case "purple":
                ansi = ANSI_PURPLE;
                break;
            case "cyan":
                ansi = ANSI_CYAN;
                break;
            case "white":
                ansi = ANSI_WHITE;
                break;
            default:
                ansi = ANSI_RESET;
        }
        return (ansi + s + ANSI_RESET);
    }

    /**
     * Display available colors represented by ANSI
     */
    public static void displayColorAvailable() {
        System.out.println("Black, Red, Green, Yellow, Blue, Purple, Cyan, White");
    }
}
