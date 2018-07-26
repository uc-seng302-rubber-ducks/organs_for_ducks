package odms.commons.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import odms.commons.model._enum.Directory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * Contains helper methods related to photos.
 */
public final class PhotoHelper {

    /**
     * sets up a temp folder and image folder (within temp folder).
     * then make a copy of the desired image and store it in the image folder.
     *
     * @param inFile desired image file
     * @param id the id of the user/clinician
     * @return filepath to the image file
     * @throws IOException if there are issues with handling files.
     */
    public static String setUpImageFile(File inFile, String id) throws IOException {
        BufferedImage bImage;

        String fileType = inFile.getName();
        fileType = fileType.substring(fileType.lastIndexOf(".") + 1);

        Files.createDirectories(Paths.get(Directory.TEMP.directory() + Directory.IMAGES));
        File outFile = new File(Directory.TEMP.directory() + Directory.IMAGES + "/" + id.toUpperCase() + "." + fileType);

        bImage = ImageIO.read(inFile);
        ImageIO.write(bImage, fileType, outFile);

        return outFile.getPath();
    }

    /**
     * displays the image
     * @param imageView JavaFX imageView object
     * @param filePath file path to the desired photo
     */
    public static void displayImage(ImageView imageView, String filePath){
        if(filePath != null) {
            Image image = new Image("file:" + filePath, 200, 200, false, true);
            imageView.setImage(image);
        }
    }

    /**
     * for deleting temp folder directory
     * @throws IOException if there are issues with handling files.
     */
    public static void deleteTempDirectory() throws IOException {
        try {
            Files.walk(Paths.get(Directory.TEMP.directory()))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (NoSuchFileException e) {
            Log.info("TEMP folder is already deleted or does not exist.");
        }
    }

    public static String createTempImageFile(byte[] image, String userId, String format) throws IOException {

        Files.createDirectories(Paths.get(Directory.TEMP.directory() + Directory.IMAGES));
        File outFile = new File(Directory.TEMP.directory() + Directory.IMAGES + "/" + userId.toUpperCase() + "." + format);
        InputStream in = new ByteArrayInputStream(image);
        BufferedImage buffImage = ImageIO.read(in);
        if(buffImage == null){
            return "";
        }
        ImageIO.write(buffImage, format, outFile);


        return outFile.getPath();
    }

    public static byte[] getBytesFromImage(String filepath) throws IOException {
        return Files.readAllBytes(Paths.get(filepath));
    }
}
