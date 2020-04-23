package biz.nable.sb.cor.comp.db.repository;

import biz.nable.sb.cor.comp.db.entity.UserMstHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMstHistoryRepository extends CrudRepository<UserMstHistory, Long> {

}
