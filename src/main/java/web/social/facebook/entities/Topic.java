package web.social.facebook.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Table(name = "topic")
@Entity
public class Topic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Must word is mandatory")
    @Column(name = "must_word")
    private String mustWord;

    @NotBlank(message = "Stop word is mandatory")
    @Column(name = "stop_word")
    private String stopWord;

    @Column(name = "create_on")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private Date createOn;

    @Column(name = "flag")
    private Integer flag;

    public Topic() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMustWord() {
        return mustWord;
    }

    public void setMustWord(String mustWord) {
        this.mustWord = mustWord;
    }

    public String getStopWord() {
        return stopWord;
    }

    public void setStopWord(String stopWord) {
        this.stopWord = stopWord;
    }

    public Date getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mustWord='" + mustWord + '\'' +
                ", stopWord='" + stopWord + '\'' +
                ", createOn=" + createOn +
                ", flag=" + flag +
                '}';
    }
}