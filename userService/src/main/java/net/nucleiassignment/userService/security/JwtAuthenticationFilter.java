package net.nucleiassignment.userService.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.nucleiassignment.userService.entity.Role;
import net.nucleiassignment.userService.entity.User;
import net.nucleiassignment.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Lazy
  @Autowired
  private JwtUtil jwtUtil;

  @Lazy
  @Autowired
  private UserService userService;

  @Override
  protected void doFilterInternal (HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
      throws ServletException, IOException {
    final String authorizationHeader = request.getHeader("Authorization");
    Integer userId = null;
    String jwt = null;
    Set<Role> roles = null;

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
      try {
        userId = jwtUtil.extractUserId(jwt);
        roles = jwtUtil.getRolesFromToken(jwt);
      } catch (JwtException e) {
        logger.error("JWT token is invalid", e);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("JWT token is invalid");
        return;
      }
    }

    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      User user = userService.getUserById(userId).get();
      if (jwtUtil.validateToken(jwt, user)) {
        List<GrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
            .collect(Collectors.toList());
        var authToken = new UsernamePasswordAuthenticationToken(
            user,
            null,
            authorities
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}