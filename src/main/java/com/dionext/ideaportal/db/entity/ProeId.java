package com.dionext.ideaportal.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;


@Embeddable
@Getter
@Setter
public class ProeId implements Serializable {
    private static final long serialVersionUID = -876068764395666653L;
    @Column(name = "pro_cite_id", nullable = false)
    private String proCiteId;
    @Column(name = "contra_cite_id", nullable = false)
    private String contraCiteId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProeId entity = (ProeId) o;
        return Objects.equals(this.proCiteId, entity.proCiteId) &&
                Objects.equals(this.contraCiteId, entity.contraCiteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proCiteId, contraCiteId);
    }
}