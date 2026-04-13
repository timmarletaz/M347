package com.m347.pollit.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(max = 15)
    @Pattern(
            regexp = "^[A-Z][a-z]*$",
            message = "Vorname: Erster Buchstabe gross, Rest klein, keine Sonderzeichen"
    )
    private String firstname;

    @NotBlank
    @Size(max = 35)
    @Pattern(
            regexp = "^[A-Z][a-z]*$",
            message = "Nachname: Erster Buchstabe gross, Rest klein, keine Sonderzeichen"
    )
    private String lastname;

    @NotBlank
    @Email(message = "Ungültige Email-Adresse")
    private String email;

    @NotBlank
    @Size(min = 8)
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$",
            message = "Passwort braucht: 1 Grossbuchstabe, 1 Kleinbuchstabe, 1 Zahl, 1 Sonderzeichen"
    )
    private String password;
}
