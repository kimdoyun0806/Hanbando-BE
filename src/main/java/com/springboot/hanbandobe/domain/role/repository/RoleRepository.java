package com.springboot.hanbandobe.domain.role.repository;

import com.springboot.hanbandobe.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);
}