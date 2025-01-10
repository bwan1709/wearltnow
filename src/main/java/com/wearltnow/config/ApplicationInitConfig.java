package com.wearltnow.config;

import com.wearltnow.constant.PredefinedRole;
import com.wearltnow.model.Role;
import com.wearltnow.model.User;
import com.wearltnow.repository.RoleRepository;
import com.wearltnow.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    static String username = "admin";
    static String password = "admin";
    @Bean
    @ConditionalOnProperty(prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver"
    )
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if(userRepository.findByUsername(username).isEmpty()){
                Role adminRole = new Role();
                adminRole.setName(PredefinedRole.DIRECTOR_ROLE);
                roleRepository.save(adminRole);
                User user = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .roles(Set.of(adminRole))
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

