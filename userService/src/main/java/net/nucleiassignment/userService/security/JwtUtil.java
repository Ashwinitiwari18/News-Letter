package net.nucleiassignment.userService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.nucleiassignment.userService.entity.Role;
import net.nucleiassignment.userService.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String SECRET_KEY;

  public Integer extractUserId(String token) {
    return Integer.parseInt(extractClaim(token, Claims::getSubject));
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
  }

  public String generateToken(Integer userId, Set<Role> roles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", roles);
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userId.toString())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }

  public String getUserIdFromToken(String token) {
    return extractAllClaims(token).getSubject();
  }

  // Validate token
  public boolean validateToken(String token, User userDetails) {
    final String userId = getUserIdFromToken(token);
    return (userId.equals(userDetails.getId().toString()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  public Set<Role> getRolesFromToken(String token) {
    Claims claims = extractAllClaims(token);

    // Extract the roles as a List and then convert it to a Set
    List<?> rolesList = claims.get("roles", List.class);

    // Convert the list to a Set<Role> (you might need to cast elements to Role if needed)
    Set<Role> roles = new HashSet<>();
    for (Object roleObj : rolesList) {
      // Cast each object in the list to Role if necessary
      if (roleObj instanceof Role) {
        roles.add((Role) roleObj);
      } else {
        // Handle the case where roles might be deserialized as another type, e.g., Map<String, Object>
        // You may need to map these manually to Role objects
        // Example: Map<String, Object> roleData = (Map<String, Object>) roleObj;
        // roles.add(new Role(roleData.get("id"), roleData.get("name")));
      }
    }

    return roles;
  }
}
