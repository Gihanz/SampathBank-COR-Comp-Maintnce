package biz.nable.sb.cor.comp.response;

import java.util.ArrayList;
import java.util.List;

import biz.nable.sb.cor.common.bean.TempDto;
import biz.nable.sb.cor.common.response.CommonResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalPendingResponse extends CommonResponse {
	List<TempDto> tempDtos = new ArrayList<>();
}
