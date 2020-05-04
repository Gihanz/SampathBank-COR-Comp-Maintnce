package biz.nable.sb.cor.comp.db.repository;

import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.CompanyAccountMst;

import java.util.Optional;

@Repository
public interface AccountsRepository extends CrudRepository<CompanyAccountMst, Long> {

    Optional<CompanyAccountMst> findByCompanyId(CompanyMst companyMst);
}
