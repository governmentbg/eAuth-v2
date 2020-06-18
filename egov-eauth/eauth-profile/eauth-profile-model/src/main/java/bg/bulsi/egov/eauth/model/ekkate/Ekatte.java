package bg.bulsi.egov.eauth.model.ekkate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ekatte", schema = "profile")
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class Ekatte implements Persistable<String>, Serializable {

	private static final long serialVersionUID = 3579125705165496727L;

	@Getter
	@Setter
	@Id
	@Column(name = "ekatte", length = 5)
	private String ekatteId;

	@Getter
	@Setter
	private String area;

	@Getter
	@Setter
	private String municipality;

	@Getter
	@Setter
	private String place;

	@Getter
	@Setter
	@Column(name = "area_code", length = 50)
	private String areaCode;

	@Getter
	@Setter
	@Column(name = "municipality_code", length = 50)
	private String municipalityCode;

	@Getter
	@Setter
	@Column(name = "place_type", length = 50)
	@Enumerated(EnumType.STRING)
	private PlaceType placeType;

	@Getter
	@Setter
	@EqualsAndHashCode.Exclude
	private Integer population = 0;

	@Getter
	@Setter
	@Column(name = "planning_area", length = 50)
	@EqualsAndHashCode.Exclude
	private String planningArea;


	@Override
	public String getId() {
		return this.ekatteId;
	}


	@Override
	public boolean isNew() {
		return StringUtils.isEmpty(this.ekatteId);
	}
}
