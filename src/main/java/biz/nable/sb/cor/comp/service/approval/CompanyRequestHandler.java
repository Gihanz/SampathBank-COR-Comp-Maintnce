package biz.nable.sb.cor.comp.service.approval;

import org.springframework.stereotype.Component;

import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.request.CreateCompanyRequest;

@Component
public interface CompanyRequestHandler {

	CommonResponse addTemp(CreateCompanyRequest createCompanyRequest, String userId, String userGroup,
			String requestType, String referenceNo);

}
