package odms.controller;

import com.google.gson.Gson;
import odms.security.AuthToken;
import odms.security.TokenStore;
import odms.security.dto.AuthDTO;
import odms.security.dto.LoginResponse;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Object login(@RequestBody AuthDTO auth) {
        //TODO check auth.username is in database
        //TODO check auth.password against stored one in database
        System.out.println((new Gson()).toJson(auth));
        AuthToken authToken = new AuthToken(
                auth.getUsername(),
                auth.getRole().toString(),
                AuthToken.generateToken());
        store.add(authToken);
        return new LoginResponse(authToken.getToken());
    }


    @RequestMapping(method = RequestMethod.POST, path = "/logout")
    public ResponseEntity logout(@RequestHeader String token) {
        return new ResponseEntity(HttpStatus.OK);
    }
}
