package org.asu.group8.impl;

import org.asu.group8.entity.SbsUser;
import org.asu.group8.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public UserDetails loadUserByUsername(String username) {
        SbsUser sbsUser = userRepository.findByUsername(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        // check if the username was found in the database
        if (sbsUser == null) {
            return null;
        } else {
            return new User(username, sbsUser.getPassword(), grantedAuthorities);
        }
    }

}
