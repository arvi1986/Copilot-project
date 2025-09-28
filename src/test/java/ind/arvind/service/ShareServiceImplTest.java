package ind.arvind.service;

import ind.arvind.dto.ShareRequestDto;
import ind.arvind.service.impl.ShareServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ind.arvind.repository.EmailRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShareServiceImplTest {
    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private ShareServiceImpl shareService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Optionally, set up default mock behavior here
    }

    @Test
    void share_Success() {
        ShareRequestDto dto = new ShareRequestDto();
        dto.setFolderpath("http://example.com");
        dto.setEmails(List.of("a@b.com"));
        assertDoesNotThrow(() -> shareService.share(dto));
    }

    @Test
    void getSharedEmails_Failure() {
        assertTrue(shareService.getSharedEmails("nonexistent").isEmpty());
    }
}
