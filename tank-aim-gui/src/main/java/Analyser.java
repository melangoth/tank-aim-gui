import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by develrage
 */
public class Analyser extends AnalyserMathTools implements Runnable {
    final static Logger log = Logger.getLogger(Analyser.class);
    private static Analyser instance = null;
    private ArrayList<int[]> fieldBlocks = new ArrayList<int[]>();

    private Analyser() {
        log.trace("Analyser()");
    }

    public static Analyser getInstance() {
        log.trace("getInstance()");
        if (instance == null) {
            instance = new Analyser();
        }

        return instance;
    }

    public void run() {
        log.debug("run()");
        loadImagePool();

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Thread.sleep(1000);
                log.trace("Analyser hearthbeat.");
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted.", e);
            }
        }
    }

    public ArrayList<int[]> searchFieldLine() {
        ArrayList<int[]> fieldLine = new ArrayList<int[]>();

        int lookW = 5;
        int lookH = 5;
        int startX = 0;
        int endX = imageWidth - lookW;
        int startY = 0;
        int endY = imageHeight - lookH;

        for (int x = endX; x >= startX; x -= lookW) {
            int[] lastBlock = new int[]{-1};

            for (int y = endY; y >= startY; y -= lookH) {
                int[] block = new int[]{x, y, lookW, lookH};
                Color c = getAverageColor(block);
                boolean fl = isField(c);

                /*System.out.println(String.format("Analyser: x:%d y:%d rgb:%s f:%s", x, y,
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
        return isIn(r, 50, 100) && isIn(g, 120, 170) && isIn(b, 200, 255) || isIn(r, 20, 80) && isIn(g, 60, 140) && isIn(b, 100, 220);
    }

    public ArrayList<int[]> simulateBallisticShot(int angle, int v, int tankX, int tankY) {
        ArrayList<int[]> shotBlocks;

        double[] T = getTurretEnd(angle, 18);

        double g = 10;
        int a = angle;
        double px = 2; // px/paint
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

        int shotSize = 2;
        shotBlocks = new ArrayList<int[]>();
        for (int p = 0; p <= paints + 1; p++) {
            double x = px * p;
            double y = x * tan(a) - g / (2 * p(v, 2) * p(cos(a), 2)) * p(x, 2);

            int coordX = (int) (x * directionX + tankX + T[0]);
            int coordY = (int) (y * directionY + tankY + (T[1] * -1));

            if (coordX < 0) coordX = coordX * -1;
            if (coordX > imageWidth) coordX = imageWidth - (coordX - imageWidth);

            shotBlocks.add(new int[]{
                    coordX,
                    coordY,
                    shotSize, shotSize});
        }

        return shotBlocks;
    }

    private double[] getTurretEnd(double angle, double r) {
        double x = cos(angle) * r;
        double y = sin(angle) * r;

        return new double[]{x, y};
    }

    // todo: remove this, or comment out reference!
    public void getTankColor(Tank tank) {
        Color c = getAverageColor(new int[]{tank.getX(), tank.getCenterY(), tank.getWidth(), tank.getHeight()});
        System.out.println(String.format("Tank AVG Color: %d/%d/%d", c.getRed(), c.getGreen(), c.getBlue()));
    }

    public ArrayList<int[]> searchTank() {
        ArrayList<int[]> tanks = new ArrayList<int[]>();

        int blocksize = 30;
        int tanksize = 15;

        // search block above fieldline blocks
        for (int[] f : fieldBlocks) {
            int startX = f[0] + f[2] / 2 - blocksize / 2;
            int startY = f[1] - blocksize + 10;

            if (0 < startX && startX <= imageWidth - blocksize
                    && 0 < startY && startY <= imageHeight - blocksize) {

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

}
