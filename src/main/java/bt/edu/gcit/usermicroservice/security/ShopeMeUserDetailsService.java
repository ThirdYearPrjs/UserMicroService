package bt.edu.gcit.usermicroservice.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bt.edu.gcit.usermicroservice.dao.UserDAO;
import bt.edu.gcit.usermicroservice.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import bt.edu.gcit.usermicroservice.security.ShopmeuserDetails;
import bt.edu.gcit.usermicroservice.dao.CustomerDAO;
import bt.edu.gcit.usermicroservice.entity.Customer;
import java.util.Collections;

@Service
@Transactional(readOnly = true)
public class ShopeMeUserDetailsService implements UserDetailsService {
    @Autowired
    private final UserDAO userDAO;
    private final CustomerDAO customerDAO;

    @Autowired
    public ShopeMeUserDetailsService(UserDAO userDAO, CustomerDAO customerDAO) {
        this.userDAO = userDAO;
        this.customerDAO = customerDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Check Admin User Table
        User user = userDAO.findByEmail(email);
        if (user != null) {
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.isEnabled(), // Use the actual status
                    true, true, true,
                    authorities);
        }

        // 2. Check Customer Table
        Customer customer = customerDAO.findByEMail(email);
        if (customer != null) {
            // Create a default authority so they aren't "empty"
            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("Customer"));

            return new org.springframework.security.core.userdetails.User(
                    customer.getEmail(),
                    customer.getPassword(),
                    customer.getEnabled(), // Your DB shows '1' (true), so this is correct
                    true, true, true,
                    authorities); // Pass the new authority list here
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }

}