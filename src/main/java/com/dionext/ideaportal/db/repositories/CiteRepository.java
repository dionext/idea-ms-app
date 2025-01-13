package com.dionext.ideaportal.db.repositories;

import com.dionext.ideaportal.db.entity.Cite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface CiteRepository extends JpaRepository<Cite, Integer> {

    @Query("SELECT u FROM Cite u WHERE u.prior = '0'")
    Page<Cite> findAllFavorite(Pageable pageable);
    @Query("SELECT u FROM Cite u WHERE u.prior = '0'")
    Collection<Cite> findAllFavorite();

    //SELECT DISTINCT(cite.id), cite.text, theme.name FROM cite, theme, theme_cite WHERE cite.id = theme_cite.cite_id AND theme_cite.theme_id = theme.id
    //AND LEFT(theme.hcode, 1) = 'E'
    @Query(value = """
    SELECT ci.* FROM cite ci, theme, theme_cite WHERE ci.id = theme_cite.cite_id AND theme_cite.theme_id = theme.id
    AND LEFT(theme.hcode, ?1) = ?2
        """, nativeQuery = true)
    Collection<Cite> findByTopicHcodeNative(int hCodeLen, String themeHCode);
    @Query(value = """
        SELECT DISTINCT ci.* FROM cite ci, theme, theme_cite WHERE ci.id = theme_cite.cite_id AND theme_cite.theme_id = theme.id
        AND LEFT(theme.hcode, ?1) = ?2
        """,
        countQuery =  """
        SELECT DISTINCT ci.* FROM cite ci, theme, theme_cite WHERE ci.id = theme_cite.cite_id AND theme_cite.theme_id = theme.id
        AND LEFT(theme.hcode, ?1) = ?2
        """,
        nativeQuery = true)
    Page<Cite> findByTopicHcodeNative(int hCodeLen, String themeHCode, Pageable pageable);
    Collection<Cite> findByAuthorId(Integer authorId);
    Page<Cite> findByAuthorId(Integer authorId, Pageable pageable);
    Collection<Cite> findByTopics_Id(Integer topicId);

}