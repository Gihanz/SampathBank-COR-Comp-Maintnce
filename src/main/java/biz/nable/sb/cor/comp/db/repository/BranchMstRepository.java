package biz.nable.sb.cor.comp.db.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.BranchMst;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;

@Repository
public interface BranchMstRepository extends CrudRepository<BranchMst, Long> {

	Optional<BranchMst> findByBranchId(String branchId);

	Optional<BranchMst> findByBranchIdAndCompany(String branchId, CompanyMst company);

}
