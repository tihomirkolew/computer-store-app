package computer_store_app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping
public class IndexController {

    @GetMapping("/")
    public ModelAndView getIndexPage() {
        return new ModelAndView("index");
    }

    @GetMapping("/contact")
    public ModelAndView getContactsPage() {
        return new ModelAndView("contact");
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        return new ModelAndView("login");
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        return new ModelAndView("register");
    }

}
