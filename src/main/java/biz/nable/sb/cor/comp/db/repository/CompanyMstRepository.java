package biz.nable.sb.cor.comp.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.common.utility.StatusEnum;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.utility.RecordStatusEnum;

@Repository
public interface CompanyMstRepository extends CrudRepository<CompanyMst, Long>, JpaSpecificationExecutor<CompanyMst> {

	Optional<CompanyMst> findByCompanyId(String referenceId);

	List<CompanyMst> findAllByStatus(StatusEnum status);

	Optional<CompanyMst> findByCompanyIdAndRecordStatus(String companyId, RecordStatusEnum active);

	List<CompanyMst> findByCompanyIdIn(List<String> companyIdList);

}
