import java.awt.*;
import java.util.ArrayList;

/**
 * Created by develrage
 */
class Tank extends AnalyserMathTools {
    private final Object trajectoryLock = new Object();
    private String name;
    private int xPos = 400;
    private int yPos = 100;
    private int width = 15;
    private int height = 15;
    private Color color;
    private ArrayList<int[]> trajectoryBlocks;

    public Tank(Color color, String name) {
        this.color = color;
        this.name = name;
        trajectoryBlocks = new ArrayList<>();
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

        // main circle with center
        g.drawOval(xPos, yPos, width, height);
        int cSize = 4;
        g.fillOval(getCenterX() - cSize / 2, getCenterY() - cSize / 2, cSize, cSize);

        // cannon circle with muzzle point
        int tSize = 36;
        g.drawOval(getCenterX() - tSize / 2, getCenterY() - tSize / 2, tSize, tSize);
        double[] muzzlePoint = getMuzzlePoint(Analyser.getInstance().getAngle(), tSize / 2);
        g.fillOval(getCenterX() - cSize / 2 + (int) muzzlePoint[0], getCenterY() - cSize / 2 - (int) muzzlePoint[1], cSize, cSize);

        // position text
        g.drawString(String.format("@ %d,%d", xPos, yPos), xPos, yPos + 20 + height);
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public ArrayList<int[]> getTrajectoryBlocks() {
        synchronized (trajectoryLock) {
            return trajectoryBlocks;
        }
    }

    public synchronized void setTrajectoryBlocks(ArrayList<int[]> trajectoryBlocks) {
        synchronized (trajectoryLock) {
            this.trajectoryBlocks = trajectoryBlocks;
        }
    }
}
