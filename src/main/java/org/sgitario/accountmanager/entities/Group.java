package org.sgitario.accountmanager.entities;

import static jakarta.persistence.CascadeType.ALL;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity(name = "t_group")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Group extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    public String name;

    @OneToMany(mappedBy = "group", cascade = ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    public Set<GroupRule> rules = new HashSet<>();

    @CreationTimestamp
    public Date created;
    @UpdateTimestamp
    public Date updated;
}
