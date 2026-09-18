package com.example.ecommerce.auth;


import com.example.ecommerce.common.exception.DuplicateEmailException;
import com.example.ecommerce.common.exception.DuplicateUsernameException;
import com.example.ecommerce.common.exception.UserNotFoundException;
import com.example.ecommerce.user.config.JwtService;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {



        if(userRepository.findByEmail(request.getEmail()).isPresent()){
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
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();

userRepository.save(user);
var jwtToken = jwtService.generateToken(user);
    return  AuthenticationResponse.builder()
            .token(jwtToken)
            .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

         authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

         var user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new UserNotFoundException("user not found"));

         var jwt = jwtService.generateToken(user);



        return AuthenticationResponse.builder()
                                     .token(jwt)
                                     .build();
    }
}
