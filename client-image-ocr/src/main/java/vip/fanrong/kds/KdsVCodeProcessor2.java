package vip.fanrong.kds;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 识别KDS验证码中表情相同的四个数字位置
 */
public class KdsVCodeProcessor2 {

    public static void main(String[] args) throws IOException {

        File file = new File("client-image-ocr/image/0.png");

        BufferedImage bufferedImage = ImageIO.read(file);

        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();


        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color color = new Color(bufferedImage.getRGB(x, y));
//                System.out.print( color.getGreen() + "\t");

                if (isFlesh(color)) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }

    }

    public static boolean isFlesh(Color c) {
        RGB rgb = new RGB(c.getRed(), c.getGreen(), c.getBlue());
        HSV hsv = RGB2HSV(rgb);
        if (hsv.getH() > 9 && hsv.getH() < 43) {
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