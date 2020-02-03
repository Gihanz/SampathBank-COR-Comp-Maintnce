package biz.nable.sb.cor.comp.service.impl;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.common.bean.ApprovalBean;
import biz.nable.sb.cor.common.bean.WebHookBean;
import biz.nable.sb.cor.common.service.impl.CommonConverter;
import biz.nable.sb.cor.common.template.CommonApprovalTemplate;
import biz.nable.sb.cor.comp.factory.ApprovalTypeFactory;

@Service
public class WebhookService {

	@Autowired
	ApprovalTypeFactory approvalTypeFactory;
	@Autowired
	CommonConverter commonConverter;

	public void doApprove(WebHookBean webHookBean) {
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) webHookBean.getData();
		ApprovalBean approvalBean = commonConverter.mapToPojo(map, ApprovalBean.class);
		CommonApprovalTemplate approval = approvalTypeFactory.getApproval(approvalBean.getType());
		approval.doApprove(approvalBean);
	}

}
