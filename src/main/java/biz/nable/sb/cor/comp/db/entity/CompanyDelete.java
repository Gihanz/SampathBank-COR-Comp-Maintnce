package biz.nable.sb.cor.comp.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import biz.nable.sb.cor.common.db.audit.Auditable;
import biz.nable.sb.cor.comp.utility.McAuthEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "SB_COR_COMPANY_DELETE")
public class CompanyDelete extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_DELETE_SEQ")
	@SequenceGenerator(name = "COMPANY_DELETE_SEQ", sequenceName = "SB_COR_COMPANY_DELETE_SEQ", allocationSize = 1)
	private Long id;
	@Column(length = 9)
	private String companyId;
	@Column(length = 9)
	private Long parentCompanyId;
	@Column(length = 80)
	private String companyName;
	@Column(length = 100)
	private String address1;
	@Column(length = 100)
	private String address2;
	@Column(length = 40)
	private String city;
	@Column(length = 50)
	private String country;
	@Column(length = 20)
	private String contactNo;
	@Column(length = 12)
	private String faxNo;
	@Column(length = 80)
	private String emailAddr;
	@Column(length = 50)
	private String contactPerson;
	@Column(length = 10)
	private String epfCode;
	@Column(length = 10)
	private String etfCode;
	@Column(length = 10)
	private String epfAreaCode;
	@Column(length = 15)
	private String commTemplateId;
	private McAuthEnum mcAuthFlg;
	@Column(length = 3)
	private String canvassedBranch;
	private Long canvassedUser;
	@Column(length = 15)
	private String treasureCustRef;
	private String mobileCashAuthorization;
	private String webServiceActivationFlag;
	private String bulkDirectDebitFlg;
	private Double bulkPaymentLimit;
	@Column(length = 15)
	private String wsIp;
	@Column(length = 15)
	private String requestId;
	@Column(length = 3)
	private String districtCode;

}
