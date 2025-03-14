package com.dionext.ideaportal.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "topic")
@Getter
@Setter
public class Topic {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    public Integer id;

    @Column(nullable = false, length = 100)
    public String name;

    @Column(name = "parent_id")
    public Integer parentId;

    @Column(length = 20)
    public String hcode;

}