package biz.nable.sb.cor.comp.response;

import java.util.ArrayList;
import java.util.List;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.CustomerIdResponseBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCustomerIdsResponse extends CommonResponse {
	List<CustomerIdResponseBean> listOfLinkedCompanies = new ArrayList<>();
}
