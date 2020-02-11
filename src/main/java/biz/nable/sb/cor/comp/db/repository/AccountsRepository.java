package biz.nable.sb.cor.comp.db.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.CompanyAccountMst;

@Repository
public interface AccountsRepository extends CrudRepository<CompanyAccountMst, Long> {

	List<CompanyAccountMst> findByCompanyId(String companyId);

}
