import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by develrage
 */
public class MenuItem {
    private final int padding;
    private final int baseline;
    private Rectangle rect;
    private String text = "";
    private Color color = Color.WHITE;

    public MenuItem(Rectangle rect, int padding, int baseline) {
        this.rect = rect;
        this.padding = padding;
        this.baseline = baseline;
    }

    public MenuItem(Rectangle rect, int padding, int baseline, String text) {
        this.rect = rect;
        this.padding = padding;
        this.baseline = baseline;
        this.text = text;
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
        g.setColor(color);
        g.drawString(text,
                getX() + padding,
                getY() + getHeight() - baseline - padding);
        g.drawRect(getX(), getY(), getWidth(), getHeight());
    }

    public boolean inside(MouseEvent e) {
        return e.getX() >= getX() && e.getX() <= getX() + getWidth() && e.getY() >= getY() && e.getY() <= getY() + getHeight();
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
