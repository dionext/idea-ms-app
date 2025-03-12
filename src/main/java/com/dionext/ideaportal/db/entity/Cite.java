package com.dionext.ideaportal.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Entity
@Table(name = "cite")
@Getter
@Setter
public class Cite {
    public static final String CITE = "Cite";
    public static final String CITE_EXP = "cite-exp";
    public static final String IDEA = "idea";
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    public Integer id;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    public Author author;

    // @Column(name = "author_id", insertable=false, updatable=false)
    //public String authorId;


    @Column(columnDefinition="TEXT")
    public String text;

    @Column(columnDefinition="TEXT")
    public String info;

    @ManyToOne()
    @JoinColumn(name = "literature_id")
    public Bibliography bibliography;

    @Column(length = 10)
    public String page;

    @ManyToOne()
    @JoinColumn(name = "literature_alt_id")
    public Bibliography bibliographyAlt;

    @Column(name = "page_alt", length = 10)
    public String pageAlt;

    @Column(name = "is_inderect", nullable = false)
    public boolean isInderect;


    //@Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    @Convert(converter = CitePriorConverter.class)
    public CitePrior prior;

    @ManyToMany(fetch = FetchType.LAZY)//не извлекать связанные объекты из базы данных, пока вы их не используете
    @JoinTable(
            name = "theme_cite",
            joinColumns = @JoinColumn(name = "cite_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id"))
    public Set<Topic> topics;
}

