import java.awt.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class SearchBlock {
    private Rectangle rect;
    private Color topColor;
    private Color bottomColor;

    public SearchBlock(int x, int y, int width, int height) {
        this.rect = new Rectangle(x, y, width, height);
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

    public Color getBottomColor() {
        return bottomColor;
    }

    public void setBottomColor(Color bottomColor) {
        this.bottomColor = bottomColor;
    }

    public Color getTopColor() {
        return topColor;
    }

    public void setTopColor(Color topColor) {
        this.topColor = topColor;
    }
}
