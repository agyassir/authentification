package com.example.auth_spring_security.Service.Impl;
import com.example.auth_spring_security.Controller.vm.AuthenticatioResponce;
import com.example.auth_spring_security.Controller.vm.AuthenticationRequest;
import com.example.auth_spring_security.Entity.Role;
import com.example.auth_spring_security.Entity.Token;
import com.example.auth_spring_security.Entity.TokenType;
import com.example.auth_spring_security.Entity.User;
import com.example.auth_spring_security.Exception.AlreadyExistException;
import com.example.auth_spring_security.Repository.TokenRepository;
import com.example.auth_spring_security.Repository.UserRepository;
import com.example.auth_spring_security.Service.JWTService;
import com.example.auth_spring_security.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final private UserRepository userRepository;
    @Autowired
    private final JWTService jwtService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticatioResponce create(User user) {
        if (userRepository.findUserByEmail(user.getEmail()).isEmpty()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.USER);
            User savedUser = userRepository.save(user);
            var jwtToken=jwtService.generateToken(savedUser);
            var jwtRefreshToken=jwtService.generateRefreshingToken(user);
            return AuthenticatioResponce.builder()
                    .accessToken(jwtToken)
                    .refreshToken(jwtRefreshToken)
                    .build();
        }
        throw new AlreadyExistException();
    }

    @Override
    public AuthenticatioResponce authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  request.getEmail(),
                  request.getPassword()
          )
        );
        var user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshingToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticatioResponce.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

    }
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
