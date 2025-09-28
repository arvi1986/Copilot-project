package ind.arvind.service;

import ind.arvind.dto.ShareRequestDto;
import java.util.List;

public interface ShareService {
    void share(ShareRequestDto requestDto);
    List<String> getSharedEmails(String folderpath);
}

