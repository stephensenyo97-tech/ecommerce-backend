package com.example.ecommerce.auth;


import com.example.ecommerce.common.exception.DuplicateEmailException;
import com.example.ecommerce.common.exception.UserNotFoundException;
import com.example.ecommerce.user.config.JwtService;
import com.example.ecommerce.user.entity.Role;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

var email = request.getEmail().trim().toLowerCase();

        if(userRepository.findByEmail(email).isPresent()){
            throw new DuplicateEmailException("email already taken " + request.getEmail());
        }
        String firstname = request.getFirstname().trim();
        String lastname = request.getLastname().trim();

    var baseUsername = firstname.toLowerCase().concat(lastname.toLowerCase());
        int count = 1;

        String username = baseUsername;
        while(userRepository.findByUsername(username).isPresent()){
            username = baseUsername + count;

            count ++;

        }

    var user = User.builder()
            .username(username)
            .firstname(firstname)
            .lastname(lastname)
            .email(email)
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Set.of(Role.ROLE_CUSTOMER))
            .build();

userRepository.save(user);
var jwt = jwtService.generateToken(user);

    return buildAuthResponse( jwt);

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

         authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

         var user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new UserNotFoundException("user not found"));

         var jwt = jwtService.generateToken(user);



        return buildAuthResponse(jwt);
    }

   private AuthenticationResponse buildAuthResponse(String jwt){
        return AuthenticationResponse
                .builder()
                .token(jwt)
                .build();
   }
}

