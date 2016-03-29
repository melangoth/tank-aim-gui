import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by develrage
 */
public class AnalyserImageTools {
    final static Logger log = Logger.getLogger(AnalyserImageTools.class);
    protected BufferedImage image = null;
    protected int imageWidth;
    protected int imageHeight;
    protected String[] imagePool = new String[]{"images/img6.png", "images/img7.png", "images/img8.png"};
    protected int imagePoolPointer = 0;

    public synchronized void loadImage(BufferedImage image) {
        log.trace("loadImage(BufferedImage image)");
        this.image = image;
        this.imageWidth = this.image.getWidth();
        this.imageHeight = this.image.getHeight();
    }

    public synchronized void loadImage(Image image) {
        log.trace("loadImage(Image image)");
        loadImage((BufferedImage) image);
    }

    public synchronized void loadImage(String path) {
        log.trace(String.format("loadImage(path=%s)", path));
        File f = new File(path);
        try {
            loadImage(ImageIO.read(f));
        } catch (IOException e) {
            log.error(String.format("Failed to ImageIO.read(%s)", f.getPath()));
        }
    }

    public synchronized void loadImagePool() {
        loadImagePool(false);
    }

    public synchronized void loadImagePool(boolean rotate) {
        log.trace(String.format("loadImagePool(rotate=%s)", rotate));
        if (rotate) {
            imagePoolPointer++;
            if (imagePoolPointer >= imagePool.length) imagePoolPointer = 0;
        }
        loadImage(imagePool[imagePoolPointer]);
    }

    public synchronized BufferedImage getImage() {
        log.trace("getImage()");

        if (image == null) {
            log.warn("Image not set yet.");
        }

        return image;
    }
}