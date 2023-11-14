package carsharing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarDao {
    private final Database db;

    CarDao(Database db) {
        this.db = db;
    }

    public List<Car> listAvailable(Company company) {
        var list = new ArrayList<Car>();
        try {
            try (var stmt = db.getConnection().prepareStatement("SELECT * FROM CAR WHERE COMPANY_ID = ? AND NOT EXISTS(SELECT * FROM CUSTOMER WHERE CAR.ID = CUSTOMER.RENTED_CAR_ID)")) {
                stmt.setInt(1, company.id());
                stmt.execute();
                System.out.println("Cars for " + company.id());
                while (true) {
                    var result = stmt.getResultSet();
                    while (result.next()) {
                        list.add(new Car(result.getInt("ID"), result.getString("NAME"), result.getInt("COMPANY_ID")));
                    }
                    if (!stmt.getMoreResults()) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Something's wrong: " + e.getMessage());
        }
        return list;
    }

    public List<Car> list(Company company) {
        var list = new ArrayList<Car>();
        try {
            try (var stmt = db.getConnection().prepareStatement("SELECT * FROM CAR WHERE COMPANY_ID = ?")) {
                stmt.setInt(1, company.id());
                stmt.execute();
                System.out.println("Cars for " + company.id());
                while (true) {
                    var result = stmt.getResultSet();
                    while (result.next()) {
                        list.add(new Car(result.getInt("ID"), result.getString("NAME"), result.getInt("COMPANY_ID")));
                    }
                    if (!stmt.getMoreResults()) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Something's wrong: " + e.getMessage());
        }
        return list;
    }

    public Car add(String name, Company company) {
        Car added = null;
        try {
            ResultSet keys;
            try (var stmt = db.getConnection().prepareStatement("INSERT INTO CAR (NAME, COMPANY_ID) VALUES ( ?, ? )", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.setInt(2, company.id());
                var result = stmt.execute();
                keys = stmt.getGeneratedKeys();
                keys.first();
                added = new Car(keys.getInt(1), name, company.id());
            }
        } catch (Exception e) {
            System.out.println("Something's wrong again: " + e.getMessage());
        }
        return added;
    }

    public Car get(int id) {
        try {
            ResultSet result;
            try (var stmt = db.getConnection().prepareStatement("SELECT * FROM CAR WHERE ID = ?")) {
                stmt.setInt(1, id);
                result = stmt.executeQuery();
                if (!result.next()) {
                    return null;
                }
                return new Car(id, result.getString(2), result.getInt(3));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
