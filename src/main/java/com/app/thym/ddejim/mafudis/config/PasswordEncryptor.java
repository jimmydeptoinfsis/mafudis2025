import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncryptor {

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String plainPassword = "admin"; // <-- ¡Pon aquí la contraseña que quieras!
        String hashedPassword = passwordEncoder.encode(plainPassword);

        System.out.println("Contraseña en texto plano: " + plainPassword);
        System.out.println("Contraseña cifrada (hash): " + hashedPassword);
    }
}
