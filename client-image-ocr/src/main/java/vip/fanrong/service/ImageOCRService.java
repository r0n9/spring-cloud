package vip.fanrong.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Rong on 2017/12/20.
 */
@Service
public class ImageOCRService {
    private static final Log LOGGER = LogFactory.getLog(ImageOCRService.class);

    @Value("${image.path}")
    private String imageFoler;

    @Value("${tessdata.path}")
    private String testResourcesLanguagePath;

    public String recognize(String imageUrl) {
        String cleanFileName = downloadImageAndClean(imageUrl);
        String resultFileName = cleanFileName + ".txt";
        if (null == cleanFileName) {
            return null;
        }
        ITesseract instance = new Tesseract();
        File imageFile = new File(imageFoler, cleanFileName);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(imageFile);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return null;
        }

        //set language
        instance.setDatapath(testResourcesLanguagePath);
        instance.setLanguage("eng");
        // whitelist
        instance.setTessVariable("tessedit_char_whitelist", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");


        String result = null;
        try {
            result = instance.doOCR(bi);
        } catch (TesseractException e) {
            LOGGER.error(e.getMessage());
            return null;
        }

        result = StringUtils.removePattern(result, "\\s*|\t|\r|\n");
        writeResultToFile(result, resultFileName);
        return result;
    }

    private void writeResultToFile(String result, String fileName) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(imageFoler + fileName);
            writer.write(result);
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String downloadImageAndClean(String imageUrl) {
        String uuid = UUID.randomUUID().toString();
        String fileName = System.currentTimeMillis() + "_" + uuid.substring(30);
        String cleanFileName = fileName + "_1.jpg";

        fileName = fileName + ".jpg";
        boolean isSuccess = MyHttpClient.downloadImage(imageUrl, imageFoler, fileName);

        if (!isSuccess) {
            LOGGER.error("Image download failed: " + imageUrl);
            return null;
        }

        try {
            MyImageUtil.cleanImage(new File(imageFoler, fileName), new File(imageFoler, cleanFileName));
        } catch (IOException e) {
            LOGGER.error("Image clean failed: " + imageUrl);
            LOGGER.error(e.getMessage());
            return null;
        }

        return cleanFileName;
    }

    public String recognizeKds(String imageUrl) {
        String cleanFileName = downloadImageAndClean(imageUrl);
        String resultFileName = cleanFileName + ".txt";
        if (null == cleanFileName) {
            return null;
        }
        File imageFile = new File(imageFoler, cleanFileName);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }


        StringBuilder builder = new StringBuilder();
        // cut
        for (int i = 0; i < 6; i++) {
            BufferedImage cutImage = cut(i * 80, 0, 80, 88, bufferedImage);
            try {
                String resultCut = String.valueOf(recongnize(cutImage));
                builder.append(resultCut);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                return null;
            }
        }

        String result = builder.toString();
        writeResultToFile(result, resultFileName);
        return result;
    }

    private BufferedImage cut(int x, int y, int width, int height, BufferedImage sourceBufferedImage) {
        int[] newRgb = new int[width * height];
        sourceBufferedImage.getRGB(x, y, width, height, newRgb, 0, width);
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        newImage.setRGB(0, 0, width, height, newRgb, 0, width);
        return newImage;
    }

    private static Map<Integer, int[][]> characteristics = new HashMap<>();

    static {
        for (int i = 0; i < 10; i++) {
            File characteristicFile = new File("src/main/resources/vcode-kds/" + i + ".png");
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(characteristicFile);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
            int[][] arr = imageToRGB(bufferedImage);
            characteristics.put(i, arr);
        }
    }

    private int recongnize(BufferedImage bufferedImage) throws IOException {
        double maxScore = 0.0;
        int result = -1;
        for (int value : characteristics.keySet()) {
            double score = compare(characteristics.get(value), imageToRGB(bufferedImage));
            if (score > maxScore) {
                maxScore = score;
                result = value;
            }
        }
        return result;
    }

    private static int[][] imageToRGB(BufferedImage bufferedImage) {

        int[][] arr = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
        for (int x = 0; x < arr.length; x++) {
            for (int y = 0; y < arr[0].length; y++) {
                arr[x][y] = bufferedImage.getRGB(x, y);
            }
        }
        return arr;
    }

    private double compare(int[][] base, int[][] cut) throws IOException {

        int h = base[0].length;
        int w = base.length;

        double total = 0.0;
        double match = 0.0;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (MyImageUtil.isBlack(base[x][y])) {
                    total++;
                    if (MyImageUtil.isBlack(cut[x][y])) {
                        match++;
                    }
                } else if (MyImageUtil.isBlack(cut[x][y])) {
                    total++;
                    if (MyImageUtil.isBlack(base[x][y])) {
                        match++;
                    }
                }
            }

        }
        return match / total;
    }
}
