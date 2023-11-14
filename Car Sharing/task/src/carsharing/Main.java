package carsharing;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // write your code here
        String dbName = "mijndb";
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-databaseFileName")) {
                dbName = args[i + 1];
            }
        }
        var db = new Database(dbName);
        var cd = new CompanyDao(db);
        var carDao = new CarDao(db);
        var customerDao = new CustomerDao(db);
        var m = new Menu(cd, carDao, customerDao);
        m.run();
    }
}
