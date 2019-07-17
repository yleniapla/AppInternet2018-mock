package it.polito.ai.server.controller;


import it.polito.ai.server.model.RegistrationRequest;
import it.polito.ai.server.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistrationController {

    @Autowired
    UserDetailsServiceImpl service;
    public RegistrationController() {
    }

    @RequestMapping(
            value = {"/registration"},
            method = {RequestMethod.POST}
    )
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    void getRegistration(@RequestBody RegistrationRequest req) throws Exception {
        this.service.register(req.getUsername(), req.getPassword());
    }
}
