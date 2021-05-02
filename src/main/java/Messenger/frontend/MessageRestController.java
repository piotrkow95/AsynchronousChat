package Messenger.frontend;

import Messenger.model.Message;
import Messenger.services.MessageService;
import Messenger.services.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Log
@RestController
public class MessageRestController {
    private final MessageService messageService;
    private final PresenceService presenceService;

    @GetMapping(path = "/fetchChatHistory")
    public List<Message> fetchChatHistory(@RequestParam @Nullable String user, Principal principal, HttpServletRequest request) {
        String principalName = null;
        if (principal == null) {
            Cookie jsessionid = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("JSESSIONID")).findFirst().get();
            log.info("Cookies: " + jsessionid.getValue());
            principalName = presenceService.getUserByCookie(jsessionid.getValue()).getPrincipalName();
        } else {
            principalName = principal.getName();
        }
        log.info("Received fetchChatHistory for user " + user + " from principal: " + principalName);
        return messageService.getConversationMessages(user, principalName);
    }
}
