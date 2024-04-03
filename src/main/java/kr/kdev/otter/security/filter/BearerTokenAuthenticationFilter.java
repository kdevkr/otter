package kr.kdev.otter.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kdev.otter.jwt.JwtFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtFactory jwtFactory;

    public BearerTokenAuthenticationFilter(JwtFactory jwtFactory) {
        this.jwtFactory = jwtFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.toLowerCase().startsWith("bearer")) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Token must start with Bearer. ex) Bearer XXX");
        } else {
            String token = header.substring("Bearer ".length());
            Jws<Claims> claimsJws = jwtFactory.parseToken(token);
            Claims payload = claimsJws.getPayload();
            boolean isAccessType = "access".equalsIgnoreCase(payload.get("type", String.class));
            if (isAccessType) {
                String id = payload.getSubject();
                UserDetails principal = User.builder().username(id).password(token).build();
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(principal, token, AuthorityUtils.NO_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } else {
                response.sendError(HttpStatus.BAD_REQUEST.value(), "Token required access type.");
            }
        }
    }
}
