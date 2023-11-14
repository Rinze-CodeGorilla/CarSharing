package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    Connection connection;

    public Database(String dbName) throws ClassNotFoundException, SQLException {
        var cs = "jdbc:h2:./src/carsharing/db/" + dbName;
        Class.forName("org.h2.Driver");
        connection = DriverManager.getConnection(cs);
        connection.setAutoCommit(true);
        var stmt = connection.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS COMPANY " +
                        "(ID INT PRIMARY KEY AUTO_INCREMENT" +
                        ",NAME VARCHAR UNIQUE NOT NULL" +
                        ")");
        stmt.close();
        stmt = connection.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS CAR " +
                        "(ID INT PRIMARY KEY AUTO_INCREMENT" +
                        ",NAME VARCHAR UNIQUE NOT NULL" +
                        ",COMPANY_ID INT NOT NULL" +
                        ",CONSTRAINT fk_company FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID)" +
                        ")");
        stmt.close();
        stmt = connection.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS CUSTOMER " +
                        "(ID INT PRIMARY KEY AUTO_INCREMENT" +
                        ",NAME VARCHAR UNIQUE NOT NULL" +
                        ",RENTED_CAR_ID INT" +
                        ",CONSTRAINT fk_rented_car FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID)" +
                        ")");
        stmt.close();
    }

    public Connection getConnection() {
        return connection;
    }
}
