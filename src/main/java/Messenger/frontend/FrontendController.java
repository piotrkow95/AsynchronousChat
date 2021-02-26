package Messenger.frontend;

import Messenger.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Log
@RequiredArgsConstructor
@Controller
public class FrontendController {

    private final MessageService messageService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("allMessages",
                messageService.readAllPublicMessages());
        return "index";
    }
}
