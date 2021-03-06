package odms.controller;

import odms.commons.model._enum.EventTypes;
import odms.commons.model.dto.LoginResponse;
import odms.commons.utils.Log;
import odms.model.dto.LoginRequest;
import odms.security.AuthToken;
import odms.security.IsClinician;
import odms.security.TokenStore;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@OdmsController
public class LoginController extends BaseController {

    private final TokenStore store;
    private SocketHandler socketHandler;

    @Autowired
    public LoginController(TokenStore store, DBManager manager, SocketHandler socket) {
        super(manager);
        this.store = store;
        this.socketHandler = socket;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    @ResponseBody
    public Object login(@RequestBody LoginRequest auth) {
        boolean validLogin;
        try (Connection connection = getDriver().getConnection()) {
            String role = auth.getRole().toString();
            validLogin = getHandler().isValidLogIn(
                    connection, auth.getPassword(), auth.getUsername(), role);
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


    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public ResponseEntity testEndpoint() {
        try {
            socketHandler.broadcast(EventTypes.CLINICIAN_UPDATE, "0", "0");
        } catch (IOException e) {
            Log.warning("failed on test endpoint");
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/test2")
    public ResponseEntity testOtherEndpoint() {
        return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
    }
}
