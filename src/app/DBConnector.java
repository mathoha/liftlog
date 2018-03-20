package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private static String url = "";
    private static String dbName = "";
    private static String user = "";
    private static String password = "";

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        DBConnector.url = url;
    }

    public static String getDbName() {
        return dbName;
    }

    public static void setDbName(String dbName) {
        DBConnector.dbName = dbName;
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        DBConnector.user = user;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DBConnector.password = password;
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://"+url+"/"+dbName,user,password);
        return connection;
    }
}
