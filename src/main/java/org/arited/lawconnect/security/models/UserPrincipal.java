package org.arited.lawconnect.security.models;

import lombok.Getter;
import org.arited.lawconnect.core.entities.User;
import org.arited.lawconnect.core.enums.RoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

    private final Long   id;
    private final String email;
    private final String password;
    private final RoleEnum   role;
    private Map<String, Object> attributes; // Google raw attributes

    private UserPrincipal(User user) {
        this.id       = user.getUserId();
        this.email    = user.getEmail();
        this.password = user.getPassword();
        this.role     = user.getRole();
    }

    /** For standard JWT login */
    public static UserPrincipal create(User user) {
        return new UserPrincipal(user);
    }

    /** For OAuth2 Google login */
    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal principal = new UserPrincipal(user);
        principal.attributes = attributes;
        return principal;
    }

    @Override public String  getUsername()              { return email; }
    @Override public String  getPassword()              { return password; }
    @Override public boolean isAccountNonExpired()      { return true; }
    @Override public boolean isAccountNonLocked()       { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public boolean isEnabled()                { return true; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));    }

    // OAuth2User
    @Override public Map<String, Object> getAttributes() { return attributes; }
    @Override public String getName()                    { return email; }
}


