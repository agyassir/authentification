package com.example.auth_spring_security.Controller.vm.User;

import com.example.auth_spring_security.Entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
private long id;
    @NotNull(message = "the location can't be null")
    @NotBlank(message = "the location can't be blank")
    private String name;
    @NotNull(message = "the location can't be null")
    @NotBlank(message = "the location can't be blank")
    private String email;

    private String password;

    private int age;

    private Role role;

}
