package com.dionext.ideaportal.db.entity;

import jakarta.persistence.*;


@Entity
@Table()
public class Proe {
    @EmbeddedId
    public ProeId id;

    @ManyToOne()
    @JoinColumn(name = "proCiteId", insertable = false, updatable = false)
    public Cite proCite;

    @ManyToOne()
    @JoinColumn(name = "contraCiteId", insertable = false, updatable = false)
    public Cite contraCite;

    @ManyToOne
    @JoinColumn(name = "themeId")
    public Topic topic;

}