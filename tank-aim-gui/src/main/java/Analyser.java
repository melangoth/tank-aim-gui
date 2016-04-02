import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by develrage
 */
public class Analyser extends AnalyserMathTools implements Runnable {
    final static Logger log = Logger.getLogger(Analyser.class);
    private static Analyser instance = null;
    private final Object tracerLock = new Object();
    private final Object fieldLineLock = new Object();
    private final Object tankLock = new Object();
    // todo repalce ints with Rectangle, or something more useful
    private ArrayList<int[]> fieldLineBlocks;
    private ArrayList<int[]> tracerBlocks;
    private Tank p1Tank;
    private Tank p2Tank;
    private Tank activeTank;
    private int angle = 45;
    private int power = 75;

    private Analyser() {
        log.trace("Analyser()");
        p1Tank = new Tank(Color.GREEN, "P1Tank");
        p2Tank = new Tank(Color.ORANGE, "P2Tank");
        activeTank = p1Tank;
        fieldLineBlocks = new ArrayList<>();
        tracerBlocks = new ArrayList<>();
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.warn("Sleep interrupted.", e);
        }

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                log.trace("Analyser hearthbeat.");
//                loadImagePool();
                loadImageCaptured();
                fullAnalysation();
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted.", e);
            }
        }
    }

    private synchronized void fullAnalysation() {
        searchFieldLine();
        searchTank(p1Tank);
        calculateTrajectory(p1Tank);
        calculateTrajectory(p2Tank);
    }

    private void searchFieldLine() {
        if (image == null) {
            log.warn("searchFieldLine(): No image loaded.");
            return;
        }

        ArrayList<int[]> searchBlocks = new ArrayList<>();

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
                boolean fl = colorIsGround(c);

                /*System.out.println(String.format("Analyser: x:%d y:%d rgb:%s f:%s", x, y,
                        String.format("%d/%d/%d", c.getRed(), c.getGreen(), c.getBlue()), fl));*/

                if (!fl) break;
                lastBlock = block;
            }
            if (lastBlock[0] > -1) searchBlocks.add(lastBlock);
        }

        synchronized (fieldLineLock) {
            fieldLineBlocks = searchBlocks;
        }
    }

    private void searchTank(Tank tank) {
        if (image == null) {
            log.warn("searchTank(): No image loaded.");
            return;
        }

        ArrayList<int[]> searchBlocks = new ArrayList<>();
        synchronized (tracerLock) {
            tracerBlocks.clear();
        }

        int blocksize = 30;
        int tanksize = 15;

        // search block above fieldline blocks
        ArrayList<int[]> fieldLineBlocks;
        synchronized (fieldLineLock) {
            fieldLineBlocks = this.fieldLineBlocks;
        }
        for (int[] f : fieldLineBlocks) {
            int startX = f[0] + f[2] / 2 - blocksize / 2;
            int startY = f[1] - blocksize + 10;

            if (0 < startX && startX <= imageWidth - blocksize
                    && 0 < startY && startY <= imageHeight - blocksize) {

                int[] block = new int[]{startX, startY, blocksize, blocksize};
                synchronized (tracerLock) {
                    tracerBlocks.add(block);
                }

                // search tank in search block
                for (int i = 0; i <= blocksize - tanksize; i++) {
                    for (int j = 0; j <= blocksize - tanksize; j++) {
                        int[] tankBlock = new int[]{i + block[0], j + block[1], tanksize, tanksize};
                        synchronized (tracerLock) {
//                            tracerBlocks.add(tankBlock);
                        }

                        Color c = getAverageColor(tankBlock);

                        if (isIn(c.getRed(), 0, 60) && isIn(c.getGreen(), 90, 130) && isIn(c.getBlue(), 80, 120)) {
                            searchBlocks.add(tankBlock);
                        }
                    }
                }
            }
        }

        int[] avg = getAvgCoords(searchBlocks);

        synchronized (tankLock) {
            tank.setCenter(avg[0], avg[1]);
        }
    }

    private void calculateTrajectory(Tank tank) {
        ArrayList<int[]> shotBlocks;

        double g = 10;
        int a = angle;
        int v = power;
        double px = 2; // px/paint
        int paints = 1000;

        double[] muzzlePoint = getMuzzlePoint(a, 18);

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
        shotBlocks = new ArrayList<>();
        for (int p = 0; p <= paints + 1; p++) {
            double x = px * p;
            double y = x * tan(a) - g / (2 * p(v, 2) * p(cos(a), 2)) * p(x, 2);

            int coordX = (int) (x * directionX + tank.getCenterX() + muzzlePoint[0]);
            int coordY = (int) (y * directionY + tank.getCenterY() + (muzzlePoint[1] * directionY));

            if (coordX < 0) coordX = coordX * -1;
            if (coordX > imageWidth) coordX = imageWidth - (coordX - imageWidth);

            shotBlocks.add(new int[]{
                    coordX,
                    coordY,
                    shotSize, shotSize});
        }

        tank.setTrajectoryBlocks(shotBlocks);
    }

    private boolean colorIsGround(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        return isIn(r, 50, 100) && isIn(g, 120, 170) && isIn(b, 200, 255) || isIn(r, 20, 80) && isIn(g, 60, 140) && isIn(b, 100, 220);
    }

    public synchronized ArrayList<int[]> getFieldLineBlocks() {
        synchronized (fieldLineLock) {
            return fieldLineBlocks;
        }
    }

    public synchronized Tank getP1Tank() {
        synchronized (tankLock) {
            return p1Tank;
        }
    }

    public synchronized Tank getP2Tank() {
        synchronized (tankLock) {
            return p2Tank;
        }
    }

    public synchronized int getAngle() {
        return angle;
    }

    public synchronized void setAngle(int angle) {
        this.angle = angle;
    }

    public synchronized int getPower() {
        return power;
    }

    public synchronized void setPower(int power) {
        this.power = power;
    }

    public synchronized ArrayList<int[]> getTracerBlocks() {
        synchronized (tracerLock) {
            return tracerBlocks;
        }
    }

    public synchronized Tank getActiveTank() {
        synchronized (tankLock) {
            return activeTank;
        }
    }

    public synchronized void switchActiveTank() {
        synchronized (tankLock) {
            if (activeTank == p1Tank) {
                activeTank = p2Tank;
            } else {
                activeTank = p1Tank;
            }
        }
    }
}
