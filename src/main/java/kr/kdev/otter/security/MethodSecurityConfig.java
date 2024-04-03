package kr.kdev.otter.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableMethodSecurity(proxyTargetClass = true)
@Configuration
public class MethodSecurityConfig {
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > permission:all\nROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Bean("authz")
    public Authorizer authorizer() {
        return new Authorizer();
    }

    public static class Authorizer {
        public boolean hasOwnership(MethodSecurityExpressionOperations operations) {
            // ... authorization logic
            return true;
        }
    }
}
