package ind.arvind.controller;

import ind.arvind.dto.ShareRequestDto;
import ind.arvind.service.ShareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ShareController {
    private static final Logger log = LoggerFactory.getLogger(ShareController.class);
    private static final String LOG_SHARE_REQUEST = "Received share request for folderpath: {}";
    private static final String LOG_GET_EMAILS_REQUEST = "Received get shared emails request for folderpath: {}";
    private final ShareService shareService;

    @PostMapping("share")
    public ResponseEntity<Void> share(@Valid @RequestBody ShareRequestDto requestDto) {
        log.info(LOG_SHARE_REQUEST, requestDto.getFolderpath());
        shareService.share(requestDto);
        log.info("Share request processed for folderpath: {}", requestDto.getFolderpath());
        return ResponseEntity.ok().build();
    }

    @GetMapping("shared-emails")
    public ResponseEntity<List<String>> getSharedEmails(@RequestParam("folderpath") String folderpath) {
        log.info(LOG_GET_EMAILS_REQUEST, folderpath);
        List<String> emails = shareService.getSharedEmails(folderpath);
        log.info("Returning {} shared emails for folderpath: {}", emails.size(), folderpath);
        return ResponseEntity.ok(emails);
    }
}
