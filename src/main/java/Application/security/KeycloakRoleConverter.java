package Application.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        // 🔹 1. Realm roles
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> realmRoles) {
            realmRoles.forEach(r -> roles.add("ROLE_" + r.toString()));
        }

        // 🔹 2. Client roles
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            resourceAccess.forEach((client, value) -> {
                if (value instanceof Map<?, ?> clientData && clientData.get("roles") instanceof List<?> clientRoles) {
                    clientRoles.forEach(r -> roles.add("ROLE_" + r.toString())); // optional: add client prefix
                    // e.g., roles.add("ROLE_" + client + "_" + r.toString());
                }
            });
        }

        // 🔹 3. Convert to GrantedAuthority
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
