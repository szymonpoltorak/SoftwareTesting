package pl.edu.pw.ee.cinemabackend.jmh;

import org.openjdk.jmh.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.ee.cinemabackend.api.auth.data.AuthResponse;
import pl.edu.pw.ee.cinemabackend.api.auth.data.LoginRequest;
import pl.edu.pw.ee.cinemabackend.api.auth.data.TokenRequest;
import pl.edu.pw.ee.cinemabackend.api.auth.interfaces.AuthService;
import pl.edu.pw.ee.cinemabackend.config.jwt.interfaces.JwtService;
import pl.edu.pw.ee.cinemabackend.entities.token.interfaces.TokenRepository;
import pl.edu.pw.ee.cinemabackend.entities.user.User;
import pl.edu.pw.ee.cinemabackend.entities.user.UserRole;
import pl.edu.pw.ee.cinemabackend.entities.user.interfaces.UserRepository;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BenchmarkJwtServiceRunner extends AbstractBenchmark {
    private static AuthService authService;
    private static UserRepository userRepository;
    private static TokenRepository tokenRepository;
    private static JwtService jwtService;
    private static final String LOGMAIL = "logusername@mail.com";
    private static final String PASSWORD = "kicia.?312312312As";
    private static PasswordEncoder passwordEncoder;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private TokenRequest tokenRequest;
    private User admin;
    private User user;

    @Autowired
    public final void setContext(AuthService authService, JwtService jwtService, UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder) {
        BenchmarkJwtServiceRunner.authService = authService;
        BenchmarkJwtServiceRunner.userRepository = userRepository;
        BenchmarkJwtServiceRunner.tokenRepository = tokenRepository;
        BenchmarkJwtServiceRunner.passwordEncoder = passwordEncoder;
        BenchmarkJwtServiceRunner.jwtService = jwtService;
    }

    @Setup(Level.Trial)
    public final void setBenchmarkTrial() {

        admin = User.builder()
                .name("Jakub")
                .password(passwordEncoder.encode(PASSWORD))
                .username(LOGMAIL)
                .surname("Wysocki")
                .userRole(UserRole.ADMIN)
                .build();

        loginRequest = LoginRequest
                .builder()
                .username(LOGMAIL)
                .password(PASSWORD)
                .build();
        user = User.builder()
                .name("Jakub")
                .password(PASSWORD)
                .username(LOGMAIL)
                .surname("Wysocki")
                .userRole(UserRole.ADMIN)
                .build();
    }

    @Setup(Level.Invocation)
    public final void setupBenchmark() {
        userRepository.saveAndFlush(admin);

        authResponse = authService.login(loginRequest);
        tokenRequest = TokenRequest
                .builder()
                .authToken(authResponse.authToken())
                .build();
    }

    @TearDown(Level.Invocation)
    public final void clear() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Benchmark
    public final void generateTokenBenchmark() {
        jwtService.generateToken(user);
    }

    @Benchmark
    public final void generateRefreshTokenBenchmark() {
        jwtService.generateRefreshToken(user);
    }

    @Benchmark
    public final void getUsernameFromTokenBenchmark() {
        jwtService.getUsernameFromToken(tokenRequest.authToken());
    }

    @Benchmark
    public final void isTokenValidBenchmark() {
        jwtService.isTokenValid(tokenRequest.authToken(), admin);
    }

}
