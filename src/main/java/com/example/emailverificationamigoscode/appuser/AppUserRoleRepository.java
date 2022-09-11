package com.example.emailverificationamigoscode.appuser;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRoleRepository extends CrudRepository<AppUserRole,Long> {
    Optional<AppUserRole> findByName(String name);
}
