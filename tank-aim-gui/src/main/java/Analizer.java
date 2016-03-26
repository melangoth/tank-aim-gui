import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by develrage on 2016. 03. 25..
 */
public class Analizer {
    private final static int SEARCHLIMIT = 500;
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
            // if next block out of top, or bottom
            if (image.getHeight() < blocky + blockh || image.getMinY() > blocky) {
                System.out.println("Next block gone out top or bottom");
                blocky = 0;
            }

            // if next block is the same as the last
            if (nextSearchBlock != null && nextSearchBlock.getX() == blockx && nextSearchBlock.getY() == blocky) {
                System.out.println("Next block is the same as last");
                blockx += stepx;
            }

            // if next block is inside right limit
            if (image.getWidth() >= blockx + blockw) {
                nextSearchBlock = new SearchBlock(blockx, blocky, blockw, blockh);

                boolean isFieldLine = hasFieldLine(nextSearchBlock);

                if (isFieldLine) {
                    blockx += stepx;
                } else {
                    if (colorIsGround(nextSearchBlock.getTopColor()) && colorIsGround(nextSearchBlock.getBottomColor())) {
                        blocky -= stepy;
                    } else if (colorIsSpace(nextSearchBlock.getBottomColor()) && colorIsSpace(nextSearchBlock.getTopColor())) {
                        blocky += stepy;
                    } else {
                        System.out.println("next block cant find neither air nor bottom");

                    }
                }
                return true;
            } else { // if next block gone out on right limit
                System.out.println("next block gone aout on right");
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

        block.setTopColor(new Color(topR, topG, topB));
        block.setBottomColor(new Color(bottomR, bottomG, bottomB));

        return (colorIsSpace(block.getTopColor()) && colorIsGround(block.getBottomColor()));
    }

    private boolean colorIsSpace(Color color) {
        return (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 60);
    }

    private boolean colorIsGround(Color color) {
        return (color.getRed() < 100 && color.getGreen() < 160 && color.getBlue() > 130);
    }
}
