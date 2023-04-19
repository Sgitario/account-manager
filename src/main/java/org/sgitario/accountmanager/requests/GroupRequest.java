package org.sgitario.accountmanager.requests;

import java.util.List;

import org.jboss.resteasy.reactive.RestForm;

import jakarta.validation.constraints.NotBlank;

public class GroupRequest {
    @NotBlank(message = "Name must not be empty")
    @RestForm
    public String name;

    @RestForm
    public List<String> expressions;
}
