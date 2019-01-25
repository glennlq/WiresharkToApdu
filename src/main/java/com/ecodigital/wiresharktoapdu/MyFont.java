package com.ecodigital.wiresharktoapdu;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class MyFont {

    public static final Font REGULAR = loadFont("PTM55FT.ttf").deriveFont(Font.PLAIN);
    

    private static Font loadFont(String resourceName) {
        try (InputStream inputStream = MyFont.class.getResourceAsStream("/fonts/" + resourceName)) {
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException("Could not load " + resourceName, e);
        }
    }
}
