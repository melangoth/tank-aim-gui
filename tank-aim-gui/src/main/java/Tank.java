import java.awt.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
class Tank {
    private int xPos = 50;
    private int yPos = 50;
    private int width = 15;
    private int height = 15;
    private Color color;

    public Tank(Color color) {
        this.color = color;
    }

    public Tank(Color color, int x, int y) {
        this.color = color;
        this.xPos = x;
        this.yPos = y;
    }

    public int getCenterX() {
        return xPos + width / 2;
    }

    public int getCenterY() {
        return yPos + height / 2;
    }

    public void setCenter(int x, int y) {
        xPos = x - width / 2;
        yPos = y - height / 2;
    }

    public int getX() {
        return xPos;
    }

    public void setX(int xPos) {
        this.xPos = xPos;
    }

    public int getY() {
        return yPos;
    }

    public void setY(int yPos) {
        this.yPos = yPos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void paintSprite(Graphics g) {
        g.setColor(this.color);
        g.drawRect(xPos, yPos, width, height);
        g.drawString(String.format("@ %d,%d", xPos, yPos), xPos, yPos + 20 + height);
    }
}
