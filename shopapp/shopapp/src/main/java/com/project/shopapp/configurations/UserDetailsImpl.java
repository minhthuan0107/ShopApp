package com.project.shopapp.configurations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.shopapp.models.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String fullname;
    private String phoneNumber;
    private String address;
    @JsonIgnore
    private String password;
    private boolean isActive;
    private LocalDate dateOfBirth;
    private int facebookAccountId;
    private int googleAccountId;
    private Collection<? extends GrantedAuthority> authorities;
    public UserDetailsImpl(Long id, String fullname, String phoneNumber, String address, String password,
                           boolean isActive, LocalDate dateOfBirth, int facebookAccountId, int googleAccountId,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
        this.isActive = isActive;
        this.dateOfBirth = dateOfBirth;
        this.facebookAccountId = facebookAccountId;
        this.googleAccountId = googleAccountId;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
        UserDetailsImpl userDetails = new UserDetailsImpl(user.getId(), user.getFullname(), user.getPhoneNumber(), user.getAddress(), user.getPassword()
                , user.isActive(), user.getDateOfBirth(), user.getFacebookAccountId(), user.getGoogleAccountId(), authorities);
        return userDetails;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }
}
