package ro.ase.ro.api_oncohub_backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "consultation_access_manager")
public class ConsultationAccessManager {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "ID", columnDefinition = "uniqueidentifier")
    private UUID id;

    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "CONSULTATION_ID", nullable = false, unique = true)
    private Consultation consultation;

    @Column(name = "ISVIEWED", nullable = false)
    private Boolean isViewed = false;

    @Column(name = "VIEWED_AT")
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        if (Boolean.TRUE.equals(isViewed) && viewedAt == null) {
            viewedAt = LocalDateTime.now();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
                o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() :
                getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ConsultationAccessManager that = (ConsultationAccessManager) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
                getClass().hashCode();
    }
}
