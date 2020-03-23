package web.social.facebook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import web.social.facebook.entities.Role;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>, JpaSpecificationExecutor<Role> {
    @Query("SELECT r FROM User u INNER JOIN UserRole b ON u.id = b.userId INNER JOIN Role r ON b.roleId = r.id\n" +
            "WHERE u.email = ?1 AND r.status = 1")
    List<Role> findRoleByUserId(String userId);
}