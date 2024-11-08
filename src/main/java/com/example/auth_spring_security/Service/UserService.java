package com.example.auth_spring_security.Service;

import com.example.auth_spring_security.Controller.vm.AuthenticatioResponce;
import com.example.auth_spring_security.Controller.vm.AuthenticationRequest;
import com.example.auth_spring_security.Entity.User;

public interface UserService {
    AuthenticatioResponce create(User user);
    AuthenticatioResponce authenticate(AuthenticationRequest request);
}
