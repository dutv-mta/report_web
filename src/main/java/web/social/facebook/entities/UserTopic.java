package web.social.facebook.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_topic")
public class UserTopic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "topic_id", nullable = false)
    private Integer topicId;

    public UserTopic() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return "UserTopic{" +
                "id=" + id +
                ", userId=" + userId +
                ", topicId=" + topicId +
                '}';
    }
}