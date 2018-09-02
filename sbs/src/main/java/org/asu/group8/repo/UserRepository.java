package org.asu.group8.repo;

import org.asu.group8.entity.SbsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<SbsUser, Long> {

    @Query("SELECT u FROM SbsUser u WHERE u.username = :username AND u.modifyRequest = false")
    SbsUser findByUsername(@Param("username") String username);

    @Query("SELECT u FROM SbsUser u WHERE u.modifyRequest = true")
    List<SbsUser> findModifyRequests();

    @Query("SELECT u FROM SbsUser u WHERE u.id = :id AND u.modifyRequest = true")
    SbsUser findModifyRequest(@Param("id") Long id);

    @Query("SELECT u FROM SbsUser u WHERE u.email = :email AND u.modifyRequest = false")
    List<SbsUser> getUsersByEmail(@Param("email") String email);

}
