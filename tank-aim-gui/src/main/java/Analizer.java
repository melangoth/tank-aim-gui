import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class Analizer {
    private BufferedImage image;
    private int fieldWidth;
    private int fieldHeight;
    private ArrayList<int[]> fieldBlocks = new ArrayList<int[]>();

    public Analizer(Image image) {
        this.image = (BufferedImage) image;
        this.fieldWidth = this.image.getWidth();
        this.fieldHeight = this.image.getHeight();
    }

    private Color getAverageColor(int[] block) {
        int cR = 0;
        int cG = 0;
        int cB = 0;
        int blocks = 0;

        for (int i = 0; i < block[2]; i++) {
            for (int j = 0; j < block[3]; j++) {
                blocks++;
                int x = i + block[0];
                int y = j + block[1];

                Color c = new Color(image.getRGB(x, y), true);
                cR += c.getRed();
                cG += c.getGreen();
                cB += c.getBlue();
            }
        }

        cR /= blocks;
        cG /= blocks;
        cB /= blocks;

        return new Color(cR, cG, cB);
    }

    public ArrayList<int[]> searchFieldLine() {
        ArrayList<int[]> fieldLine = new ArrayList<int[]>();

        int lookW = 5;
        int lookH = 5;
        int startX = 0;
        int endX = image.getWidth() - lookW;
        int startY = 0;
        int endY = image.getHeight() - lookH;

        for (int x = endX; x >= startX; x -= lookW) {
            int[] lastBlock = new int[]{-1};

            for (int y = endY; y >= startY; y -= lookH) {
                int[] block = new int[]{x, y, lookW, lookH};
                Color c = getAverageColor(block);
                boolean fl = isField(c);

                /*System.out.println(String.format("Analizer: x:%d y:%d rgb:%s f:%s", x, y,
                        String.format("%d/%d/%d", c.getRed(), c.getGreen(), c.getBlue()), fl));*/

                if (!fl) break;
                lastBlock = block;
            }
            fieldLine.add(lastBlock);
        }

        this.fieldBlocks = fieldLine;
        return fieldLine;
    }

    private boolean isField(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        if (isIn(r, 50, 100) && isIn(g, 120, 170) && isIn(b, 200, 255)) {
            return true;
        } else if (isIn(r, 20, 80) && isIn(g, 60, 140) && isIn(b, 100, 220)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isIn(int a, int l1, int l2) {
        return (l1 <= a && a <= l2);
    }

    public ArrayList<int[]> simulateBallisticShot(int angle, int power, int tankX, int tankY) {
        ArrayList<int[]> shotBlocks;

        double[] T = getTurretEnd(angle, tankX, tankY, 18);

        int v = power;
        double g = 10;
        int a = angle;
        double px = 1; // px/paint
        int paints = 1000;

        int directionX = 1;
        int directionY = -1;
        if (90 < a && a <= 180) {
            a = 180 - a;
            directionX = -1;
        } else if (180 < a && a <= 270) {
            a = 360 - a;
            directionX = -1;
        }

        // todo: refine shot position and density
        int shotSize = 2;
        shotBlocks = new ArrayList<int[]>();
        for (int p = 0; p <= paints + 1; p++) {
            double x = px * p;
            double y = x * tan(a) - g / (2 * p(v, 2) * p(cos(a), 2)) * p(x, 2);

            int coordX = (int) (x * directionX + tankX + T[0]);
            int coordY = (int) (y * directionY + tankY + (T[1] * -1));

            if (coordX < 0) coordX = coordX * -1;
            if (coordX > fieldWidth) coordX = fieldWidth - (coordX - fieldWidth);

            shotBlocks.add(new int[]{
                    coordX,
                    coordY,
                    shotSize, shotSize});
        }

        return shotBlocks;
    }

    private double[] getTurretEnd(double angle, double tankX, double tankY, double r) {
        double x = cos(angle) * r;
        double y = sin(angle) * r;

        return new double[]{x, y};
    }

    private double p(double base, double power) {
        return Math.pow(base, power);
    }

    private double cos(double alpha) {
        return Math.cos(Math.PI / 180 * alpha);
    }

    private double sin(double alpha) {
        return Math.sin(Math.PI / 180 * alpha);
    }

    private double tan(double alpha) {
        return Math.tan(Math.PI / 180 * alpha);
    }

    public void getTankColor(Tank tank) {
        Color c = getAverageColor(new int[]{tank.getX(), tank.getCenterY(), tank.getWidth(), tank.getHeight()});
        System.out.println(String.format("Tank ACG Color: %d/%d/%d", c.getRed(), c.getGreen(), c.getBlue()));
    }

    public ArrayList<int[]> searchTank() {
        ArrayList<int[]> tanks = new ArrayList<int[]>();

        int blocksize = 30;
        int tanksize = 15;

        // search block above fieldline blocks
        for (int[] f : fieldBlocks) {
            int startX = f[0] + f[2] / 2 - blocksize / 2;
            int startY = f[1] - blocksize + 10;

            if (0 < startX && startX <= fieldWidth - blocksize
                    && 0 < startY && startY <= fieldHeight - blocksize) {

                int[] block = new int[]{startX, startY, blocksize, blocksize};
                //tanks.add(block);

                // search tank in search block
                for (int i = 0; i <= blocksize - tanksize; i++) {
                    for (int j = 0; j <= blocksize - tanksize; j++) {
                        int[] tank = new int[]{i + block[0], j + block[1], tanksize, tanksize};

                        Color c = getAverageColor(tank);

                        if (isIn(c.getRed(), 0, 60) && isIn(c.getGreen(), 90, 130) && isIn(c.getBlue(), 80, 120)) {
                            tanks.add(tank);
                        }
                    }
                }
            }
        }

        int[] avg = getAvgCoords(tanks);
        tanks.clear();
        tanks.add(new int[]{avg[0], avg[1], 5, 5});

        System.out.println("Returning tank blocks: " + tanks.size());

        return tanks;
    }

    private int[] getAvgCoords(ArrayList<int[]> blocks) {
        double x = 0;
        double y = 0;

        for (int[] b : blocks) {
            x += b[0] + b[2] / 2;
            y += b[1] + b[3] / 2;
        }

        x = x / blocks.size();
        y = y / blocks.size();

        return new int[]{(int) Math.round(x), (int) Math.round(y)};
    }
}
