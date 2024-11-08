package com.example.auth_spring_security.Controller;

import com.example.auth_spring_security.Controller.vm.AuthenticatioResponce;
import com.example.auth_spring_security.Controller.vm.AuthenticationRequest;
import com.example.auth_spring_security.Controller.vm.User.UserRequest;
import com.example.auth_spring_security.Entity.User;
import com.example.auth_spring_security.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final UserService userService;
    final ModelMapper modelMapper;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody() UserRequest requestUser) {
        User user=modelMapper.map(requestUser,User.class);
        return ResponseEntity.ok(userService.create(user));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticatioResponce> login(@Valid @RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(userService.authenticate(request));
    }
}
