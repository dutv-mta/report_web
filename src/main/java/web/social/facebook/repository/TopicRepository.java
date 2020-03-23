package web.social.facebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.social.facebook.entities.Topic;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer>, JpaSpecificationExecutor<Topic> {

    @Query("SELECT t.mustWord FROM Topic t where t.id = ?1 and t.flag =1")
    String findMustWord(int topicId);

    @Query("SELECT t.stopWord FROM Topic t where t.id = ?1 and t.flag =1")
    String findStopWord(int topicId);

    @Query("SELECT t FROM Topic t INNER JOIN UserTopic u ON t.id = u.topicId WHERE u.userId = ?1 And t.flag=1 ")
    List<Topic> getListTopicByUser(int userId) throws SQLException;

    Topic findTopicByName(String name) throws SQLException;

    @Modifying
    @Transactional
    @Query("UPDATE Topic t SET t.flag = 0 WHERE t.id = ?1")
    void deleteTopic(int id);
}
