package com.outing.auth.repository;

import com.outing.auth.model.AuthUserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<AuthUserModel,String> {
    AuthUserModel findByUsername(String username);


    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<AuthUserModel> findAll();

    List<AuthUserModel> findByIdIn(List<String> id);


    @Query("SELECT u from AuthUserModel u WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail)")
    AuthUserModel findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

//    @Query("SELECT u from AuthUser u WHERE (u.email = :Email)")
    AuthUserModel findByEmail(String Email);

//    @Query("SELECT u from AuthUser u WHERE (u.name = :Nmae)")
    AuthUserModel findByName(String Name);

    @Modifying
    @Query("DELETE from AuthUserModel u WHERE (u.activationId IS NOT NULL OR u.activationId != '') AND u.registerTime < :currentTimeFifteenMinAgo")
    void deleteByRegisterTimeBefore(@Param("currentTimeFifteenMinAgo") LocalDateTime currentTimeFifteenMinAgo);


}
