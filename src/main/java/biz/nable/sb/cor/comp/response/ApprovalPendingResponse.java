package biz.nable.sb.cor.comp.response;

import java.util.ArrayList;
import java.util.List;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.CompanyListResponseBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalPendingResponse extends CommonResponse {
	List<CompanyListResponseBean> tempDtos = new ArrayList<>();
}
