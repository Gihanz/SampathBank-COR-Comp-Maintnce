package biz.nable.sb.cor.comp.db.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.UserFeatures;

@Repository
public interface UserFeaturesRepositoty extends CrudRepository<UserFeatures, Long> {

	List<UserFeatures> findByCompanyUser(String companyUser);

}
