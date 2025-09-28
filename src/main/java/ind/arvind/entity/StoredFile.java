package ind.arvind.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "stored_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String filename;

    @Column(name = "file_size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @OneToMany(mappedBy = "storedFile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileMetadata> metadata;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "file_owner", nullable = false)
    private String owner;

    @ManyToOne
    @JoinColumn(name = "storage_bucket_id")
    private StorageBucket storageBucket;
}
