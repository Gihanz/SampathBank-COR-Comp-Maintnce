package biz.nable.sb.cor.comp.response;

import java.util.ArrayList;
import java.util.List;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.CompanySummeryBean;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanySummeryListResponse extends CommonResponse {
	private List<CompanySummeryBean> companySummeryBeans = new ArrayList<>();
}
