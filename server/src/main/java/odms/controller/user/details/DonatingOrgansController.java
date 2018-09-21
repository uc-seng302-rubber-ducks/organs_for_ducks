package odms.controller.user.details;

import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ExpiryReason;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.utils.Log;
import odms.controller.BaseController;
import odms.controller.OdmsController;
import odms.database.DBHandler;
import odms.database.DisqualifiedOrgansHandler;
import odms.database.JDBCDriver;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

@OdmsController
public class DonatingOrgansController extends BaseController {
    private JDBCDriver driver;
    private DBHandler handler;
    private DisqualifiedOrgansHandler disqualifiedOrgansHandler;

    public DonatingOrgansController(DBManager manager) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
        disqualifiedOrgansHandler = new DisqualifiedOrgansHandler();
    }


    @RequestMapping(method = RequestMethod.POST, value = "/users/{nhi}/donating")
    public ResponseEntity postDonatingOrgans(@PathVariable String nhi,
                                             @RequestBody Map<Organs, ExpiryReason> donating) {
        try (Connection connection = driver.getConnection()) {
            User toModify = handler.getOneUser(connection, nhi);
            if (toModify == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            for (Map.Entry<Organs, ExpiryReason> entry: donating.entrySet()) {
                toModify.getDonorDetails().addOrgan(entry.getKey(), entry.getValue());
            }
            handler.saveUser(toModify, connection);
        } catch (SQLException ex) {
            Log.severe("Could not post donating organs to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}/donating")
    public ResponseEntity putDonatingOrgans(@PathVariable String nhi,
                                            @RequestBody Map<Organs, ExpiryReason> donating) {
        try (Connection connection = driver.getConnection()) {
            User toModify = handler.getOneUser(connection, nhi);
            if (toModify == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            toModify.getDonorDetails().setOrgans(donating);
            handler.saveUser(toModify, connection);
        } catch (SQLException ex) {
            Log.severe("Could not put donating organs to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/users/{nhi}/disqualified")
    public Collection<OrgansWithDisqualification> getDisqualifiedOrgans(@PathVariable String nhi) {
        try (Connection connection = driver.getConnection()) {
            return disqualifiedOrgansHandler.getDisqualifiedOrgans(connection, nhi);
        } catch (SQLException ex) {
            Log.severe("could not get disqualified organs for user " + nhi, ex);
            throw new ServerDBException(ex);
        }
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.POST, value = "/users/{nhi}/disqualified")
    public ResponseEntity postDisqualifiedOrgan(@PathVariable String nhi,
                                             @RequestBody Collection<OrgansWithDisqualification> disqualified) {
        try (Connection connection = driver.getConnection()) {
            disqualifiedOrgansHandler.postDisqualifiedOrgan(connection, disqualified, nhi);
        } catch (SQLException ex) {
            Log.severe("Could not post disqualified organs to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.DELETE, value = "/users/{nhi}/disqualified")
    public ResponseEntity deleteDisqualifiedOrgan(@PathVariable String nhi,
                                                  @RequestBody Collection<OrgansWithDisqualification> disqualified) {
        try (Connection connection = driver.getConnection()) {

            disqualifiedOrgansHandler.deleteDisqualifiedOrgan(connection, disqualified);
        } catch (SQLException ex) {
            Log.severe("Could not delete disqualified organs for user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
    }
}
