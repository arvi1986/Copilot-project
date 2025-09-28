package ind.arvind.repository;

import ind.arvind.entity.StorageBucket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StorageBucketRepository extends JpaRepository<StorageBucket, Long> {
    Optional<StorageBucket> findByName(String name);
}
