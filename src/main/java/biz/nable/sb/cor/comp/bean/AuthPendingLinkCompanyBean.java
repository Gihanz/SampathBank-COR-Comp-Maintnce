package biz.nable.sb.cor.comp.bean;

import java.util.ArrayList;
import java.util.List;

import biz.nable.sb.cor.common.response.CommonResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthPendingLinkCompanyBean extends CommonResponse {

	public AuthPendingLinkCompanyBean(int returnCode, String returnMessage, String errorCode) {
		super(returnCode, returnMessage, errorCode);
	}

	public AuthPendingLinkCompanyBean() {

	}

	private List<LinkCompanyResponseBean> newCustomers = new ArrayList<>();
	private List<LinkCompanyResponseBean> deletedCustomers = new ArrayList<>();
}
