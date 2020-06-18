package bg.bulsi.egov.eauth.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import bg.bulsi.egov.eauth.model.ekkate.Ekatte;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "address", schema = "profile")
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class Address implements Persistable<Long>, Serializable {

	private static final long serialVersionUID = 6205937777935027035L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    //@EqualsAndHashCode.Exclude
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "ekatte", foreignKey = @ForeignKey(name = "fk_ekatte"))
    @Getter
    @Setter
    private Ekatte ekatte;

    @Column(name = "address")
    @Getter
    @Setter
    private String addressDescription;

    @Column(name = "postal_code")
    @Getter
    @Setter
    private String postalCode;

    @OneToOne(mappedBy = "address")
    @Getter
    @Setter
    private User user;
    
	@Override
	public boolean isNew() {
		return false;
	}

}