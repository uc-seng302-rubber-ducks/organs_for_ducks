package odms.controller;

import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.security.IsUser;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@OdmsController
public class PhotoController extends BaseController {

    private DBHandler handler;
    private JDBCDriver driver;

    public PhotoController(DBManager manager) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
    }

    @IsUser
    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}/photo")
    public ResponseEntity putUserProfilePhoto(@PathVariable String nhi, @RequestBody MultipartFile profileImageFile) {
        if(isNotValidImageFile(profileImageFile)){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try (Connection connection = driver.getConnection()) {
            User toModify = handler.getOneUser(connection, nhi);
            if (toModify == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            handler.updateProfilePhoto(User.class, toModify.getNhi(), profileImageFile.getInputStream(), connection);

        } catch (SQLException ex) {
            Log.severe("Could not add or update user's profile photo to user " + nhi, ex);
            throw new ServerDBException(ex);

        } catch (IOException ex) {
            Log.severe("Could not add or update user's profile photo to user " + nhi, ex);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.PUT, value = "/clinicians/{staffId}/photo")
    public ResponseEntity putClinicianProfilePhoto(@PathVariable String staffId, @RequestBody MultipartFile profileImageFile) {
        if(isNotValidImageFile(profileImageFile)){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try (Connection connection = driver.getConnection()) {
            Clinician toModify = handler.getOneClinician(connection, staffId);
            if (toModify == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            handler.updateProfilePhoto(Clinician.class, toModify.getStaffId(), profileImageFile.getInputStream(), connection);

        } catch (SQLException ex) {
            Log.severe("Could not add or update clinician's profile photo to clinician " + staffId, ex);
            throw new ServerDBException(ex);

        } catch (IOException ex) {
            Log.severe("Could not add or update clinician's profile photo to clinician " + staffId, ex);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    /**
     * Validates image file
     *
     * @param profileImageFile desired image file
     * @return true if invalid, false otherwise
     */
    private boolean isNotValidImageFile(MultipartFile profileImageFile){
        String fileType = profileImageFile.getContentType();

        if((!fileType.equalsIgnoreCase("image/jpg") && !fileType.equalsIgnoreCase("image/png")) || profileImageFile.getSize() > 2000000){
            return true;
        }
        return false;
    }
}
