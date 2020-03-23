package web.social.facebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import web.social.facebook.entities.UserTopic;

@Repository
public interface UserTopicRepository extends JpaRepository<UserTopic, Integer>, JpaSpecificationExecutor<UserTopic> {

}