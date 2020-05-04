package biz.nable.sb.cor.comp.factory;

import biz.nable.sb.cor.comp.factory.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import biz.nable.sb.cor.common.template.CommonApprovalTemplate;
import biz.nable.sb.cor.comp.utility.RequestTypeEnum;

@Component
public class ApprovalTypeFactory {

	@Autowired
	CompanyApproval company;

	@Autowired
	LinkCompanyApproval linkCompanyApproval;

	@Autowired
	BranchApproval branchApproval;

	@Autowired
	UserApproval userApproval;

	@Autowired
	UserLinkCompanyApproval userLinkCompanyApproval;

	public CommonApprovalTemplate getApproval(String type) {
		if (null == type) {
			return null;
		} else if (RequestTypeEnum.COMPANY.name().equalsIgnoreCase(type)) {
			return company;
		} else if (RequestTypeEnum.LINK_COMPANY.name().equalsIgnoreCase(type)) {
			return linkCompanyApproval;
		} else if (RequestTypeEnum.BRANCH.name().equalsIgnoreCase(type)) {
			return branchApproval;
		} else if (RequestTypeEnum.USER.name().equalsIgnoreCase(type)){
			return userApproval;
		}else if (RequestTypeEnum.LINK_USER.name().equalsIgnoreCase(type)){
			return userLinkCompanyApproval;
		}else {
			return null;
		}
	}
}
