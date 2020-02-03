package biz.nable.sb.cor.comp.db.entity;


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
@Table(name = "SB_COR_COMPANY_MST_HIS")
public class CompanyMstHis extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_MST_HIS_SEQ")
	@SequenceGenerator(name = "COMPANY_MST_HIS_SEQ", sequenceName = "SB_COR_COMPANY_MST_HIS_SEQ", allocationSize = 1)
	private Long id;
	private Long mstId;
	private String companyId;
	private String parentCompanyId;
	private String companyName;
	private String address1;
	private String address2;
	private String city;
	private String country;
	private String contactNo;
	private String faxNo;
	private String emailAddr;
	private String contactPerson;
	private String createBy;
	private String lastModifiedBy;
	private String lastVerifiedBy;
	private String epfCode;
	private String etfCode;
	private String commTemplateId;
	private McAuthEnum mcAuthFlg;
	private String canvassedBranch;
	private String canvassedUser;
	private String treasureCustRef;
	private String bulkDirectDebitFlg;
	private Double bulkPaymentLimit;
	private String wsIp;

}
