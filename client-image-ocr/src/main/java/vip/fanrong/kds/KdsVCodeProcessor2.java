package vip.fanrong.kds;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * 识别KDS验证码中表情相同的四个数字位置
 */
public class KdsVCodeProcessor2 {

    static class PixelLine {
        int s;
        int e;
        int len;

        public PixelLine(int s, int e, int len) {
            this.s = s;
            this.e = e;
            this.len = len;
        }

        @Override
        public String toString() {
            return "pixelLine{" +
                    "s=" + s +
                    ", e=" + e +
                    ", len=" + len +
                    '}';
        }
    }

    public static void main1(String[] args) throws IOException {

        File file = new File("client-image-ocr/image/cut/0_0.png");

        BufferedImage bufferedImage = ImageIO.read(file);

        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();


        PixelLine[] rows = new PixelLine[h];

        int[][] array = new int[w][h];

        for (int y = 0; y < h; y++) {
            int start = -1;
            int end = -1;
            int len = 0;

            for (int x = 0; x < w; x++) {
                Color color = new Color(bufferedImage.getRGB(x, y));

                if (isRed(color) || isFlesh(color)) {
                    System.out.print("*");

                    if (start == -1) {
                        start = x;
                    }
                    end = x;

                    array[x][y] = 1;
                } else {
                    System.out.print(" ");

                    array[x][y] = 0;
                }
            }

            if (end != -1) {
                len = end - start;
            }

            rows[y] = new PixelLine(start, end, len);

            System.out.println();
        }


        System.out.println(Arrays.toString(rows));

        diameterRows(array);
    }


    private static int[] diameterRows(int[][] rows) {

        int h = rows[0].length;
        double total = (double) h * 4.0;

        for (int i = 0; i < rows.length; i = i + 4) {
            double pixels = 0;
            for (int j = 0; j < h; j++) {
                pixels += rows[i][j] == 1 ? 1.0 : 0.0;
            }

            double density = pixels / total;

            System.out.println("(" + i + "->" + (i + 3) + ") " + density);
        }

        return null;
    }

    public static boolean isFlesh(Color c) {
        RGB rgb = new RGB(c.getRed(), c.getGreen(), c.getBlue());
        HSV hsv = RGB2HSV(rgb);
        if (hsv.getH() > 9 && hsv.getH() < 43) {
            return true;
        }
        return false;
    }

    public static boolean isRed(Color c) {
        RGB rgb = new RGB(c.getRed(), c.getGreen(), c.getBlue());
        HSV hsv = RGB2HSV(rgb);
        if (hsv.getH() <= 10 || (hsv.getH() >= 156 && hsv.getH() <= 180)) {
            return true;
        }
        return false;
    }

    public static HSV RGB2HSV(RGB rgb) {
        float r = (float) rgb.getR() / 255;
        float g = (float) rgb.getG() / 255;
        float b = (float) rgb.getB() / 255;
        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);
        float h = 0;
        if (r == max)
            h = (g - b) / (max - min);
        if (g == max)
            h = 2 + (b - r) / (max - min);
        if (b == max)
            h = 4 + (r - g) / (max - min);
        h *= 60;
        if (h < 0) h += 360;
        HSV hsv = new HSV(h, (max - min) / max, max);
        return hsv;
    }


    static class RGB {
        int R;
        int G;
        int B;

        public RGB(int r, int g, int b) {
            R = r;
            G = g;
            B = b;
        }

        public int getR() {
            return R;
        }

        public void setR(int r) {
            R = r;
        }

        public int getG() {
            return G;
        }

        public void setG(int g) {
            G = g;
        }

        public int getB() {
            return B;
        }

        public void setB(int b) {
            B = b;
        }
    }

    static class HSV {
        float H;
        float S;
        float V;

        public HSV(float h, float s, float v) {
            H = h;
            S = s;
            V = v;
        }

        @Override
        public String toString() {
            return "HSV{" +
                    "H=" + H +
                    ", S=" + S +
                    ", V=" + V +
                    '}';
        }

        public float getH() {
            return H;
        }

        public void setH(float h) {
            H = h;
        }

        public float getS() {
            return S;
        }

        public void setS(float s) {
            S = s;
        }

        public float getV() {
            return V;
        }

        public void setV(float v) {
            V = v;
        }
    }

}