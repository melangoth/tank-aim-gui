import java.awt.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class MonitorLine {
    private final int padding;
    private final int baseline;
    private Rectangle rect;
    private String text = "";

    public MonitorLine(Rectangle rect, int padding, int baseline) {
        this.rect = rect;
        this.padding = padding;
        this.baseline = baseline;
    }

    public int getX() {
        return rect.getBounds().x;
    }

    public int getY() {
        return rect.getBounds().y;
    }

    public int getWidth() {
        return rect.getBounds().width;
    }

    public int getHeight() {
        return rect.getBounds().height;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void paintSprite(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString(text,
                getX() + padding,
                getY() + getHeight() - baseline - padding);
        g.drawRect(getX(), getY(), getWidth(), getHeight());
    }
}
