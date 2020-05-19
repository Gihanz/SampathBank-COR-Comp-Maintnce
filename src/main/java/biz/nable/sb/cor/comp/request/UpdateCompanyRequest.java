package biz.nable.sb.cor.comp.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import biz.nable.sb.cor.common.bean.CommonTempBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateCompanyRequest implements CommonTempBean {

	private String companyId;
	
	@Size(min = 1 , message = "companyName should not be empty")
	@NotNull(message = "companyName should not be null")
	private String companyName;
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
	
	@NotNull(message = "corporatePaymentsLimit can not be null")
	private Long corporatePaymentsLimit;
	
	@Size(min = 1 , message = "deviceLocation should not be empty")
	@NotNull(message = "deviceLocation should not be null")
	private String deviceLocation;
	
	@Size(min = 1 , message = "contactPerson should not be empty")
	@NotNull(message = "contactPerson should not be null")
	private String contactPerson;
	
	@Size(min = 1 , message = "canvassedBranch should not be empty")
	@NotNull(message = "canvassedBranch should not be null")
	private String canvassedBranch;
	
	@Size(min = 1 , message = "canvassedUser should not be empty")
	@NotNull(message = "canvassedUser can not be null")
	private String canvassedUser;
	private Double bulkPaymentLimit;
	private String wsIp;

	@JsonIgnore
	protected String lastUpdatedBy;
	@JsonIgnore
	protected Date lastUpdatedDate;
	

	private List<Long> companyFeatures = new ArrayList<>();
}
