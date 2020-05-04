package biz.nable.sb.cor.comp.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.common.bean.CommonSearchBean;
import biz.nable.sb.cor.common.response.CommonResponse;
import biz.nable.sb.cor.comp.bean.CompanyListResponseBean;
import biz.nable.sb.cor.comp.component.CompanyTempComponent;
import biz.nable.sb.cor.comp.response.ApprovalPendingResponse;
import biz.nable.sb.cor.comp.utility.ErrorCode;

@Service
public class ApprovalService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	CompanyTempComponent companyTempComponent;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommonResponse getApprovalPendingRecord(String userId, String userGroup) {
		ApprovalPendingResponse commonResponse = new ApprovalPendingResponse();
		logger.info("================== Start Get approval pending data =================");

		CommonSearchBean bean = new CommonSearchBean();
		bean.setUserGroup(userGroup);
//		List<TempDto> tempList = companyTempComponent.getTempRecord(bean).getTempList();
		List<CompanyListResponseBean> responseBeans = new ArrayList<>();
		commonResponse.setTempDtos(responseBeans);
		commonResponse.setReturnCode(HttpStatus.OK.value());
		commonResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		commonResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		logger.info("================== End get approval pending data =================");
		return commonResponse;
	}

}
