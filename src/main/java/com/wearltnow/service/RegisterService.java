package com.wearltnow.service;

import com.wearltnow.dto.request.auth.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RegisterService {
    
    private final Map<String, RegisterRequest> pendingRegistrations = new HashMap<>();

    public void saveRegisterRequest(RegisterRequest registration) {
        pendingRegistrations.put(registration.getEmail(), registration);
    }

    public RegisterRequest getRegisterRequest(String email) {
        return pendingRegistrations.get(email);
    }

    public void removeRegisterRequest(String email) {
        pendingRegistrations.remove(email);
    }

    public Optional<RegisterRequest> findByEmail(String email) {
        return Optional.ofNullable(pendingRegistrations.get(email));
    }
}
