package odms.controller;

import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;

@OdmsController
public class PhotoController extends BaseController {

    private static final int MAX_FILE_SIZE = 2000000;
    private DBHandler handler;
    private JDBCDriver driver;

    public PhotoController(DBManager manager) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}/photo")
    public ResponseEntity putUserProfilePhoto(@PathVariable String nhi, @RequestBody byte[] profileImageFile, @RequestHeader(value = "Content-Type") String header) {

        try (Connection connection = driver.getConnection()) {
            User toModify = handler.getOneUser(connection, nhi);
            if (toModify == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(profileImageFile);
            if(inputStream.available() <= 14 || inputStream.available() > MAX_FILE_SIZE){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            handler.updateProfilePhoto(User.class, nhi, inputStream, header, connection);

        } catch (SQLException ex) {
            Log.severe("Could not add or update user's profile photo to user " + nhi, ex);
            throw new ServerDBException(ex);

        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.PUT, value = "/clinicians/{staffId}/photo")
    public ResponseEntity putClinicianProfilePhoto(@PathVariable String staffId, @RequestBody byte[] profileImageFile,  @RequestHeader(value = "Content-Type") String header) {

        try (Connection connection = driver.getConnection()) {
            Clinician toModify = handler.getOneClinician(connection, staffId);
            if (toModify == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(profileImageFile);
            if(inputStream.available() <= 14 || inputStream.available() > MAX_FILE_SIZE){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            handler.updateProfilePhoto(Clinician.class, toModify.getStaffId(),inputStream,header, connection);

        } catch (SQLException ex) {
            Log.severe("Could not add or update clinician's profile photo to clinician " + staffId, ex);
            throw new ServerDBException(ex);

        }
        return new ResponseEntity(HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/users/{nhi}/photo", produces = {"image/png","image/jpeg","image/gif"})
    public ResponseEntity<byte[]> getUserProfilePicture(@PathVariable("nhi") String nhi) {
        byte[] image;
        String format;
        MediaType mediaType;
        try (Connection connection = driver.getConnection()) {
            image = handler.getProfilePhoto(User.class, nhi, connection);
            format = handler.getFormat(User.class, nhi, connection);
            mediaType = getMediaType(format);

        } catch (SQLException e) {
            Log.severe("Cannot fetch profile picture for user " + nhi, e);
            throw new ServerDBException(e);
        }
        return ResponseEntity.ok().contentType(mediaType).body(image);
    }

    private MediaType getMediaType(String format) {
        MediaType mediaType = MediaType.IMAGE_PNG;
        if(format == null || format.equals("")){
            mediaType = MediaType.IMAGE_PNG;
        } else if(format.equals("image/png")){
            mediaType = MediaType.IMAGE_PNG;
        } else if(format.equals("image/jpg")){
            mediaType = MediaType.IMAGE_JPEG;
        } else if(format.equals("image/gif")){
            mediaType = MediaType.IMAGE_GIF;
        }
        return mediaType;
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/clinicians/{staffId}/photo", produces = "image/png")
    public ResponseEntity<byte[]> getClinicianProfilePicture(@PathVariable("staffId") String staffId) {
        byte[] image;
        String format;
        MediaType mediaType;
        try (Connection connection = driver.getConnection()) {
            image = handler.getProfilePhoto(Clinician.class, staffId, connection);
            format = handler.getFormat(Clinician.class, staffId, connection);
            mediaType = getMediaType(format);

        } catch (SQLException e) {
            Log.severe("Cannot fetch profile picture for user " + staffId, e);
            throw new ServerDBException(e);
        }
        return ResponseEntity.ok().contentType(mediaType).body(image);
    }


}
