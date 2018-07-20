package odms.controller;

import com.google.gson.Gson;
import odms.security.AuthToken;
import odms.security.IsAdmin;
import odms.security.IsClinician;
import odms.security.TokenStore;
import odms.model.dto.LoginRequest;
import odms.commons.model.dto.LoginResponse;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@OdmsController
public class LoginController extends BaseController {

    private final TokenStore store;

    @Autowired
    public LoginController(TokenStore store, DBManager manager) {
        super(manager);
        this.store = store;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    @ResponseBody
    public Object login(@RequestBody LoginRequest auth) {
        boolean validLogin;
        try {
            String role = auth.getRole().toString();
            validLogin = getHandler().isVaildLogIn(
                    getDriver().getConnection(), auth.getPassword(), auth.getUsername(),role);
        } catch (SQLException e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!validLogin){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        AuthToken authToken = new AuthToken(
                auth.getUsername(),
                auth.getRole().toString(),
                AuthToken.generateToken());
        store.add(authToken);
        return new LoginResponse(authToken.getToken());
    }


    @RequestMapping(method = RequestMethod.POST, path = "/logout")
    public ResponseEntity logout(@RequestHeader String token) {
        store.remove(token);
        return new ResponseEntity(HttpStatus.OK);
    }


    @IsAdmin
    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public ResponseEntity testEndpoint() {
        return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/test2")
    public ResponseEntity testOtherEndpoint() {
        return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
    }
}
