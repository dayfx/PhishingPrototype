package at.daniel.phishingprototype;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping("/welcome")
    public String welcome(){
        return "Welcome to Daniel";
    }

}
