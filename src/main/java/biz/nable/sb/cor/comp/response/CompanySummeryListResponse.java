package biz.nable.sb.cor.comp.response;

import java.util.ArrayList;
import java.util.List;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.CompanySummeryBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanySummeryListResponse extends CommonResponse {
	private List<CompanySummeryBean> companySummeryBeans = new ArrayList<>();

	public CompanySummeryListResponse(int returnCode, String returnMessage, String errorCode) {
		super(returnCode, returnMessage, errorCode);
	}
}
