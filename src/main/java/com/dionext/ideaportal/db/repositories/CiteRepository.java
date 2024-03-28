package com.dionext.ideaportal.db.repositories;

import com.dionext.ideaportal.db.entity.Cite;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface CiteRepository extends JpaRepository<Cite, String> {

    //@Query("FROM Author WHERE firstName = ?1")
    @Query("SELECT u FROM Cite u WHERE u.prior = '0'")
    Collection<Cite> findAllFavorite(Sort sort);

    @Query(value = "SELECT u FROM Cite u WHERE u.prior = 0", nativeQuery = true)
    Collection<Cite> findAllFavoriteNative();
}