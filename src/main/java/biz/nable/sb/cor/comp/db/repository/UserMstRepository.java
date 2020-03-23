package biz.nable.sb.cor.comp.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.UserMst;

@Repository
public interface UserMstRepository extends CrudRepository<UserMst, Long> {

	Optional<UserMst> findByUserName(String userName);

	List<UserMst> findAll();

}
