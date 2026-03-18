package utils;

import java.awt.Font;

public class AppFont {

    private static Font titles;
    private static Font base;

    static {
        try {
            titles = Font.createFont(
                    Font.TRUETYPE_FONT,
                    AppFont.class.getResourceAsStream("/fonts/Merriweather-VariableFont_opsz,wdth,wght.ttf"));

            base = Font.createFont(
                    Font.TRUETYPE_FONT,
                    AppFont.class.getResourceAsStream("/fonts/Inter_18pt-Regular.ttf"));
        } catch (Exception e) {
            titles = new Font("Times New Roman", Font.PLAIN, 14);
            base = new Font("Arial", Font.PLAIN, 14);
        }
    }

    public static Font title() {
        return titles.deriveFont(22f);
    }

    public static Font large() {
        return base.deriveFont(16f);
    }

    public static Font medium() {
        return base.deriveFont(14f);
    }

    public static Font small() {
        return base.deriveFont(12f);
    }

}