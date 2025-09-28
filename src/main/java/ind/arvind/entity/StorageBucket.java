package ind.arvind.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "storage_bucket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageBucket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bucket_name", nullable = false, unique = true)
    private String name;

    @Column(name = "bucket_description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "bucket_owner", nullable = false)
    private String owner;

    @OneToMany(mappedBy = "storageBucket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoredFile> files;
}
