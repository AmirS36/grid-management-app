package resources;

public class Resources {

    static String currentTheme;

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(String currentTheme) {
        Resources.currentTheme = currentTheme;
    }
}
