package odms.controller;

import odms.commons.model.User;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.utils.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhotoControllerTest {
    private PhotoController controller;
    private Connection connection;
    private JDBCDriver driver;
    private DBManager manager;
    private DBHandler handler;
    private User testUser;
    private static final String JPG_PHOTO_TEST_FILE_PATH = "../commons/src/test/java/resources/images/duck_jpg.jpg";
    private static final String PNG_PHOTO_TEST_FILE_PATH = "../commons/src/test/java/resources/images/duck_png.png";
    private static final String NOT_PHOTO_TEST_FILE_PATH = "../commons/src/test/java/resources/images/not_image.txt";
    private static final String TWO_MB_PHOTO_TEST_FILE_PATH = "../commons/src/test/java/resources/images/sample_2mb_image.jpg";

    @Before
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        driver = mock(JDBCDriver.class);
        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(manager.getDriver()).thenReturn(driver);
        controller = new PhotoController(manager);
        testUser = new User("steve", LocalDate.now(), "ABC1234");
    }

    @Test
    public void putUserProfilePhotoJpgTest() throws SQLException, IOException{
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);

        Path path = Paths.get(JPG_PHOTO_TEST_FILE_PATH);
        MultipartFile multipartFile = new MockMultipartFile("duck_jpg.jpg", "duck_jpg.jpg", "image/jpg", Files.readAllBytes(path));
        ResponseEntity res = controller.putProfilePhoto("ABC1234", multipartFile);
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void putUserProfilePhotoPngTest() throws SQLException, IOException{
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);

        Path path = Paths.get(PNG_PHOTO_TEST_FILE_PATH);
        MultipartFile multipartFile = new MockMultipartFile("duck_png.jpg", "duck_png.jpg", "image/png", Files.readAllBytes(path));
        ResponseEntity res = controller.putProfilePhoto("ABC1234", multipartFile);
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void putUserProfilePhotoUserNotFoundTest() throws SQLException, IOException{
        Path path = Paths.get(JPG_PHOTO_TEST_FILE_PATH);
        MultipartFile multipartFile = new MockMultipartFile("duck_jpg.jpg", "duck_jpg.jpg", "image/jpg", Files.readAllBytes(path));
        ResponseEntity res = controller.putProfilePhoto("ZZZ1111", multipartFile);
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void putUserProfilePhotoInvalidFileTypeTest() throws SQLException, IOException{
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);

        Path path = Paths.get(NOT_PHOTO_TEST_FILE_PATH);
        MultipartFile multipartFile = new MockMultipartFile("not_image.txt", "not_image.txt", "text/plain", Files.readAllBytes(path));
        ResponseEntity res = controller.putProfilePhoto("ABC1234", multipartFile);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void putUserProfilePhoto2MBFileTest() throws SQLException, IOException{
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);

        Path path = Paths.get(TWO_MB_PHOTO_TEST_FILE_PATH);
        MultipartFile multipartFile = new MockMultipartFile("sample_2mb_image.jpg", "sample_2mb_image.jpg", "image/jpg", Files.readAllBytes(path));
        ResponseEntity res = controller.putProfilePhoto("ABC1234", multipartFile);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }
}
