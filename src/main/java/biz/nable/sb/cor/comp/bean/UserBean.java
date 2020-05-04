package biz.nable.sb.cor.comp.bean;

import java.util.List;

import lombok.Data;

@Data
public class UserBean {
	private String companyId;
	private String userName;
	private String branchCode;
	private String designation;
	private List<Integer> listOfWorkflowGroups;
	private List<Long> listOfFeatures;
	private List<Long> listOfAccounts;
	private String email;
	private String allAccountAccessFlag;
}
