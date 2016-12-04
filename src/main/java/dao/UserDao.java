package dao;

import domain.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    public void add(User user) throws SQLException;
    public void update(User user) throws SQLException;
    public void delete(User user) throws SQLException;
    public User findById(int id) throws SQLException;
    public List<User> findAll() throws SQLException;
}
