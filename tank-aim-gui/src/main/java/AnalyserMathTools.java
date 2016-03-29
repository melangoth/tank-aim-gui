import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by develrage
 */
public class AnalyserMathTools extends AnalyserImageTools {
    final static Logger log = Logger.getLogger(AnalyserMathTools.class);

    protected Color getAverageColor(int[] block) {
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

    protected boolean isIn(int a, int l1, int l2) {
        return (l1 <= a && a <= l2);
    }

    protected double p(double base, double power) {
        return Math.pow(base, power);
    }

    protected double cos(double alpha) {
        return Math.cos(Math.PI / 180 * alpha);
    }

    protected double sin(double alpha) {
        return Math.sin(Math.PI / 180 * alpha);
    }

    protected double tan(double alpha) {
        return Math.tan(Math.PI / 180 * alpha);
    }

    protected double[] getMuzzlePoint(double angle, double r) {
        double x = cos(angle) * r;
        double y = sin(angle) * r;

        return new double[]{x, y};
    }

    protected int[] getAvgCoords(ArrayList<int[]> blocks) {
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
