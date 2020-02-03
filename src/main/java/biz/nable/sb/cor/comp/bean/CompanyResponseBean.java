package biz.nable.sb.cor.comp.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.utility.McAuthEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CompanyResponseBean {
	private Long parentCompanyId;
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
	private Date createdDate;
	private String lastModifiedBy;
	private String lastVerifiedBy;
	private Date lastVerifiedDate;
	private String epfCode;
	private String etfCode;
	private String epfAreaCode;
	private String commTemplateId;
	private McAuthEnum mcAuthFlg;
	private String canvassedBranch;
	private Long canvassedUser;
	private String treasureCustRef;
	private String mobileCashAuthorization;
	private String webServiceActivationFlag;
	private String bulkDirectDebitFlg;
	private Double bulkPaymentLimit;
	private String wsIp;
	private String districtCode;
	private String requestId;
	private Long authorizationId;
	private List<Long> listOfLinkedCustIDs = new ArrayList<>();
	private StatusEnum status;
}
