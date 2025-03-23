package ro.ase.ro.api_oncohub_backend.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;

import java.sql.Types;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PROTOCOL")
    private Long id;

    @Column(name = "PROTOCOL_CARC_MAM_INVAZ")
    private String protocolCarcMamInvaz;

    @Column(name = "ER")
    private Integer er;

    @Column(name = "PR")
    private Integer pr;

    @Column(name = "HER2")
    private Integer her2;

    @Column(name = "TNM")
    private String tnm;

    @Column(name = "HISTOLOGIC_TYPE")
    private String histologicType;

    @Column(name = "HISTOLOGIC_GRADE")
    private Integer histologicGrade;

    @Column(name = "CARCINOMA_IN_SITU")
    private String carcinomaInSitu;

    @Column(name = "NUCLEAR_HISTOLOGIC_GRADE")
    private String nuclearHistologicGrade;

    @Column(name = "DIAGNOSTIC")
    private String diagnostic;

    @Column(name = "KI67")
    private Integer ki67;

    @Column(name = "stage")
    private String stage;

    @Column(name = "FIRST_LINE_TREATMENT", columnDefinition = "nvarchar(MAX)")
    @JdbcTypeCode(Types.LONGNVARCHAR)
    private String firstLineTreatment;

    @Column(name = "SECOND_LINE_TREATMENT", columnDefinition = "nvarchar(MAX)")
    @JdbcTypeCode(Types.LONGNVARCHAR)
    private String secondLineTreatment;



    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Consultation that = (Consultation) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
