package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static springbook.jdbc.connection.ConnectionConst.*;

public class DConnectionMaker implements ConnectionMaker{
    @Override
    public Connection makeConnection() throws SQLException {
        Connection c = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        return c;
    }
}
