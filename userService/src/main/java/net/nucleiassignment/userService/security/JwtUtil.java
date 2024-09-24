package net.nucleiassignment.userService.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.nucleiassignment.userService.entity.Role;
import net.nucleiassignment.userService.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    System.out.println();
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
  }

  public String generateToken(Integer userId, Set<Role> roles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", roles.stream()
        .map(role -> role.getRoleName().replace("ROLE_", ""))
        .collect(Collectors.toList()));
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

  public Integer extractUserIdFromContext() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof User) {
      return ((User) authentication.getPrincipal()).getId();
    }
    return null;
  }

  public boolean validateToken(String token, User userDetails) {
    final String userId = getUserIdFromToken(token);
    return (userId.equals(userDetails.getId().toString()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  public Set<Role> getRolesFromToken(String token) {
    Claims claims = extractAllClaims(token);
    List<String> rolesList = claims.get("roles", List.class);
    Set<Role> roles = new HashSet<>();
    for (String roleName : rolesList) {
      Role role = new Role();
      role.setRoleName(roleName);
      roles.add(role);
    }
    return roles;
  }
}
