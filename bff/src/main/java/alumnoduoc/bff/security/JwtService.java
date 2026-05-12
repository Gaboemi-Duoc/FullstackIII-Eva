package alumnoduoc.bff.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "mySuperSecretKeyForJwtAuthentication123456";

    // Generar token
    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)
                )
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraer username
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    // Validar token
    public boolean isTokenValid(String token, String username) {

        final String extractedUsername = extractUsername(token);

        return extractedUsername.equals(username)
                && !isTokenExpired(token);
    }

    // Verificar expiración
    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    // Obtener expiración
    private Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    // Extraer claims
    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {

        final Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    // Extraer payload
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Obtener key
    private Key getSignKey() {

        byte[] keyBytes = SECRET_KEY.getBytes();

        return Keys.hmacShaKeyFor(keyBytes);
    }
}