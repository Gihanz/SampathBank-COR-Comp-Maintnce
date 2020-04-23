package biz.nable.sb.cor.comp.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import biz.nable.sb.cor.comp.bean.RetrieveFinacleBean;
import biz.nable.sb.cor.comp.exception.FinacleCallException;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetCustomerInfoRequestType;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetCustomerInfoResponseType;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetGenInqRequestType;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetGenInqResponseType;
import biz.nable.sb.cor.comp.soap.schemas.iib.ObjectFactory;
import biz.nable.sb.cor.comp.utility.BlzServiceAdapter;

@Component
public class FinacleConnector {
	@Autowired
	private BlzServiceAdapter blzAdapter;

	@Autowired
	private RetrieveFinacleBean retrieveFinacleBean;

	@Value("${custom.iib.finacle.integration.url}")
	private String iibFinacleIntegrationUrl;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public GetCustomerInfoResponseType getCustomerInfo(String custId) {
		logger.info("Start getCustomerInfo finacle call - custId: {}", custId);
		ObjectFactory objectFactory = new ObjectFactory();
		GetCustomerInfoRequestType requestType = new GetCustomerInfoRequestType();
		requestType.setAPPCode(retrieveFinacleBean.getAPPCode());
		requestType.setCDCICode(retrieveFinacleBean.getCDCICode());
		requestType.setController(retrieveFinacleBean.getController());
		requestType.setCustId(custId);
		try {
			logger.info("Success Response: {} ", blzAdapter.getCustomerInfo(iibFinacleIntegrationUrl,
					objectFactory.createGetCustomerInfoRequest(requestType)));
			return blzAdapter.getCustomerInfo(iibFinacleIntegrationUrl,
					objectFactory.createGetCustomerInfoRequest(requestType));

		} catch (Exception e) {
			logger.error("Response Error:{} ClassName: {} MethodName: {}", e ,"FinacleConnector", "getCustomerInfo");
			throw new FinacleCallException("getCustomerInfo finacle call error", e);
		}

	}

	public GetGenInqResponseType getGenInq(String accountNo) {

		logger.info("Start getGenInq finacle call - accountNo: {}", accountNo);
		ObjectFactory objectFactory = new ObjectFactory();
		GetGenInqRequestType requestType = new GetGenInqRequestType();
		requestType.setAccountNo(accountNo);
		requestType.setAPPCode(retrieveFinacleBean.getAPPCode());
		requestType.setCDCICode(retrieveFinacleBean.getCDCICode());
		requestType.setController(retrieveFinacleBean.getController());
		requestType.setSolID(retrieveFinacleBean.getSolID());
		try {
			return blzAdapter.getGenInq(iibFinacleIntegrationUrl, objectFactory.createGetGenInqRequest(requestType));
		} catch (Exception e) {
			throw new FinacleCallException("getCustomerInfo finacle call error", e);
		}
	}

}
