import java.awt.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class SearchBlock {
    private Rectangle rect;
    private boolean topIsAir;
    private boolean bottomIsGround;

    public SearchBlock(Rectangle rect) {
        this.rect = rect;
    }

    public SearchBlock(int x, int y, int width, int height) {
        this.rect = new Rectangle(x, y, width, height);
    }

    public Rectangle getRect() {
        return this.rect;
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

    public void setTopIsAir(boolean topIsAir) {
        this.topIsAir = topIsAir;
    }

    public void setBottomIsGround(boolean bottomIsGround) {
        this.bottomIsGround = bottomIsGround;
    }

    public boolean topIsAir() {
        return this.topIsAir;
    }

    public boolean bottomIsGround() {
        return this.bottomIsGround;
    }
}
