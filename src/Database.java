import java.sql.*;

public class Database {
    private Connection connection;
    private Statement statement;

    // inisialisasi koneksi ke database mariadb/mysql lokal
    public Database() {
        try {
            // konfigurasi akses database
            String url = "jdbc:mysql://localhost:3306/db_people";
            String user = "sum8ul";
            String password = "password123";

            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // mengeksekusi query select untuk mengambil data dari tabel
    public ResultSet selectQuery(String sql) {
        try {
            statement.executeQuery(sql);
            return statement.getResultSet();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // mengeksekusi query insert, update, atau delete
    public int insertUpdateDeleteQuery(String sql) {
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Statement getStatement() {
        return statement;
    }
}