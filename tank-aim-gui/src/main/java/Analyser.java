import com.develrage.birdocr.Recognition;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by develrage
 */
public class Analyser extends AnalyserMathTools implements Runnable {
    final static Logger log = Logger.getLogger(Analyser.class);
    private static Analyser instance = null;
    private final Object tracerLock = new Object();
    private final Object fieldLineLock = new Object();
    private final Object tankLock = new Object();
    private final Object upsLock = new Object();
    // todo repalce ints with Rectangle, or something more useful
    private ArrayList<int[]> fieldLineBlocks;
    private ArrayList<int[]> tracerBlocks;
    private Tank p1Tank;
    private Tank p2Tank;
    private Tank activeTank;
    private int angle = 45;
    private int power = 75;
    private Recognition powerRecognizer = null;
    private Recognition angleRecognizer = null;
    // UPS monitor
    private ArrayList<Long> workMillis = new ArrayList<>();
    private double ups;

    private Analyser() {
        log.trace("Analyser()");

        // init
        try {
            log.info("Loading ocr maps...");
            powerRecognizer = new Recognition(Recognition.OcrMap.TANKP, "classpath://Screener/ocrmaps/TANKP.json");
            angleRecognizer = new Recognition(Recognition.OcrMap.TANKA, "classpath://Screener/ocrmaps/TANKA.json");
        } catch (IOException e) {
            log.error("Failed to load OcrMap!");
        }
        p1Tank = new Tank(Color.GREEN, "P1Tank");
        p2Tank = new Tank(Color.ORANGE, "P2Tank");
        activeTank = p1Tank;
        fieldLineBlocks = new ArrayList<>();
        tracerBlocks = new ArrayList<>();
        loadImagePool();
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
                Screener.getInstance().captureScreenRegions();
                loadImageCaptured();
                fullAnalysation();

                // Refresh UPS monitor
                workMillis.add(new Date().getTime());
                if (workMillis.size() > 10) {
                    calcUPSAvg();
                }

                Thread.sleep(50);
            } catch (InterruptedException e) {
                log.warn("Sleep interrupted.", e);
            }
        }
    }

    private synchronized void fullAnalysation() {
        readAimValues();
        searchFieldLine();
        searchTank(p1Tank);
        calculateTrajectory(p1Tank);
        calculateTrajectory(p2Tank);
    }

    private void readAimValues() {
        BufferedImage angle = Screener.getInstance().getAngleCaptured();
        BufferedImage power = Screener.getInstance().getPowerCaptured();
        String digits = "";
        this.angle = -1;
        this.power = -1;

        // todo NegativeArraySizeException on blank captures
        if (angle != null) {
            digits = angleRecognizer.getRecognisedStringFromImage(angle);

            try {
                this.angle = Integer.parseInt(digits);
            } catch (NumberFormatException e) {
                this.angle = -1;
            } finally {
                digits = "";
            }
        } else {
            log.debug("Failed to get captured angle.");
        }

        if (power != null) {
            digits = powerRecognizer.getRecognisedStringFromImage(power);

            try {
                this.power = Integer.parseInt(digits);
            } catch (NumberFormatException e) {
                this.power = -1;
            }
        } else {
            log.debug("Failed to get captured power.");
        }

//        log.debug(String.format("Found digits: %s", digits));
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

    private void calcUPSAvg() {
        double sum = 0;
        for (int i = workMillis.size() - 1; i >= 1; i--) {
            sum += workMillis.get(i) - workMillis.get(i - 1);
        }
        sum /= (workMillis.size() - 1);
        synchronized (upsLock) {
            ups = 1000 / sum;
//            log.info(String.format("Calculating UPS (%d): %f", workMillis.size(), ups));
        }
        workMillis.remove(0);
    }

    public double getUPS() {
        synchronized (upsLock) {
            return ups;
        }
    }
}
