package me.peihao.autoInvest.model;

import java.util.Collection;
import java.util.Collections;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.peihao.autoInvest.constant.AppUserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class AppUser implements UserDetails {

  @Id
  // TODO: What is sequence generator
  @GeneratedValue(
      strategy = GenerationType.IDENTITY
  )
  private Long id;
  private String name;
  private String username;
  private String email;
  private String password;
  @Enumerated(EnumType.STRING)
  private AppUserRole appUserRole;
  private Boolean locked;
  private Boolean enabled;

  public AppUser(String name, String username, String email, String password,
      AppUserRole appUserRole) {
    this.name = name;
    this.username = username;
    this.email = email;
    this.password = password;
    this.appUserRole = appUserRole;
    this.locked = false;
    this.enabled = false;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(appUserRole.name());
    return Collections.singletonList(authority);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
