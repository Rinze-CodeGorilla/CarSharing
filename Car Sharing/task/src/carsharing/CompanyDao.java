package carsharing;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CompanyDao {
    private final Database db;
    CompanyDao(Database db) {
        this.db = db;
    }

    public List<Company> list() {
        var list = new ArrayList<Company>();
        try {
            var stmt = db.getConnection().createStatement();
            var result = stmt.executeQuery("SELECT * FROM COMPANY");
            while (result.next()) {
                list.add(new Company(result.getInt("ID"), result.getString("NAME")));
            }
        } catch(Exception e) {
            System.out.println("Something's wrong: " + e.getMessage());
        }
        return list;
    }

    public Company add(String name) {
        Company added = null;
        try {
            var stmt = db.getConnection().prepareStatement("INSERT INTO COMPANY (NAME) VALUES ( ? )", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            var result = stmt.execute();
            var keys = stmt.getGeneratedKeys();
            keys.first();
            added = new Company(keys.getInt(1), name);
        } catch (Exception e) {
            System.out.println("Something's wrong again: " + e.getMessage());
        }
        return added;
    }

    public Company get(int i) {
        try (var stmt = db.getConnection().prepareStatement("SELECT * FROM COMPANY WHERE ID = ?")) {
            stmt.setInt(1, i);
            var result = stmt.executeQuery();
            result.next();
            return new Company(result.getInt("id"), result.getString("name"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
