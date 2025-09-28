package ind.arvind.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email_id", nullable = false, unique = true)
    private String emailId;

    public Email() {}

    public Email(String emailId) {
        this.emailId = emailId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}

