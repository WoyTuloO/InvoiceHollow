package com.woytuloo.accountingapp.handlers;

import com.woytuloo.accountingapp.main.AppFrame;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class FontHandler {
    public static Font montserrat18 = null;
    public static Font montserrat12 = null;
    public static Font montserrat14 = null;
    public static Font montserrat24 = null;
    public static Font montserrat48 = null;


    static{

        InputStream fontStream = AppFrame.class.getResourceAsStream("/fonts/Montserrat-Regular.ttf");
        if (fontStream == null) {
            throw new RuntimeException("Nie znaleziono czcionki!");
        }

        Font montserratBase = null;
        try {
            montserratBase = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        montserrat18 = montserratBase.deriveFont(18f);
        montserrat12 = montserratBase.deriveFont(12f);
        montserrat14 = montserratBase.deriveFont(14f);
        montserrat24 = montserratBase.deriveFont(24f);
        montserrat48 = montserratBase.deriveFont(48f);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(montserrat18);
        ge.registerFont(montserrat12);
        ge.registerFont(montserrat14);
        ge.registerFont(montserrat24);
        ge.registerFont(montserrat48);

    }
}
