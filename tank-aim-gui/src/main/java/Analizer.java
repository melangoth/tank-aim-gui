import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class Analizer {
    private final static int SEARCHLIMIT = 150;
    BufferedImage image;
    private int searchCounter = 0;
    private SearchBlock nextSearchBlock = null;

    // searching
    private int blockx = 0;
    private int blocky = 0;
    private int blockw = 5;
    private int blockh = 45;
    private int stepx = blockw;
    private int stepy = 10;

    public Analizer(Image image) {
        this.image = (BufferedImage) image;
    }

    public SearchBlock nextSearchBlock() {
        return nextSearchBlock;
    }

    public boolean hasSearchBlock() {
        searchCounter++;
        if (searchCounter > SEARCHLIMIT) {
            return false;
        } else {
            if (image.getWidth() >= blockx + blockw && image.getHeight() >= blocky + blockh
                    && image.getMinX() <= blockx && image.getMinY() <= blocky) {
                nextSearchBlock = new SearchBlock(blockx, blocky, blockw, blockh);

                boolean isFieldLine = hasFieldLine(nextSearchBlock);

                if (isFieldLine) {
                    blockx += stepx;
                } else {
                    if (!nextSearchBlock.topIsAir() && nextSearchBlock.bottomIsGround()) {
                        blocky -= stepy;
                    } else {
                        blocky += stepy;
                    }
                }
                return true;
            } else {
                nextSearchBlock = null;
                return false;
            }
        }
    }

    private boolean hasFieldLine(SearchBlock block) {
        int bottomR = 0;
        int bottomG = 0;
        int bottomB = 0;
        int topR = 0;
        int topG = 0;
        int topB = 0;
        int blocks = 0;

        for (int i = 0; i < block.getWidth(); i++) {
            for (int j = 0; j < 5; j++) {
                blocks++;
                int x = i + block.getX();
                int by = block.getY() + block.getHeight() - j;
                int ty = block.getY() + j;

                Color cBottom = new Color(image.getRGB(x, by), true);
                bottomR += cBottom.getRed();
                bottomG += cBottom.getGreen();
                bottomB += cBottom.getBlue();

                Color cTop = new Color(image.getRGB(x, ty), true);
                topR += cTop.getRed();
                topG += cTop.getGreen();
                topB += cTop.getBlue();
            }
        }

        bottomR /= blocks;
        bottomG /= blocks;
        bottomB /= blocks;

        topR /= blocks;
        topG /= blocks;
        topB /= blocks;

        System.out.println(String.format("Search #%d; Top RGB avg: %d,%d,%d; Bottom RGB avg: %d,%d,%d,", searchCounter, topR, topG, topB, bottomR, bottomG, bottomB));

        boolean topIsAir = (topR < 50 && topG < 50 && topB < 60);
        boolean bottomIsGround = (bottomR < 100 && bottomG < 160 && bottomB > 130);

        block.setTopIsAir(topIsAir);
        block.setBottomIsGround(bottomIsGround);

        return (topIsAir && bottomIsGround);
    }
}
