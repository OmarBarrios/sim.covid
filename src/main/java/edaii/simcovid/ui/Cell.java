package edaii.simcovid.ui;

import edaii.simcovid.utils.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Cell extends JComponent {

    public static final int STATE_NOT_INFECTED = 0;
    public static final int STATE_INFECTED = 1;
    public static final int STATE_IMMUNE = 2;
    public static final int STATE_SURROUNDED = 3;
    public static final int STATE_MASKED = 4;
    public static final int STATE_DEAD = 5;

    static Map<Integer, Image> imageDictionary;
    static Map<Integer, Color> colorDictionary;

    int state = STATE_NOT_INFECTED;

    final int id;

    BufferedImage buffer;
    boolean invalidateBuffer;

    public Cell(int id) {
        this.id = id;
        setDoubleBuffered(true);
        if (imageDictionary == null) {
            imageDictionary = new HashMap<>();
            imageDictionary.put(STATE_NOT_INFECTED, ImageHelper.fromResource("smiling.png"));
            imageDictionary.put(STATE_IMMUNE, ImageHelper.fromResource("sunglasses.png"));
            imageDictionary.put(STATE_INFECTED, ImageHelper.fromResource("infected.png"));
            imageDictionary.put(STATE_SURROUNDED, ImageHelper.fromResource("surrounded.png"));
            imageDictionary.put(STATE_MASKED, ImageHelper.fromResource("masked.png"));
            imageDictionary.put(STATE_DEAD, ImageHelper.fromResource("dead.png"));

            colorDictionary = new HashMap<>();
            colorDictionary.put(STATE_NOT_INFECTED, Color.white);
            colorDictionary.put(STATE_INFECTED, Color.decode("#f50e0e"));
            colorDictionary.put(STATE_IMMUNE, Color.blue);
            colorDictionary.put(STATE_SURROUNDED, Color.decode("#f78d36"));
            colorDictionary.put(STATE_MASKED, Color.decode("#4daafc"));
            colorDictionary.put(STATE_DEAD, Color.decode("#000000"));
        }

        buffer = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        invalidateBuffer = true;
    }

    public void setState(int value) {
        if (!imageDictionary.containsKey(value))
            throw new IllegalArgumentException("Illegal cell state");
        if (value == state) return;
        state = value;
        invalidateBuffer = true;

        repaint();
    }

    @Override
    public synchronized void paint(Graphics g) {
        super.paint(g);
        ensureBufferedIsPainted();
        drawBuffer(g);
    }

    private void drawBuffer(Graphics g) {
        g.drawImage(buffer,
                0, 0, getWidth(), getHeight(),
                0, 0, buffer.getWidth(), buffer.getHeight(),
                this);
    }

    private void ensureBufferedIsPainted() {
        if(!invalidateBuffer) return;

        Graphics g = buffer.createGraphics();
        drawBackground(g, buffer.getWidth(), buffer.getHeight());
        drawStateImage(g, buffer.getWidth(), buffer.getHeight());
        drawBorderState(g, buffer.getWidth(), buffer.getHeight());
        g.dispose();
        invalidateBuffer = false;
    }

    private void drawBackground(Graphics g, int width, int height) {
        g.setColor(colorDictionary.get(state));
        g.fillRect(0, 0, width, height);
    }

    private void drawBorderState(Graphics g, int width, int height) {
        g.setColor(Color.white);
        g.drawRect(0, 0, width, height);
    }

    private void drawStateImage(Graphics g, int width, int height) {
        final Image image = imageDictionary.get(this.state);
        final Point imageSource = ImageHelper.centerImage(image, new Dimension(width, height));

        if (imageFitsInControl(image)) {
            g.drawImage(image, imageSource.x, imageSource.y, this);
        } else {
            final int margin = (int) (width * 0.10);
            final int sx2 = image.getWidth(this);
            final int sy2 = image.getHeight(this);
            g.drawImage(image,
                    margin, margin, width - margin, height - margin,
                    0, 0, sx2, sy2, this);
        }

    }

    private boolean imageFitsInControl(Image image) {
        return image.getWidth(this) <= this.getWidth()
                && image.getHeight(this) <= this.getHeight();
    }

}
