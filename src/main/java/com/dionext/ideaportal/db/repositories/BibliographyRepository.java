package com.dionext.ideaportal.db.repositories;

import com.dionext.ideaportal.db.entity.Bibliography;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BibliographyRepository extends JpaRepository<Bibliography, Integer> {
}