package ind.arvind.service.impl;

import ind.arvind.dto.ShareRequestDto;
import ind.arvind.service.ShareService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ind.arvind.entity.Email;
import ind.arvind.repository.EmailRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    private final EmailRepository emailRepository;

    @Override
    @Retry(name = "shareServiceRetry")
    @CircuitBreaker(name = "shareServiceCB")
    public void share(ShareRequestDto requestDto) {
        // Simulate sending notification (e.g., email)
        log.info("Sharing folder {} with emails {}", requestDto.getFolderpath(), requestDto.getEmails());
        if (requestDto.getEmails() != null) {
            for (String emailId : requestDto.getEmails()) {
                Email email = new Email();
                email.setEmailId(emailId);
                emailRepository.save(email);
            }
        }
    }

    @Override
    @Retry(name = "shareServiceRetry")
    @CircuitBreaker(name = "shareServiceCB")
    public List<String> getSharedEmails(String folderpath) {
        List<Email> emails = emailRepository.findAll();
        List<String> emailIds = new ArrayList<>();
        for (Email email : emails) {
            emailIds.add(email.getEmailId());
        }
        return emailIds;
    }
}
