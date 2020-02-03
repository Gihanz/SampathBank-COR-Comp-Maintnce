package biz.nable.sb.cor.comp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import biz.nable.sb.cor.common.bean.WebHookBean;
import biz.nable.sb.cor.common.utility.WebHookTypeEnum;
import biz.nable.sb.cor.comp.service.impl.WebhookService;

@RestController
public class WebHookController {
	@Autowired
	WebhookService webhookService;

	@PostMapping("/v1/webhook")
	public ResponseEntity<?> webhook(@RequestBody WebHookBean webHookBean) {
		if (WebHookTypeEnum.APPROVAL.name().equalsIgnoreCase(webHookBean.getWebHookType())) {
			webhookService.doApprove(webHookBean);
		}
		return ResponseEntity.accepted().body("Success");
	}

}
