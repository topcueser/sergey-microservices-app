package com.topcueser.photoapp.api.users.repositories;

import com.topcueser.photoapp.api.users.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByName(String name);
}
