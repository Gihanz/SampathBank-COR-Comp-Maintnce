package biz.nable.sb.cor.comp.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.CompanyDelete;

@Repository
public interface CompanyDeleteRepository extends CrudRepository<CompanyDelete, Long> {

}
