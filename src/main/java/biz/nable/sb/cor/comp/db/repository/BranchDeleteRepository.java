package biz.nable.sb.cor.comp.db.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.BranchDelete;

@Repository
public interface BranchDeleteRepository extends CrudRepository<BranchDelete, Long> {

	List<BranchDelete> findByCompanyId(Long id);

}
