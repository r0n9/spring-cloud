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
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Rong on 2017/12/20.
 */
@Service
public class ImageOCRService {
    private static final Log LOGGER = LogFactory.getLog(ImageOCRService.class);

    private final static String IMAGE_FOLDER = "./../images/";
    private final static String IMAGE_CLEAN_FOLDER = IMAGE_FOLDER + "clean/";

    private final String testResourcesLanguagePath = "src/main/resources/tessdata";

    public String recognize(String imageUrl) {
        String uuid = UUID.randomUUID().toString();
        String fileName = System.currentTimeMillis() + "_" + uuid.substring(30) + ".jpg";
        boolean isSuccess = MyHttpClient.downloadImage(imageUrl, IMAGE_FOLDER, fileName);

        if (!isSuccess) {
            LOGGER.error("Image download failed: " + imageUrl);
            return null;
        }

        try {
            MyImageUtil.cleanImage(new File(IMAGE_FOLDER, fileName), IMAGE_CLEAN_FOLDER);
        } catch (IOException e) {
            LOGGER.error("Image clean failed: " + imageUrl);
            LOGGER.error(e.getMessage());
            return null;
        }

        ITesseract instance = new Tesseract();
        File imageFile = new File(IMAGE_CLEAN_FOLDER, fileName);
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

        return result;
    }
}
