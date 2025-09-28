package ind.arvind.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stored_file_id")
    private StoredFile storedFile;

    @Column(name = "meta_key", nullable = false)
    private String metaKey;

    @Column(name = "meta_value", nullable = false)
    private String value;
}
