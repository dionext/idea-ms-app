package com.dionext.ideaportal.db.repositories;

import com.dionext.ideaportal.db.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, String> {
}