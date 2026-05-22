package bt.edu.gcit.usermicroservice.service;

import bt.edu.gcit.usermicroservice.dao.UserDAO;
import bt.edu.gcit.usermicroservice.dao.RoleDAO;
import bt.edu.gcit.usermicroservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Lazy;
import bt.edu.gcit.usermicroservice.exception.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO; // 1. Uncommented to make it accessible in methods
    private final BCryptPasswordEncoder passwordEncoder;
    private final String uploadDir = "src/main/resources/static/images";

    @Autowired
    @Lazy
    public UserServiceImpl(UserDAO userDAO, RoleDAO roleDAO, BCryptPasswordEncoder passwordEncoder) {
        // 2. Updated Constructor parameters to properly inject RoleDAO bean context
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userDAO.save(user);
    }

    @Override
    public boolean isEmailDuplicate(String email) {
        User user = userDAO.findByEmail(email);
        return user != null;
    }

    @Override
    public User findByID(Long theId) {
        return userDAO.findByID(theId);
    }

    @Transactional
    @Override
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userDAO.findByID(id);

        if (existingUser != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setEnabled(updatedUser.isEnabled());

            // --- SAFE ROLE PERSISTENCE MANAGEMENT ---
            java.util.Set<bt.edu.gcit.usermicroservice.entity.Role> managedRoles = new java.util.HashSet<>();

            if (updatedUser.getRoles() != null) {
                for (bt.edu.gcit.usermicroservice.entity.Role transientRole : updatedUser.getRoles()) {
                    // Safe lookup from active database session using our newly uncommented field
                    bt.edu.gcit.usermicroservice.entity.Role safeDbRole = roleDAO.findByName(transientRole.getName());
                    if (safeDbRole != null) {
                        managedRoles.add(safeDbRole);
                    }
                }
            }

            existingUser.setRoles(managedRoles);
            // ----------------------------------------

            if (updatedUser.getPhoto() != null) {
                existingUser.setPhoto(updatedUser.getPhoto());
            }

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            } else {
                existingUser.setPassword(existingUser.getPassword());
            }

            return userDAO.save(existingUser);
        }
        return null;
    }

    @Transactional
    @Override
    public void deleteById(Long theId) {
        userDAO.deleteById(theId);
    }

    @Transactional
    @Override
    public void updateUserEnabledStatus(Long id, boolean enabled) {
        userDAO.updateUserEnabledStatus(id, enabled);
    }

    @Transactional
    @Override
    public void uploadUserPhoto(Long id, MultipartFile photo) throws IOException {
        // Method body empty or handled elsewhere
    }
}