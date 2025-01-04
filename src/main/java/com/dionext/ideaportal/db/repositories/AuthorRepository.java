package com.dionext.ideaportal.db.repositories;

import com.dionext.ideaportal.db.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface AuthorRepository
        extends JpaRepository<Author, Integer> {

    @Query("SELECT u FROM Author u WHERE u.names like CONCAT('%',?1,'%')")
    Collection<Author> findAllByTerm(String name);


}
