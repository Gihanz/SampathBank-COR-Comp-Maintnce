package biz.nable.sb.cor.comp.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import biz.nable.sb.cor.comp.db.entity.Features;

import java.util.Optional;

@Repository
public interface FeaturesRepository extends CrudRepository<Features, Long> {

    Optional<Features> findById(String featureID);
}
