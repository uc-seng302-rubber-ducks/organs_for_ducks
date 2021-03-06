package odms.commons.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import odms.commons.model._enum.Directory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static odms.commons.model._enum.Directory.TEMP;

/**
 * Contains helper methods related to photos.
 */
public final class PhotoHelper {

    public PhotoHelper() {
    }

    /**
     * sets up a temp folder and image folder (within temp folder).
     * then make a copy of the desired image and store it in the image folder.
     *
     * @param inFile desired image file
     * @param id     the id of the user/clinician
     * @return filepath to the image file
     * @throws IOException if there are issues with handling files.
     */
    public static String setUpImageFile(File inFile, String id) throws IOException {
        BufferedImage bImage;

        String fileType = inFile.getName();
        fileType = fileType.substring(fileType.lastIndexOf(".") + 1);

        Files.createDirectories(Paths.get(TEMP.directory() + Directory.IMAGES));
        File outFile = new File(TEMP.directory() + Directory.IMAGES + "/" + id.toUpperCase() + "." + fileType);

        bImage = ImageIO.read(inFile);
        ImageIO.write(bImage, fileType, outFile);

        return outFile.getPath();
    }

    /**
     * displays the image
     *
     * @param imageView JavaFX imageView object
     * @param filePath  file path to the desired photo
     */
    public static void displayImage(ImageView imageView, String filePath) {
        if (filePath != null) {
            Image image = new Image("file:" + filePath, 200, 200, false, true);
            imageView.setImage(image);
        }
    }


    /**
     *Displays an image in a javafx image view
     *
     * @param imageView image view to place the image in
     * @param url location of the file
     */
    public static void displayImage(ImageView imageView, URL url) {
        try {
            Image image = new Image(url.openStream());
            imageView.setImage(image);
        } catch (IOException o){
            //no
        }
    }

    /**
     * for deleting temp folder directory
     *
     * @throws IOException on file not found
     */
    public static void deleteTempDirectory() throws IOException {
        File f = new File(String.valueOf(Directory.TEMP));
        if (f.isDirectory()) {
            File[] fileList = f.listFiles();
            if(fileList == null){
                return;
            }
            for (File file : fileList) {
                Files.deleteIfExists(file.toPath());
            }
        }
    }

    /**
     * Creates a temp file to hold an image
     *
     * @param image image to place into the file
     * @param userId id of the user who owns the photo
     * @param format format of the file
     * @return location of the temp file
     * @throws IOException on unable to decode file
     */
    public static String createTempImageFile(byte[] image, String userId, String format) throws IOException {

        Files.createDirectories(Paths.get(TEMP.directory() + Directory.IMAGES));
        File outFile = new File(TEMP.directory() + Directory.IMAGES + "/" + userId.toUpperCase() + "." + format);
        InputStream in = new ByteArrayInputStream(image);
        BufferedImage buffImage = ImageIO.read(in);
        if (buffImage == null) {
            return "";
        }
        ImageIO.write(buffImage, format, outFile);


        return outFile.getPath();
    }

    /**
     * Takes an image and returns the bytes contained by it
     * @param filepath location of the image to be converted
     * @return the bytes in the image
     * @throws IOException on file not found
     */
    public static byte[] getBytesFromImage(String filepath) throws IOException {
        if(filepath.equals("")) return new byte[0];
        return Files.readAllBytes(Paths.get(filepath));
    }
}
