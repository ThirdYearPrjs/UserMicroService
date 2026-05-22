package bt.edu.gcit.usermicroservice.dao;

import bt.edu.gcit.usermicroservice.entity.User;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import bt.edu.gcit.usermicroservice.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class UserDAOImpl implements UserDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public UserDAOImpl() {
    }

    @Override
    public User save(User user) {
        return entityManager.merge(user);
    }

    @Override
    public List<User> getAllUsers() {
        // Implement the logic to retrieve all users from the database
        // and return them as a list
        TypedQuery<User> query = entityManager.createQuery("from User", User.class);
        List<User> users = query.getResultList();
        return users;
    }

    @Override
    public User findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery("from User where email = :email", User.class);
        query.setParameter("email", email);
        List<User> users = query.getResultList();
        System.out.println(users.size());
        if (users.isEmpty()) {
            return null;
        } else {
            System.out.println(users.get(0) + " " + users.get(0).getEmail());
            return users.get(0);
        }
    }

    @Override
    public User findByID(Long theId) {
        // Implement the logic to find a user by their ID in the database
        // and return the user object
        User user = entityManager.find(User.class, theId);
        return user;
    }

@Override
public void deleteById(Long theId) {
    User user = entityManager.find(User.class, theId); 
    if (user != null) {
        user.getRoles().clear();
        entityManager.remove(user);
    }
}

    @Override
    public void updateUserEnabledStatus(Long id, boolean enabled) {
        User user = entityManager.find(User.class, id);
        System.out.println(user);
        if (user == null) {
            throw new UserNotFoundException("User not found with id " + id);
        }
        user.setEnabled(enabled);
        entityManager.persist(user);
    }

}
