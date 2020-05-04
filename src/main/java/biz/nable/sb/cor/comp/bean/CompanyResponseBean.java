package biz.nable.sb.cor.comp.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import biz.nable.sb.cor.common.utility.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CompanyResponseBean extends CompanyBean {

	private String createBy;
	private Date createdDate;
	private String lastModifiedBy;
	private Date lastModifiedDate;
	private String lastVerifiedBy;
	private Date lastVerifiedDate;
	private Long authorizationId;
	private List<Long> listOfLinkedCustIDs = new ArrayList<>();
	private StatusEnum status;

}
