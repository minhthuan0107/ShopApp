package com.project.shopapp.configurations;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        com.project.shopapp.models.User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with phone number = " + phoneNumber));
        return UserDetailsImpl.build(user);
    }
    public UserDetails loadUserByGoogleAccountId(String googleAccountId) throws UsernameNotFoundException {
        User user = userRepository.findByGoogleAccountId(googleAccountId)
                .orElseThrow(() -> new UsernameNotFoundException("Google account not found = "+googleAccountId));
        return UserDetailsImpl.build(user);
    }
}
