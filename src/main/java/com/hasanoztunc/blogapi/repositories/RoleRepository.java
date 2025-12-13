package com.hasanoztunc.blogapi.repositories;

import com.hasanoztunc.blogapi.models.Role;
import com.hasanoztunc.blogapi.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(UserRole userRole);
}