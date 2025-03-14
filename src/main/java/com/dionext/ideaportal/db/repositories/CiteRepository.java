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

    //SELECT DISTINCT(cite.id), cite.text, topic.name FROM cite, topic, topic_cite WHERE cite.id = topic_cite.cite_id AND topic_cite.topic_id = topic.id
    //AND LEFT(topic.hcode, 1) = 'E'
    @Query(value = """
    SELECT ci.* FROM cite ci, topic, topic_cite WHERE ci.id = topic_cite.cite_id AND topic_cite.topic_id = topic.id
    AND LEFT(topic.hcode, ?1) = ?2
        """, nativeQuery = true)
    Collection<Cite> findByTopicHcodeNative(int hCodeLen, String topicHCode);
    @Query(value = """
        SELECT DISTINCT ci.* FROM cite ci, topic, topic_cite WHERE ci.id = topic_cite.cite_id AND topic_cite.topic_id = topic.id
        AND LEFT(topic.hcode, ?1) = ?2
        """,
        countQuery =  """
        SELECT DISTINCT ci.* FROM cite ci, topic, topic_cite WHERE ci.id = topic_cite.cite_id AND topic_cite.topic_id = topic.id
        AND LEFT(topic.hcode, ?1) = ?2
        """,
        nativeQuery = true)
    Page<Cite> findByTopicHcodeNative(int hCodeLen, String topicHCode, Pageable pageable);
    Collection<Cite> findByAuthorId(Integer authorId);
    Page<Cite> findByAuthorId(Integer authorId, Pageable pageable);
    Collection<Cite> findByTopics_Id(Integer topicId);

}