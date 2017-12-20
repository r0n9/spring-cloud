package vip.fanrong.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import vip.fanrong.common.MyHttpClient;
import vip.fanrong.common.MyImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Rong on 2017/12/20.
 */
@Service
public class ImageOCRService {
    private static final Log LOGGER = LogFactory.getLog(ImageOCRService.class);

    private final static String IMAGE_FOLDER = "./../images/";

    private final String testResourcesLanguagePath = "src/main/resources/tessdata";

    public String recognize(String imageUrl) {
        String uuid = UUID.randomUUID().toString();
        String fileName = System.currentTimeMillis() + "_" + uuid.substring(30);
        String cleanFileName = fileName + "_1.jpg";
        String resultFileName = fileName + ".txt";

        fileName = fileName + ".jpg";
        boolean isSuccess = MyHttpClient.downloadImage(imageUrl, IMAGE_FOLDER, fileName);

        if (!isSuccess) {
            LOGGER.error("Image download failed: " + imageUrl);
            return null;
        }

        try {
            MyImageUtil.cleanImage(new File(IMAGE_FOLDER, fileName), new File(IMAGE_FOLDER, cleanFileName));
        } catch (IOException e) {
            LOGGER.error("Image clean failed: " + imageUrl);
            LOGGER.error(e.getMessage());
            return null;
        }

        ITesseract instance = new Tesseract();
        File imageFile = new File(IMAGE_FOLDER, cleanFileName);
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

        String result = null;
        try {
            result = instance.doOCR(bi);
        } catch (TesseractException e) {
            LOGGER.error(e.getMessage());
            return null;
        }


        try {
            FileWriter writer = new FileWriter(IMAGE_FOLDER + resultFileName);
            writer.write(result);
            writer.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }


        return result;
    }
}
