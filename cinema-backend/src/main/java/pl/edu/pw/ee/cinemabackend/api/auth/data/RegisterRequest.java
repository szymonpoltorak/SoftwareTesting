package pl.edu.pw.ee.cinemabackend.api.auth.data;

import lombok.Builder;
import pl.edu.pw.ee.cinemabackend.entities.user.interfaces.Password;

@Builder
public record RegisterRequest(String name, String surname, String username, @Password String password) {
}
