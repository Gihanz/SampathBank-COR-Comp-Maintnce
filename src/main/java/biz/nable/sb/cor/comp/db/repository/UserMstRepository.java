package biz.nable.sb.cor.comp.db.repository;

import java.util.Optional;
import java.util.Set;

import biz.nable.sb.cor.comp.utility.RecordStatuUsersEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.UserMst;

@Repository
public interface UserMstRepository extends CrudRepository<UserMst, Long> {

	Optional<UserMst> findByUserName(String userName);

	Optional<UserMst> findByUserId(long userId);

	Set<UserMst> findAll();

	Set<UserMst> findByUserIdIn(Set<Long> userID);

//	Set<UserMst> findByCompanyId(String companyID);

	Set<UserMst> findByRecordStatus(RecordStatuUsersEnum recordStatus);

	Set<UserMst> findByCompanyId(String referenceId);

	Optional<UserMst> findByUserIdAndCompanyId(long userID, String companyID);

	Optional<UserMst> findByApprovalId(long approvalId);

}
