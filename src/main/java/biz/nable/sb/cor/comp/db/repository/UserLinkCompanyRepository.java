package biz.nable.sb.cor.comp.db.repository;

import biz.nable.sb.cor.comp.db.entity.CompanyMst;
import biz.nable.sb.cor.comp.db.entity.UserLinkedCompany;
import biz.nable.sb.cor.comp.db.entity.UserMst;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLinkCompanyRepository extends CrudRepository<UserLinkedCompany, Long> {

    Optional<UserLinkedCompany> findByCompanyMstAndUserMst(CompanyMst companyMst, UserMst userMst);

}
