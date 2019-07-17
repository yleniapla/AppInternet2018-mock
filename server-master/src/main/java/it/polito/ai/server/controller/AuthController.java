package it.polito.ai.server.controller;

import it.polito.ai.server.model.RegistrationRequest;
import it.polito.ai.server.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {


    @Autowired
    UserDetailsServiceImpl service;

    public AuthController() {
    }

    @RequestMapping(
            value = {"/login"},
            method = {RequestMethod.POST}
    )
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    void getLogin(@RequestBody String usr, String pwd) throws Exception {
        this.service.login(usr, pwd);
    }

}

