package ind.arvind.repository;

import ind.arvind.entity.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
    List<StoredFile> findByOwner(String owner);
    List<StoredFile> findByStorageBucketId(Long bucketId);
}

