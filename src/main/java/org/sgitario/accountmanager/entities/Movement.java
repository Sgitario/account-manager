package org.sgitario.accountmanager.entities;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity(name = "t_movement")
public class Movement extends PanacheEntityBase {
    @Id
    public int id;
    @NotNull
    public String subject;
    @NotNull
    @Column(columnDefinition = "NUMERIC(20, 2)")
    public double quantity;
    @OneToOne
    @JoinColumn(name = "group_id")
    public Group group;
    @OneToOne
    @JoinColumn(name = "profile_id")
    public Profile profile;
    @CreationTimestamp
    public Date created;
    public Date valueDate;
    public Date accountingDate;
}
