package com.app.thym.ddejim.mafudis.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry_date")
    private LocalDateTime resetPasswordTokenExpiryDate;

    // ⭐ CAMBIO CRÍTICO: Usar Integer en lugar de int para permitir null
    @Column(name = "failed_attempt_count")
    private Integer failedAttemptCount = 0;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Campo temporal para confirmar contraseña (no se persiste en BD)
    @Transient
    private String confirmPassword;

    // Getters y Setters personalizados para manejar null

    public int getFailedAttemptCount() {
        return failedAttemptCount != null ? failedAttemptCount : 0;
    }

    public void setFailedAttemptCount(Integer failedAttemptCount) {
        this.failedAttemptCount = failedAttemptCount != null ? failedAttemptCount : 0;
    }

    // Los demás getters/setters los genera Lombok con @Data
    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDateTime getResetPasswordTokenExpiryDate() {
        return resetPasswordTokenExpiryDate;
    }

    public void setResetPasswordTokenExpiryDate(LocalDateTime resetPasswordTokenExpiryDate) {
        this.resetPasswordTokenExpiryDate = resetPasswordTokenExpiryDate;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }
}