package biz.nable.sb.cor.comp.db.criteria;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import biz.nable.sb.cor.common.exception.SystemException;
import biz.nable.sb.cor.common.utility.ErrorCode;
import biz.nable.sb.cor.comp.bean.FindCompanyBean;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.repository.CompanyMstRepository;

@Component
public class CompanyCustomRepository {

	@Autowired
	CompanyMstRepository companyMstRepository;

	@PersistenceContext
	private EntityManager entityManager;
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${system.find.init.date}")
	private String initFromDate;

	@Autowired
	MessageSource messageSource;

	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

	public List<CompanyMst> findCompanyList(FindCompanyBean findCompanyBean) {
		logger.info("Start create findCompanyList criteria");
		return companyMstRepository.findAll(new Specification<CompanyMst>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<CompanyMst> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {

				List<Predicate> predicates = new ArrayList<>();
				createdByCriteriaBuilder(predicates, criteriaBuilder, root, findCompanyBean);
				createdDateCriteriaBuilder(predicates, criteriaBuilder, root, findCompanyBean);
				lastUpdatedCriteriaBuilder(predicates, criteriaBuilder, root, findCompanyBean);
				companyNameCriteriaBuilder(predicates, criteriaBuilder, root, findCompanyBean);
				lastUpdatedByCriteriaBuilder(predicates, criteriaBuilder, root, findCompanyBean);
				createdUserGroupCriteriaBuilder(predicates, criteriaBuilder, root, findCompanyBean);
				lastUpdatedUserGroupCriteriaBuilder(predicates, criteriaBuilder, root, findCompanyBean);
				statusCriteriaBuilder(predicates, criteriaBuilder, root, findCompanyBean);
				companyIdCriteriaBuilder(predicates, criteriaBuilder, root, findCompanyBean);

				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}

		});
	}

	private void companyNameCriteriaBuilder(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
			Root<CompanyMst> root, FindCompanyBean findCompanyBean) {
		if (null != findCompanyBean.getCompanyName() && !findCompanyBean.getCompanyName().isEmpty()) {
			predicates.add(criteriaBuilder
					.and(criteriaBuilder.equal(root.get("companyName"), findCompanyBean.getCompanyName())));
		}
	}

	private void createdByCriteriaBuilder(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
			Root<CompanyMst> root, FindCompanyBean findCompanyBean) {
		if (null != findCompanyBean.getCreatedBy() && !findCompanyBean.getCreatedBy().isEmpty()) {
			predicates.add(
					criteriaBuilder.and(criteriaBuilder.equal(root.get("createdBy"), findCompanyBean.getCreatedBy())));
		}
	}

	private void createdDateCriteriaBuilder(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
			Root<CompanyMst> root, FindCompanyBean findCompanyBean) {
		if ((null != findCompanyBean.getCreatedFromDate() && !findCompanyBean.getCreatedFromDate().isEmpty())
				|| (null != findCompanyBean.getCreatedToDate() && !findCompanyBean.getCreatedToDate().isEmpty())) {
			try {
				Date fromDate = formatter.parse(
						null != findCompanyBean.getCreatedFromDate() && !findCompanyBean.getCreatedFromDate().isEmpty()
								? findCompanyBean.getCreatedFromDate()
								: initFromDate);

				Date toDate = (null != findCompanyBean.getCreatedToDate()
						&& !findCompanyBean.getCreatedToDate().isEmpty())
								? (formatter.parse(findCompanyBean.getCreatedToDate() + " 23:59:59"))
								: new Date();
				predicates.add(criteriaBuilder.and(criteriaBuilder.between(root.get("createdDate"), fromDate, toDate)));
			} catch (ParseException e) {
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATE_FORMATING_ERROR, null, LocaleContextHolder.getLocale()),
						ErrorCode.DATE_FORMATING_ERROR);
			}

		}
	}

	private void lastUpdatedCriteriaBuilder(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
			Root<CompanyMst> root, FindCompanyBean findCompanyBean) {
		if ((null != findCompanyBean.getLastUpdatedFromDate() && !findCompanyBean.getLastUpdatedFromDate().isEmpty())
				|| (null != findCompanyBean.getLastUpdatedToDate()
						&& !findCompanyBean.getLastUpdatedToDate().isEmpty())) {
			try {
				Date fromDate = formatter.parse(null != findCompanyBean.getLastUpdatedFromDate()
						&& !findCompanyBean.getLastUpdatedFromDate().isEmpty()
								? findCompanyBean.getLastUpdatedFromDate()
								: initFromDate);

				Date toDate = (null != findCompanyBean.getLastUpdatedToDate()
						&& !findCompanyBean.getLastUpdatedToDate().isEmpty())
								? (formatter.parse(findCompanyBean.getLastUpdatedToDate() + " 23:59:59"))
								: new Date();
				predicates.add(
						criteriaBuilder.and(criteriaBuilder.between(root.get("lastUpdatedDate"), fromDate, toDate)));
			} catch (ParseException e) {
				throw new SystemException(
						messageSource.getMessage(ErrorCode.DATE_FORMATING_ERROR, null, LocaleContextHolder.getLocale()),
						ErrorCode.DATE_FORMATING_ERROR);
			}

		}
	}

	private void lastUpdatedByCriteriaBuilder(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
			Root<CompanyMst> root, FindCompanyBean findCompanyBean) {
		if (null != findCompanyBean.getLastUpdatedBy() && !findCompanyBean.getLastUpdatedBy().isEmpty()) {
			predicates.add(criteriaBuilder
					.and(criteriaBuilder.equal(root.get("lastUpdatedBy"), findCompanyBean.getLastUpdatedBy())));
		}
	}

	private void createdUserGroupCriteriaBuilder(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
			Root<CompanyMst> root, FindCompanyBean findCompanyBean) {
		if (null != findCompanyBean.getCreatedUserGroup() && !findCompanyBean.getCreatedUserGroup().isEmpty()) {
			predicates.add(criteriaBuilder
					.and(criteriaBuilder.equal(root.get("userGroup"), findCompanyBean.getCreatedUserGroup())));
		}
	}

	private void lastUpdatedUserGroupCriteriaBuilder(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
			Root<CompanyMst> root, FindCompanyBean findCompanyBean) {
		if (null != findCompanyBean.getLastUpdatedUserGroup() && !findCompanyBean.getLastUpdatedUserGroup().isEmpty()) {
			predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("lastUpdatedUserGroup"),
					findCompanyBean.getLastUpdatedUserGroup())));
		}
	}

	private void statusCriteriaBuilder(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
			Root<CompanyMst> root, FindCompanyBean findCompanyBean) {
		if (null != findCompanyBean.getStatus()) {
			predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("status"), findCompanyBean.getStatus())));
		}
	}

	private void companyIdCriteriaBuilder(List<Predicate> predicates, CriteriaBuilder criteriaBuilder,
			Root<CompanyMst> root, FindCompanyBean findCompanyBean) {
		if (null != findCompanyBean.getCompanyId()) {
			predicates.add(
					criteriaBuilder.and(criteriaBuilder.equal(root.get("companyId"), findCompanyBean.getCompanyId())));
		}
	}
}