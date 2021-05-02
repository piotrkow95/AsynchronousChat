package Messenger.frontend;

import Messenger.services.FileStorageService;
import Messenger.model.Message;
import Messenger.model.UploadFileResponse;
import Messenger.services.MessageService;
import Messenger.services.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log
@RestController
public class MessageRestController {
    private final MessageService messageService;
    private final PresenceService presenceService;
    private final FileStorageService fileStorageService;

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
        List<Message> conversationMessages = messageService.getConversationMessages(user, principalName);
        if (conversationMessages == null) {
            conversationMessages = Collections.emptyList();
        }
        log.info("fetchChatHistory: returning " + conversationMessages);
        return conversationMessages;
    }
    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws FileNotFoundException {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
