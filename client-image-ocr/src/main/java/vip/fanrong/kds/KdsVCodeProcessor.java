package vip.fanrong.kds;

import vip.fanrong.common.MyImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 识别KDS验证码中6位数字
 */
public class KdsVCodeProcessor {


    public static void main1(String[] args) throws IOException {
//        MyHttpClient.downloadImage("https://passport.pchome.net/index.php?c=vcode", "client-image-ocr/image/", "41.png");
        for (int j = 0; j < 50; j++) {
            String fileName = "client-image-ocr/image/" + j + ".png";
            System.out.println(fileName);
            File file = new File(fileName);
            BufferedImage bufferedImage = ImageIO.read(file);

            int srcWidth = bufferedImage.getWidth();
            int srcHeight = bufferedImage.getHeight();

            for (int i = 0; i < 6; i++) {
                BufferedImage cutImage = cut(i * 80, 0, 80, 88, bufferedImage);
                ImageIO.write(cutImage, "png", new File("client-image-ocr/image/cut/" + j + "_" + i + ".png"));
            }

        }
    }

    private static BufferedImage cut(int x, int y, int width, int height, BufferedImage sourceBufferedImage) {
        int[] newRgb = new int[width * height];
        sourceBufferedImage.getRGB(x, y, width, height, newRgb, 0, width);
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        newImage.setRGB(0, 0, width, height, newRgb, 0, width);
        return newImage;
    }

    public static void main2(String[] args) throws IOException {

        File cutFolder = new File("client-image-ocr/image/cut/");
        File[] list = cutFolder.listFiles();

        for (File file : list) {
            if (!file.getName().endsWith("png")) continue;
            System.out.println(file.getPath());
            MyImageUtil.cleanImage(file, new File("client-image-ocr/image/clean/", file.getName()));
        }
    }

    public static void main3(String[] args) throws IOException {

        File image1 = new File("client-image-ocr/image/clean/5_0.png");
        File cutFolder = new File("client-image-ocr/image/clean/");
        File[] list = cutFolder.listFiles();

        double min = 1.0;
        List<String> group = new ArrayList<>();
        group.add(image1.getAbsolutePath());
        for (File image : list) {
            if (!image.getName().endsWith("png")) continue;

            if (image.getName().equalsIgnoreCase(image1.getName())) continue;

            double score = compare(image1, image);
            if (score > 0.6) {
                min = score;
                System.out.println(image.getName() + ": " + score);
                group.add(image.getAbsolutePath());
            }
        }

        System.out.println(group.size());

        BufferedImage bufferedImage1 = ImageIO.read(image1);

        int w = bufferedImage1.getWidth();
        int h = bufferedImage1.getHeight();

        int[][] base = new int[w][h];

        for (int i = 0; i < group.size(); i++) {
            BufferedImage bufferedImage = ImageIO.read(new File(group.get(i)));
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (MyImageUtil.isBlack(bufferedImage.getRGB(x, y))) {
                        base[x][y]++;
                        System.out.print("*");

                    } else {
                        System.out.print(" ");

                    }

                }
                System.out.println("");
            }
            System.out.println(" ");
        }

        double threshold = (double) group.size() / 2.0;

        BufferedImage baseBufferedImage = new BufferedImage(w, h,
                BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (base[x][y] < threshold) {
                    base[x][y] = 0;
                    System.out.print("0");
                    baseBufferedImage.setRGB(x, y, 0x00FFFF);
                } else {
                    base[x][y] = 1;
                    System.out.print("1");
                    baseBufferedImage.setRGB(x, y, 0xFF0000);
                }
            }
            System.out.println();
        }

        ImageIO.write(baseBufferedImage, "jpg", new File("client-image-ocr/image/base/7.png"));


    }

    public static double compare(File image1, File image2) throws IOException {
        BufferedImage bufferedImage1 = ImageIO.read(image1);
        BufferedImage bufferedImage2 = ImageIO.read(image2);

        int h = bufferedImage1.getHeight();
        int w = bufferedImage1.getWidth();

        double total = 0.0;
        double match = 0.0;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (MyImageUtil.isBlack(bufferedImage1.getRGB(x, y))) {
                    total++;
                    if (MyImageUtil.isBlack(bufferedImage2.getRGB(x, y))) {
                        match++;
                    }
                } else if (MyImageUtil.isBlack(bufferedImage2.getRGB(x, y))) {
                    total++;
                    if (MyImageUtil.isBlack(bufferedImage1.getRGB(x, y))) {
                        match++;
                    }
                }
            }

        }
        return match / total;
    }

    public static void main5(String[] args) throws IOException {

        for (int i = 0; i < 10; i++) {
            File folder = new File("client-image-ocr/image/base/" + i);
            if (folder.exists()) {
                for (File file : folder.listFiles()) {
                    Files.deleteIfExists(file.toPath());
                }

            } else {
                folder.mkdirs();
            }
        }

        File cleanFolder = new File("client-image-ocr/image/clean/");
        File[] list = cleanFolder.listFiles();

        for (File image : list) {
            if (!image.getName().endsWith("png")) continue;
            int result = recongnize(image);
            File dest = new File("client-image-ocr/image/base/" + result + "/" + image.getName());
            Files.copy(image.toPath(), dest.toPath());
        }

    }


    public static void main4(String[] args) throws IOException {
        System.out.println(recongnize(new File("client-image-ocr/image/clean/25_4.png")));
    }

    public static int recongnize(File image) throws IOException {
        Map<String, Integer> baseToValue = new HashMap<>();
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/0.png", 0);
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/1.png", 1);
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/2.png", 2);
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/3.png", 3);
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/4.png", 4);
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/5.png", 5);
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/6.png", 6);
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/7.png", 7);
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/8.png", 8);
        baseToValue.put("client-image-ocr/src/main/resources/vcode-kds/9.png", 9);

        double maxScore = 0.0;
        int result = -1;
        for (String baseFilePath : baseToValue.keySet()) {
            double score = compare(new File(baseFilePath), image);

            int value = baseToValue.get(baseFilePath);

            if (score > maxScore) {
                maxScore = score;
                result = value;
            }
        }

        return result;


    }

    public static void main6(String[] args) throws IOException {
        File image1 = new File("client-image-ocr/src/main/resources/vcode-kds/0.png");
        BufferedImage bufferedImage1 = ImageIO.read(image1);

        System.out.println(bufferedImage1.getHeight());
        System.out.println(bufferedImage1.getWidth());

        int[][] arr = new int[80][88];

        System.out.println(arr.length);
        System.out.println(arr[0].length);
    }
}