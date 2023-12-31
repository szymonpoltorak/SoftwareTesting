package pl.edu.pw.ee.cinemabackend.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.edu.pw.ee.cinemabackend.config.constants.Headers;
import pl.edu.pw.ee.cinemabackend.config.constants.Matchers;
import pl.edu.pw.ee.cinemabackend.config.constants.Properties;
import pl.edu.pw.ee.cinemabackend.config.jwt.interfaces.JwtService;
import pl.edu.pw.ee.cinemabackend.entities.user.User;
import pl.edu.pw.ee.cinemabackend.exceptions.auth.TokenDoesNotExistException;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value(Properties.ENCODING_KEY_PROPERTY)
    private String encodingKey;

    @Value(Properties.EXPIRATION_PROPERTY)
    private long expirationTime;

    @Value(Properties.REFRESH_PROPERTY)
    private long refreshTime;

    @Override
    public final Optional<String> getUsernameFromToken(String jwtToken) {
        return getClaimFromToken(jwtToken, Claims::getSubject);
    }

    @Override
    public final <T> Optional<T> getClaimFromToken(String jwtToken, Function<Claims, T> claimsHandler) {
        Claims claims = getAllClaims(jwtToken);

        return Optional.ofNullable(claimsHandler.apply(claims));
    }

    @Override
    public final String generateRefreshToken(UserDetails userDetails) {
        return buildToken(Collections.emptyMap(), userDetails, refreshTime);
    }

    @Override
    public final String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = Collections.emptyMap();
        if(userDetails instanceof User user) {
            claims = new HashMap<>() {{
                put("fullName", user.getFullName());
                put("role", user.getUserRole());
            }};
        }
        return generateToken(claims, userDetails, expirationTime);
    }

    @Override
    public final String generateToken(Map<String, Object> additionalClaims, UserDetails userDetails, long expiration) {
        return buildToken(additionalClaims, userDetails, expiration);
    }

    @Override
    public final boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        Optional<String> username = getUsernameFromToken(jwtToken);

        return username.filter(s -> s.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken)).isPresent();
    }

    @Override
    public final Optional<String> getJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader(Headers.AUTH_HEADER);

        if (request.getServletPath().contains(Matchers.AUTH_MAPPING) || authHeader == null || !authHeader.startsWith(Headers.TOKEN_HEADER)) {
            return Optional.empty();
        }
        return Optional.of(authHeader.substring(Headers.TOKEN_START_INDEX));
    }

    @Override
    public final Optional<String> getJwtRefreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader(Headers.AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(Headers.TOKEN_HEADER)) {
            return Optional.empty();
        }
        return Optional.of(authHeader.substring(Headers.TOKEN_START_INDEX));
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(buildSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String buildToken(Map<String, Object> additionalClaims, UserDetails userDetails, long expiration) {
        long time = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(additionalClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(time))
                .setExpiration(new Date(time + expiration))
                .signWith(buildSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        Optional<Date> optionalDate = getClaimFromToken(token, Claims::getExpiration);

        if (optionalDate.isEmpty()) {
            throw new TokenDoesNotExistException("Token without expiration date does not exists!");
        }
        return optionalDate.get();
    }

    private Key buildSignInKey() {
        byte[] decodedKey = Decoders.BASE64.decode(encodingKey);

        return Keys.hmacShaKeyFor(decodedKey);
    }
}
