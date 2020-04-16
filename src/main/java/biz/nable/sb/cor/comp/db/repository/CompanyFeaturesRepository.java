package biz.nable.sb.cor.comp.db.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.CompanyFeatures;
import biz.nable.sb.cor.comp.db.entity.CompanyMst;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CompanyFeaturesRepository extends CrudRepository<CompanyFeatures, Long> {

	@Modifying
	@Query("delete from CompanyFeatures u where u.company = ?1")
	void deleteFeaturesByCompany(CompanyMst companyMst);

	Optional<CompanyFeatures> findByCompany(Long aLong);

	Set<CompanyFeatures> findByFeature(long feature);
}
