package biz.nable.sb.cor.comp.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateCompanyRequest implements CommonTempBean {
//	private String address1;
//	private String address2;
//	private String city;
//	private String country;

	private String contactNo;
	private String emailAddr;
	private String epfCode;
	private String etfCode;
	private String epfAreaCode;
	private String districtCode;
	private String treasureCustRef;
	private String mobileCashAuthorization;
	private String webServiceActivationFlag;
	private String bulkDirectDebitFlg;
	private String contactPerson;
	private String canvassedBranch;
	private Long canvassedUser;
	private Long authorizationLevels;
	private Double bulkPaymentLimit;
	private String wsIp;

	@JsonIgnore
	protected String lastUpdatedBy;
	@JsonIgnore
	protected Date lastUpdatedDate;

//	private String faxNo;
//	private String commTemplateId;
//	private McAuthEnum mcAuthFlg;
//	private String requestId;
}
