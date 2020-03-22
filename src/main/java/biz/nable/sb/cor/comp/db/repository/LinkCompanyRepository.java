package biz.nable.sb.cor.comp.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.CompanyCummData;

@Repository
public interface LinkCompanyRepository extends CrudRepository<CompanyCummData, Long> {

	List<CompanyCummData> findByParentCompanyId(String companyId);

	Optional<CompanyCummData> findBycustomerId(String customerId);

	Optional<CompanyCummData> findByParentCompanyIdAndCustomerId(String parentCompanyId, String customerId);

}
