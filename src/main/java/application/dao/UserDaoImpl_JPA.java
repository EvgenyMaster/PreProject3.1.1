package application.dao;

import org.springframework.stereotype.Repository;
import application.models.Role;
import application.models.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserDaoImpl_JPA implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Role getRoleByName(String name) {
        try {
            entityManager
                    .createQuery("select r from Role r where r.role = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            // если такой роли не существует в БД, то добавляем её
            Role r = new Role();
            r.setRole(name);
            entityManager.persist(r);
        }
        // заново достаём роль из БД
        return entityManager
                .createQuery("select r from Role r where r.role = :name", Role.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    @Override
    public Set<Role> getRolesFromText(String text) {
        Set<Role> roles = new HashSet<>();

        text = text.toLowerCase();

        if (text.contains("admin")) {
            Role role = getRoleByName("ROLE_ADMIN");
            roles.add(role);
        }
        if (text.contains("user")) {
            Role role = getRoleByName("ROLE_USER");
            roles.add(role);
        }

        return roles;
    }

    @Override
    public List<User> getAllUsers() {
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }

    @Override
    public User getUser(long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User getUserByLogin(String login) {
        return entityManager.createQuery("select u from User u where u.login = :login", User.class)
                .setParameter("login", login).getSingleResult();
    }

    @Override
    public void addUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public void deleteUser(Long id) {
        entityManager.createQuery("delete from User u where u.id = :id")
                .setParameter("id", id).executeUpdate();
    }

    @Override
    public void updateUser(long id, String log, String pas, String rol, String fn, String em) {
        User u = entityManager.getReference(User.class, id);

        if (log.length() > 0) {
            u.setLogin(log);
        }
        if (pas.length() > 0) {
            u.setPassword(pas);
        }
        if (rol.length() > 0) {
            u.setRoles(getRolesFromText(rol));
        }
        if (fn.length() > 0) {
            u.setName(fn);
        }
        if (em.length() > 0) {
            u.setEmail(em);
        }
    }
}