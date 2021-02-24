package Messenger.frontend;

import Messenger.frontend.dto.SendMessageDto;
import Messenger.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Log
@RequiredArgsConstructor
@Controller
public class FrontendController {


    private final MessageService messageService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("allMessages",
                messageService.readAllPublicMessages());
        model.addAttribute("newMessage", new SendMessageDto());
        return "index";
    }

    @PostMapping("/")
    public String receiveMessage(Model model, @ModelAttribute SendMessageDto messageDto){
        log.info("New message: " + messageDto);

        messageService.postPublicMessage(messageDto);
        return "redirect:/";
    }
}
