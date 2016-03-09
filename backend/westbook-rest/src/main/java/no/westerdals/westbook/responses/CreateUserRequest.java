package no.westerdals.westbook.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.westerdals.westbook.model.Credential;
import no.westerdals.westbook.model.User;

@Data
@NoArgsConstructor
public class CreateUserRequest {
    private String firstname;
    private String surname;
    private String email;
    private String password;
    private String gender;
    private boolean accountLocked;
    private String[] authorities;

    public User toUser() {
        User user = new User();
        user.setFirstname(firstname);
        user.setSurname(surname);
        user.setEmail(email);
        user.setGender(gender);
        return user;
    }
}