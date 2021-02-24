package Messenger.frontend;

import Messenger.frontend.dto.SendMessageDto;
import Messenger.model.Message;
import Messenger.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Log
@RequiredArgsConstructor
@Controller
public class FrontendController {

    private static final DateTimeFormatter FORMATTER_TIMESTAMP =
            DateTimeFormatter.ofPattern("hh:mm:ss");

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

    private List<String> convertMessagesToText(List<Message> messages) {
        return messages.stream()
                .map(m -> m.getTimestamp().format(FORMATTER_TIMESTAMP) + " " +
                        m.getUsername() + " " +
                        m.getText())
                .collect(Collectors.toList());
    }
}
