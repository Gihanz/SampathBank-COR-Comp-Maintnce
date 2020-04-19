package biz.nable.sb.cor.comp.db.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.UserMst;

@Repository
public interface UserMstRepository extends CrudRepository<UserMst, Long> {

	Optional<UserMst> findByUserName(String userName);

	Optional<UserMst> findByUserId(long userId);

	Set<UserMst> findAll();

	Set<UserMst> findByUserIdIn(Set<String> userID);

//	Set<UserMst> findByCompanyId(String companyID);

	Set<UserMst> findByRecordStatus(String companyID);

	Set<UserMst> findByCompanyId(String referenceId);

	Optional<UserMst> findByUserIdAndCompanyId(String userID, String companyID);

	Optional<UserMst> findByApprovalId(long approvalId);
}
