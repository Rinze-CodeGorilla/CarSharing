package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {
    private final Database db;

    CustomerDao(Database db) {
        this.db = db;
    }

    public List<Customer> list() {
        var list = new ArrayList<Customer>();
        try {
            try (var stmt = db.getConnection().prepareStatement("SELECT * FROM CUSTOMER")) {
                stmt.execute();
                while (true) {
                    var result = stmt.getResultSet();
                    while (result.next()) {
                        list.add(new Customer(result.getInt("ID"), result.getString("NAME"), result.getObject("RENTED_CAR_ID", Integer.class)));
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

    public Customer add(String name) {
        Customer added = null;
        try {
            ResultSet keys;
            try (var stmt = db.getConnection().prepareStatement("INSERT INTO CUSTOMER (NAME) VALUES ( ? )", Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                var result = stmt.execute();
                keys = stmt.getGeneratedKeys();
                keys.first();
                added = new Customer(keys.getInt(1), name, null);
            }
        } catch (Exception e) {
            System.out.println("Something's wrong again: " + e.getMessage());
        }
        return added;
    }

    public Customer setCar(Customer customer, Car car) {
        PreparedStatement stmt = null;
        Integer carId = car == null ? null : car.id();
        try {
            stmt = db.getConnection().prepareStatement("UPDATE CUSTOMER SET RENTED_CAR_ID = ? WHERE ID = ?");
                stmt.setObject(1, carId);
            stmt.setInt(2, customer.id());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new Customer(customer.id(), customer.name(), carId);
    }

}
