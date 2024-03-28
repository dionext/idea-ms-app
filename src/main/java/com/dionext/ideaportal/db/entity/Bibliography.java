package com.dionext.ideaportal.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "literature")
@Getter
@Setter
public class Bibliography {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

}