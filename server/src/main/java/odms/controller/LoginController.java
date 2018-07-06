package odms.controller;

import odms.security.Token;
import odms.security.TokenStore;
import odms.security.dto.AuthDTO;
import odms.security.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@OdmsController
public class LoginController {

    @Autowired
    TokenStore store;

    @RequestMapping(method = RequestMethod.POST, path = "/login")
    @ResponseBody
    public Object login(@RequestBody AuthDTO auth) {
        //TODO check auth.username is in database
        //TODO check auth.password against stored one in database
        Token token = new Token(auth.getUsername(), auth.getRole().toString());
        store.add(token);
        return new LoginResponse(token.getToken());
    }
}
