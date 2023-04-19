package org.sgitario.accountmanager.management;

import org.sgitario.accountmanager.entities.Group;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;

public interface GroupResource extends PanacheEntityResource<Group, Long> {
}
