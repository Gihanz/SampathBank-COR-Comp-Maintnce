/**
 * 
 */
package biz.nable.sb.cor.comp.utility;

import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import biz.nable.sb.cor.comp.soap.schemas.iib.GetCustomerInfoRequestType;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetCustomerInfoResponseType;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetGenInqRequestType;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetGenInqResponseType;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetOperativeAccountDataRequestType;
import biz.nable.sb.cor.comp.soap.schemas.iib.GetOperativeAccountDataResponseType;

/**
 * @author asel.meemeduma
 *
 */

@Component
public class BlzServiceAdapter extends WebServiceGatewaySupport {

	@SuppressWarnings("unchecked")
	public GetGenInqResponseType getGenInq(String url, JAXBElement<GetGenInqRequestType> jaxbElement) {
		JAXBElement<GetGenInqResponseType> res = (JAXBElement<GetGenInqResponseType>) getWebServiceTemplate()
				.marshalSendAndReceive(url, jaxbElement);
		return res.getValue();
	}

	@SuppressWarnings("unchecked")
	public GetCustomerInfoResponseType getCustomerInfo(String url,
			JAXBElement<GetCustomerInfoRequestType> jaxbElement) {
		JAXBElement<GetCustomerInfoResponseType> response = (JAXBElement<GetCustomerInfoResponseType>) getWebServiceTemplate()
				.marshalSendAndReceive(url, jaxbElement);
		return response.getValue();
	}

	@SuppressWarnings("unchecked")
	public GetOperativeAccountDataResponseType getOperativeAccountData(String url,
			JAXBElement<GetOperativeAccountDataRequestType> jaxbElement) {
		JAXBElement<?> marshalSendAndReceive = (JAXBElement<?>) getWebServiceTemplate().marshalSendAndReceive(url,
				jaxbElement);
		if (marshalSendAndReceive.getValue() instanceof GetOperativeAccountDataResponseType) {
			JAXBElement<GetOperativeAccountDataResponseType> response = (JAXBElement<GetOperativeAccountDataResponseType>) marshalSendAndReceive;
			return response.getValue();

		} else {
			return null;
		}
	}

}
