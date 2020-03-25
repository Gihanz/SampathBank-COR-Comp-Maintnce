package biz.nable.sb.cor.comp.response;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class UserCommonResponse {

	private Long id;
	private String userName;
	private String designation;
	private Long branch;
	private String remark;
	private StatusEnum status;
	private RecordStatusEnum recordStatus;
	private String createdBy;
	private Date createdDate;
	private String lastUpdatedBy;
	private Date lastUpdatedDate;
	private Date lastVerifiedDate;
	private String lastVerifiedBy;
	private String userGroup;
	private List<CompanyUserResponse> companyUsers;

}
