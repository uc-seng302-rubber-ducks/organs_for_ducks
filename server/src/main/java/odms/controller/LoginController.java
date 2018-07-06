package odms.controller;

import odms.security.TokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@OdmsController
public class LoginController {

    @Autowired
    TokenStore store;

    /*@RequestMapping(method = RequestMethod.POST, path = "/login")
    @ResponseBody
    public Object login(@RequestBody AuthDTO auth) {

    }*/
}
