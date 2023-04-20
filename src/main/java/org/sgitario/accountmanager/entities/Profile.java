package org.sgitario.accountmanager.entities;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity(name = "t_profile")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Profile extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @NotNull
    public String name;
    @NotNull
    public int columnSubject;
    @NotNull
    public int columnQuantity;
    @NotNull
    public int columnAccountingDate;
    @NotNull
    public int columnValueDate;
    @CreationTimestamp
    public Date created;
    @UpdateTimestamp
    public Date updated;
}
