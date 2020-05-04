package biz.nable.sb.cor.comp.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import biz.nable.sb.cor.comp.utility.YnFlagEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CompanyBean {
	private Long parentCompanyId;
	@NotEmpty(message = "companyName should not be empty")
	@NotNull(message = "companyName should not be null")
	private String companyName;
	private String contactNo;
	private String emailAddr;
	
	@NotEmpty(message = "contactPerson should not be empty")
	@NotNull(message = "contactPerson should not be null")
	private String contactPerson;
	
	@JsonIgnore
	private String createBy;
	@JsonIgnore
	private Date createDate;
	@JsonIgnore
	private String lastModifiedBy;
	@JsonIgnore
	private String lastVerifiedBy;
	private String epfCode;
	private String etfCode;
	private String epfAreaCode;
	private String commTemplateId;
	private YnFlagEnum mcAuthFlg;
	
	@NotEmpty(message = "canvassedBranch should not be empty")
	@NotNull(message = "canvassedBranch should not be null")
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
	@JsonIgnore
	private String userGroup;
	
	private Long corporatePaymentsLimit;
	
	@NotEmpty(message = "deviceLocation should not be empty")
	@NotNull(message = "deviceLocation should not be null")
	private String deviceLocation;
	
	@NotEmpty(message = "companyFeatures cannot be empty")
	private List<Long> companyFeatures = new ArrayList<>();
}
