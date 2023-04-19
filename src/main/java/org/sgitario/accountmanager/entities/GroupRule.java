package org.sgitario.accountmanager.entities;

import java.util.Date;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class GroupRule extends PanacheEntity {

    public String expression;

    @ManyToOne
    @JsonIgnore
    public Group group;

    @CreationTimestamp
    public Date created;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupRule)) {
            return false;
        }

        GroupRule other = (GroupRule) o;

        return Objects.equals(id, other.id)
                && Objects.equals(expression, other.expression)
                && Objects.equals(group, other.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expression, group);
    }
}
