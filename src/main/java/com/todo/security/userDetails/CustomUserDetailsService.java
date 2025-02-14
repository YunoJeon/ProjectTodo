package com.todo.security.userDetails;

import static org.springframework.security.core.userdetails.User.*;

import com.todo.user.entity.User;
import com.todo.user.service.UserQueryService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserQueryService userQueryService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userQueryService.findByEmail(email);

    return withUsername(user.getEmail())
        .password(user.getPassword())
        .authorities(Collections.emptyList())
        .build();
  }
}