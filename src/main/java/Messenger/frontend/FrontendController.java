package Messenger.frontend;

import Messenger.frontend.dto.SendMessageDto;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Log
@Controller
public class FrontendController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("czas", System.currentTimeMillis());
        return "index";
    }

    @PostMapping("/")
    public String receiveMessage(Model model, @ModelAttribute SendMessageDto messageDto){
        log.info("New message: " + messageDto);

        return "redirect:/";
    }
}
