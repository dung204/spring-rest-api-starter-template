package com.example.modules.auth.entities;

import com.example.base.entities.BaseEntity;
import com.example.modules.auth.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
@DynamicInsert
public class Account extends BaseEntity implements UserDetails {

  @Column(unique = true, nullable = false)
  private String email;

  @Column
  private String password;

  @Enumerated(EnumType.STRING)
  @ColumnDefault("'USER'")
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.getValue()));
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.deletedTimestamp == null;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.deletedTimestamp == null;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.deletedTimestamp == null;
  }

  @Override
  public boolean isEnabled() {
    return this.deletedTimestamp == null;
  }
}
