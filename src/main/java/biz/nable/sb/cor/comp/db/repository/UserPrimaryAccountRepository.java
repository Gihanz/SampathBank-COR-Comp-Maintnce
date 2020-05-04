package biz.nable.sb.cor.comp.db.repository;

import biz.nable.sb.cor.comp.db.entity.UserMst;
import biz.nable.sb.cor.comp.db.entity.UserPrimaryAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserPrimaryAccountRepository extends CrudRepository<UserPrimaryAccount, Long> {

    Optional<UserPrimaryAccount> findByUserMstAcc(UserMst userMst);
}
