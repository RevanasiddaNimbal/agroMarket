package com.agri.market.user.repository;

import com.agri.market.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>,
        JpaSpecificationExecutor<User> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByPhoneNumberIgnoreCase(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);
}