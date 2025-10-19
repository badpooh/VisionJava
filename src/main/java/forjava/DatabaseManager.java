package forjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:settings.db";

    // 애플리케이션 시작 시 한 번만 호출하여 테이블이 준비되도록 합니다.
    public static void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS settings ("
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " type TEXT NOT NULL UNIQUE,"
                    + " value TEXT NOT NULL"
                    + ");";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Database and table have been initialized.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveOrUpdateSetting(String key, String value) {
        // 'key'가 이미 존재하면 value를 갱신하고, 없으면 새로 추가하는 SQL (UPSERT)
        String sql = "INSERT INTO settings (type, value) VALUES (?, ?)"
                    + " ON CONFLICT(type) DO UPDATE SET value = excluded.value";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String loadSetting(String key) {
        String sql = "SELECT value FROM settings WHERE type = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("value");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}