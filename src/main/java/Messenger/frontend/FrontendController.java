package Messenger.frontend;

import Messenger.services.MessageService;
import Messenger.services.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;


@Log
@RequiredArgsConstructor
@Controller
public class FrontendController {
    private final MessageService messageService;
    private final PresenceService presenceService;

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        model.addAttribute("principalName", principal == null ? "anonymous" : principal.getName());
        return "index";
    }

    @GetMapping("/chat")
    public String chat(Model model) {
        model.addAttribute("allMessages",
                messageService.readAllPublicMessages());
        model.addAttribute("allActiveUsers", presenceService.getAllActiveUsers());
        return "chat";
    }
}
