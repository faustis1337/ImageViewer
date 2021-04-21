package dk.easv;

import javafx.scene.image.Image;

import java.awt.*;

public class ColorCounter {
    public static int[] colors(Image img) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        int r = 0;
        int g = 0;
        int b = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(img.getPixelReader().getArgb(x, y));

                if (c.getRed() > c.getGreen() && c.getRed() > c.getBlue()) {
                    r++;
                } else if (c.getGreen() > c.getRed() && c.getGreen() > c.getBlue()) {
                    g++;
                } else if (c.getBlue() > c.getGreen() && c.getBlue() > c.getRed()) {
                    b++;
                }
            }
        }
        return new int[]{r, g, b};
    }
}
