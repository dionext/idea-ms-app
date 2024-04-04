package com.dionext.ideaportal.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "proe")
@Getter
@Setter
public class Proe {
    @EmbeddedId
    public ProeId id;

    @ManyToOne()
    @JoinColumn(name = "pro_cite_id", insertable = false, updatable = false)
    public Cite proCite;

    @ManyToOne()
    @JoinColumn(name = "contra_cite_id", insertable = false, updatable = false)
    public Cite contraCite;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    public Topic topic;

}