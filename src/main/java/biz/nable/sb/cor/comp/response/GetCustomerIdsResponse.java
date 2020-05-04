package biz.nable.sb.cor.comp.response;

import java.util.ArrayList;
import java.util.List;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.CustomerIdResponseBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetCustomerIdsResponse extends CommonResponse {
	List<CustomerIdResponseBean> listOfLinkedCompanies = new ArrayList<>();

	public GetCustomerIdsResponse(int returnCode, String returnMessage, String errorCode) {
		super(returnCode, returnMessage, errorCode);
	}
}
