package biz.nable.sb.cor.comp.db.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import biz.nable.sb.cor.common.db.audit.Auditable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "SB_COR_FEATURES")
public class Features extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FEATURE_SEQ")
	@SequenceGenerator(name = "FEATURE_SEQ", sequenceName = "SB_COR_FEATURES_SEQ", allocationSize = 1)
	private Long id;
	private String description;

	@OneToMany(mappedBy = "feature")
	List<CompanyFeatures> companyFeatures;
}
