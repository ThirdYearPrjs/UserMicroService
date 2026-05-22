package bt.edu.gcit.usermicroservice.dao;

import bt.edu.gcit.usermicroservice.entity.Role;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Repository
public class RoleDAOImpl implements RoleDAO {
    private EntityManager entityManager;

    @Autowired
    public RoleDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void addRole(Role role) {
        // TODO Auto-generated method
        entityManager.persist(role);
    }

    @Override
    public Role findByName(String name) {
        // Query the database for the role by name
        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.name = :name", Role.class);
        query.setParameter("name", name);

        List<Role> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }
}