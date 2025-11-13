package com.app.thym.ddejim.mafudis.repository;

import com.app.thym.ddejim.mafudis.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    // Método para buscar por email
    Optional<Usuario> findByEmail(String email);

    // Método para verificar si existe un username en otro usuario (útil para actualizaciones)
    @Query("SELECT u FROM Usuario u WHERE u.username = :username AND u.id != :userId")
    Optional<Usuario> findByUsernameAndNotId(@Param("username") String username, @Param("userId") Long userId);

    // Método para verificar si existe un email en otro usuario (útil para actualizaciones)
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.id != :userId")
    Optional<Usuario> findByEmailAndNotId(@Param("email") String email, @Param("userId") Long userId);

    // ====================== CONSULTAS CON ROLES ======================

    /**
     * Esta consulta personalizada utiliza JOIN FETCH para cargar un usuario y su colección
     * de roles en una única consulta a la base de datos.
     */
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<Usuario> findByIdWithRoles(@Param("id") Long id);

    /**
     * Esta consulta carga TODOS los usuarios y sus respectivos roles de una sola vez.
     */
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles")
    List<Usuario> findAllWithRoles();

    Optional<Usuario> findByResetPasswordToken(String token);
}