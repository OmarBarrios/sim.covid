package edaii.simcovid.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class ImageHelper {

    public static Image fromResource(String resourceName) {
        try (var in = streamOfResource(resourceName)) {
            return ImageIO.read(in);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot load resource " + resourceName);
        }
    }

    public static Point centerImage(Image image, Dimension dim) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);

        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        return new Point(x, y);
    }

    private static InputStream streamOfResource(String resourceName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(resourceName);
    }
}
