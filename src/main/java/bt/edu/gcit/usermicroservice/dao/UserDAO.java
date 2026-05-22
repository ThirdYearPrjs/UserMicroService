package bt.edu.gcit.usermicroservice.dao;

import java.util.List;

import bt.edu.gcit.usermicroservice.entity.User;

public interface UserDAO {
    List<User> getAllUsers();
    User save(User user);
    User findByEmail(String email);
    User findByID(Long theId);
    void deleteById(Long theId);
    void updateUserEnabledStatus(Long id, boolean enabled);
}