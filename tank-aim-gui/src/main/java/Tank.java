import java.awt.*;

/**
 * Created by develrage on 2016. 03. 25..
 */
class Tank {
    private int xPos = 50;
    private int yPos = 50;
    private int width = 20;
    private int height = 20;
    private Color color;

    public Tank(Color color) {
        this.color = color;
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
        //g.setColor(this.color);
        //g.fillRect(xPos,yPos,width,height);
        g.setColor(this.color);
        g.drawRect(xPos, yPos, width, height);
    }
}