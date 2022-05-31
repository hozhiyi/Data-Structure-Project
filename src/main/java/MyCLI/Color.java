package MyCLI;

public class Color {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static String colorize(String s, String color) {
        String str = "";
        switch (color) {
            case "black":
                str += ANSI_BLACK;
                break;
            case "red":
                str += ANSI_RED;
                break;
            case "green":
                str += ANSI_GREEN;
                break;
            case "yellow":
                str += ANSI_YELLOW;
                break;
            case "blue":
                str += ANSI_BLUE;
                break;
            case "purple":
                str += ANSI_PURPLE;
                break;
            case "cyan":
                str += ANSI_CYAN;
                break;
            case "white":
                str += ANSI_WHITE;
                break;
            default:
                str += ANSI_RESET;
                break;
        }
        return str + (s + ANSI_RESET);
    }

    public static String showColorAvailable() {
        return "Black, Red, Green, Yellow, Blue, Purple, Cyan, White";
    }
}
