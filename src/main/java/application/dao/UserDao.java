package application.dao;

import application.models.Role;
import application.models.User;

import java.util.List;
import java.util.Set;

public interface UserDao {

    Role getRoleByName(String name);

    Set<Role> getRolesFromText(String text);

    List<User> getAllUsers();

    User getUser(long id);

    User getUserByLogin(String login);

    void addUser(User user);

    void deleteUser(Long id);

    void updateUser(long id, String log, String pas, String rol, String fn, String em);

}
