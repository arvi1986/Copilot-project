package ind.arvind.config;

import ind.arvind.entity.StorageBucket;
import ind.arvind.repository.StorageBucketRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class StorageBucketInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(StorageBucketInitializer.class);
    private final StorageBucketRepository storageBucketRepository;
    private static final String DEFAULT_BUCKET = "my-bucket";
    private static final String DEFAULT_OWNER = "system";

    @Override
    public void run(ApplicationArguments args) {
        String bucketName = args.containsOption("bucket.name") ?
                args.getOptionValues("bucket.name").get(0) : DEFAULT_BUCKET;
        String owner = args.containsOption("bucket.owner") ?
                args.getOptionValues("bucket.owner").get(0) : DEFAULT_OWNER;
        storageBucketRepository.findByName(DEFAULT_BUCKET).ifPresentOrElse(
            bucket -> log.info("Bucket '{}' already exists.", DEFAULT_BUCKET),
            () -> {
                StorageBucket bucket = StorageBucket.builder()
                        .name(bucketName)
                        .description("Default bucket created at startup.")
                        .createdAt(Instant.now())
                        .owner(owner)
                        .build();
                storageBucketRepository.save(bucket);
                log.info("Created default bucket '{}'.", DEFAULT_BUCKET);
            }
        );
    }
}

