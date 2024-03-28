package com.dionext.ideaportal.db.repositories;

import com.dionext.ideaportal.db.entity.Proe;
import com.dionext.ideaportal.db.entity.ProeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProeRepository extends JpaRepository<Proe, ProeId> {
}