package biz.nable.sb.cor.comp.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "SB_COR_USER_FEATURES")
public class UserFeatures extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_FET_SEQ")
	@SequenceGenerator(name = "USER_FET_SEQ", sequenceName = "SB_COR_USER_FEA", allocationSize = 1)
	private Long id;
	private String corpUserId;
	private String companyFeatureId;

}
