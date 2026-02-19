package com.uab.taller.store.usecase.user;

import com.uab.taller.store.domain.Account;
import com.uab.taller.store.domain.Profile;
import com.uab.taller.store.domain.Rol;
import com.uab.taller.store.domain.User;
import com.uab.taller.store.domain.dto.request.CreateUserRequest;
import com.uab.taller.store.domain.dto.response.UserResponse;
import com.uab.taller.store.service.interfaces.IAccountService;
import com.uab.taller.store.service.interfaces.IProfileService;
import com.uab.taller.store.service.interfaces.IUserService;
import com.uab.taller.store.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreateUserUseCase {

    @Autowired
    private IUserService userService;

    @Autowired
    private IProfileService profileService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UserMappingUseCase userMappingUseCase;

    @Transactional
    public UserResponse save(CreateUserRequest createUserRequest) {
        if (createUserRequest == null) {
            throw new IllegalArgumentException("Los datos del usuario no pueden ser nulos");
        }

        // Validar que el email no esté en uso
        validateEmailNotExists(createUserRequest.getEmail());

        // Crear perfil
        Profile profile = createProfile(createUserRequest);
        Profile savedProfile = profileService.save(profile);

        // Crear usuario
        User user = createUser(createUserRequest, savedProfile);
        User savedUser = userService.save(user);

        // Crear cuenta automáticamente si se especificó
        if (shouldCreateAccount(createUserRequest)) {
            Account account = createAccount(createUserRequest, savedUser);
            Account savedAccount = accountService.save(account);

            savedUser.getAccounts().add(savedAccount);
            savedUser = userService.update(savedUser);
        }

        return userMappingUseCase.toUserResponse(savedUser);
    }

    private void validateEmailNotExists(String email) {
        Optional<User> existingUser = userService.getByEmail(email.trim());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + email);
        }
    }

    private Profile createProfile(CreateUserRequest request) {
        Profile profile = new Profile();
        profile.setName(request.getName().trim());
        profile.setLastName(request.getLastName().trim());

        if (request.getCi() != null && !request.getCi().trim().isEmpty()) {
            profile.setCi(request.getCi().trim());
        }

        if (request.getMobile() != null && !request.getMobile().trim().isEmpty()) {
            profile.setMobile(request.getMobile().trim());
        }

        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            profile.setAddress(request.getAddress().trim());
        }

        profile.setStatus("ACTIVE");
        return profile;
    }

    private User createUser(CreateUserRequest request, Profile profile) {
        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setPassword(request.getPassword()); // En producción debería estar hasheada
        user.setProfile(profile);
        user.setAccounts(new ArrayList<>()); // Asignar rol por defecto "USER"
        try {
            Rol defaultRol = rolRepository.findByNameIgnoreCase("USER")
                    .orElseThrow(() -> new IllegalStateException("No se pudo encontrar el rol por defecto 'USER'"));
            user.setRol(defaultRol);
        } catch (Exception e) {
            throw new IllegalStateException("Error al asignar rol por defecto: " + e.getMessage(), e);
        }

        return user;
    }

    private boolean shouldCreateAccount(CreateUserRequest request) {
        return request.getSaldoInicial() != null &&
                request.getSaldoInicial().compareTo(BigDecimal.ZERO) >= 0 &&
                request.getTipoCuenta() != null &&
                !request.getTipoCuenta().trim().isEmpty();
    }

    private Account createAccount(CreateUserRequest request, User user) {
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setType(request.getTipoCuenta());
        account.setCurrency(request.getMoneda() != null ? request.getMoneda() : "BOB");
        account.setBalance(request.getSaldoInicial());
        account.setStatus("ACTIVE");
        account.setUser(user);

        return account;
    }

    private String generateAccountNumber() {
        // Generar número de cuenta único
        return "ACC" + System.currentTimeMillis() +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
