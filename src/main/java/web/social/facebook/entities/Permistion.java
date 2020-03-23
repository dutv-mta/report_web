package web.social.facebook.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "permistion")
public class Permistion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "role_Id")
    private Integer roleId;

    @Column(name = "permitted_path")
    private String permittedPath;

    @Column(name = "display_discription")
    private String displayDiscription;

    @Column(name = "create_on")
    private Date createOn;

    @Column(name = "create_by")
    private Integer createBy;

    @Column(name = "update_on")
    private Date updateOn;

    @Column(name = "update_by")
    private Integer updateBy;

    @Column(name = "delete_on")
    private Date deleteOn;

    @Column(name = "delete_by")
    private Integer deleteBy;

    @Column(name = "status")
    private Integer status;

    public Permistion() {
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getPermittedPath() {
        return permittedPath;
    }

    public void setPermittedPath(String permittedPath) {
        this.permittedPath = permittedPath;
    }

    public String getDisplayDiscription() {
        return displayDiscription;
    }

    public void setDisplayDiscription(String displayDiscription) {
        this.displayDiscription = displayDiscription;
    }

    public Date getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateOn() {
        return updateOn;
    }

    public void setUpdateOn(Date updateOn) {
        this.updateOn = updateOn;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }

    public Date getDeleteOn() {
        return deleteOn;
    }

    public void setDeleteOn(Date deleteOn) {
        this.deleteOn = deleteOn;
    }

    public Integer getDeleteBy() {
        return deleteBy;
    }

    public void setDeleteBy(Integer deleteBy) {
        this.deleteBy = deleteBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Permistion{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", permittedPath='" + permittedPath + '\'' +
                ", displayDiscription='" + displayDiscription + '\'' +
                ", createOn=" + createOn +
                ", createBy=" + createBy +
                ", updateOn=" + updateOn +
                ", updateBy=" + updateBy +
                ", deleteOn=" + deleteOn +
                ", deleteBy=" + deleteBy +
                ", status=" + status +
                '}';
    }
}