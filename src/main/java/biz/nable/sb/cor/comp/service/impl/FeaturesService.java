package biz.nable.sb.cor.comp.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import biz.nable.sb.cor.comp.utility.ErrorCode;
import biz.nable.sb.cor.comp.bean.FeatureBean;
import biz.nable.sb.cor.comp.db.entity.CompanyFeatures;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.entity.Features;
import biz.nable.sb.cor.comp.db.repository.CompanyFeaturesRepository;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;
import biz.nable.sb.cor.comp.db.repository.FeaturesRepository;
import biz.nable.sb.cor.comp.db.repository.UserFeaturesRepositoty;
import biz.nable.sb.cor.comp.response.FeatureResponse;

@Service
public class FeaturesService {

	@Autowired
	FeaturesRepository featuresRepository;

	@Autowired
	CompanyMstRepository companyMstRepository;

	@Autowired
	UserFeaturesRepositoty userFeaturesRepository;

	@Autowired
	CompanyFeaturesRepository companyFeaturesRepository;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MessageSource messageSource;

	public FeatureResponse getAllFeatures() {

		logger.info("================== Start Get All Features =================");
		FeatureResponse featureResponse = new FeatureResponse();
		List<Features> features = (List<Features>) featuresRepository.findAll();
		for (Features feature : features) {
			FeatureBean featureBean = new FeatureBean();
			featureBean.setDescription(feature.getDescription());
			featureBean.setId(feature.getId());
			featureResponse.getFeatureBeans().add(featureBean);
		}
		featureResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
		featureResponse.setReturnCode(HttpStatus.OK.value());
		featureResponse.setReturnMessage(
				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));

		logger.info("================== End Get All features =================");
		return featureResponse;

	}
//
//	public FeatureResponse getUserFeatures(String corpUserId) {
//
//		logger.info("================== Start Get user Features =================");
//		FeatureResponse featureResponse = new FeatureResponse();
//		List<UserFeatures> userFeatures = userFeaturesRepository.findByCorpUserId(corpUserId);
//		for (UserFeatures feature : userFeatures) {
//			FeatureBean featureBean = new FeatureBean();
//			featureBean.setFeature(feature.get);
//			featureBean.setId(feature.getId());
//			featureResponse.getFeatureBeans().add(featureBean);
//		}
//		featureResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
//		featureResponse.setReturnCode(HttpStatus.OK.value());
//		featureResponse.setReturnMessage(
//				messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
//		logger.info("================== End Get user Features =================");
//		return featureResponse;
//	}

	public FeatureResponse getCompanyFeatures(String companyId) {
		logger.info("================== Start Get Company Features =================");
		FeatureResponse featureResponse = new FeatureResponse();
		Optional<CompanyMst> optional = companyMstRepository.findByCompanyId(companyId);
		if (!optional.isPresent()) {
			logger.info(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));
			featureResponse.setErrorCode(ErrorCode.NO_COMPANY_RECORD_FOUND);
			featureResponse.setReturnCode(HttpStatus.NOT_FOUND.value());
			featureResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.NO_COMPANY_RECORD_FOUND, null, LocaleContextHolder.getLocale()));

		} else {
			for (CompanyFeatures companyFeature : optional.get().getCompanyFeatures()) {
				FeatureBean featureBean = new FeatureBean();
				featureBean.setDescription(companyFeature.getFeature().getDescription());
				featureBean.setId(companyFeature.getFeature().getId());
				featureResponse.getFeatureBeans().add(featureBean);
			}
			featureResponse.setErrorCode(ErrorCode.OPARATION_SUCCESS);
			featureResponse.setReturnCode(HttpStatus.OK.value());
			featureResponse.setReturnMessage(
					messageSource.getMessage(ErrorCode.OPARATION_SUCCESS, null, LocaleContextHolder.getLocale()));
		}
		logger.info("================== End Get Company Features =================");
		return featureResponse;
	}

}
