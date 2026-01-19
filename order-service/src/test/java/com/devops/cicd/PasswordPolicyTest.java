package com.devops.cicd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyTest {

    @Test
    @DisplayName("Un mot de passe fort doit être validé")
    void shouldValidateStrongPassword() {
        assertTrue(PasswordPolicy.isStrong("Valid123!"));
    }

    @Test
    @DisplayName("Doit rejeter si moins de 8 caractères")
    void shouldRejectTooShort() {
        assertFalse(PasswordPolicy.isStrong("V123!"), "Trop court");
    }

    @Test
    @DisplayName("Doit rejeter s'il manque une majuscule")
    void shouldRejectNoUppercase() {
        assertFalse(PasswordPolicy.isStrong("valid123!"), "Manque majuscule");
    }

    @Test
    @DisplayName("Doit rejeter s'il manque un chiffre")
    void shouldRejectNoDigit() {
        assertFalse(PasswordPolicy.isStrong("Valid!!!"), "Manque chiffre");
    }

    @Test
    @DisplayName("Doit rejeter s'il manque un caractère spécial")
    void shouldRejectNoSpecialChar() {
        assertFalse(PasswordPolicy.isStrong("Valid1234"), "Manque caractère spécial");
    }
}