package web.social.facebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import web.social.facebook.entities.Permistion;

@Repository
public interface PermistionRepository extends JpaRepository<Permistion, Integer>, JpaSpecificationExecutor<Permistion> {

}