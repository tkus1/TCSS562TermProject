package lambda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class SaveToRDS {
    private static final String DB_URL = "jdbc:mysql://your-rds-endpoint:3306/your-database";
    private static final String DB_USER = "your-username";
    private static final String DB_PASSWORD = "your-password";

    // テーブル名
    private static final String TABLE_NAME = "your-table";

    // データ保存メソッド
    public void saveDataToRDS(HashMap<String, Object> jsonData) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // テーブルにデータを挿入するSQL文
            String insertSql = "INSERT INTO " + TABLE_NAME + " (json_column) VALUES (?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                // JSONデータを文字列に変換
                String jsonString = convertToJsonString(jsonData);

                // パラメータを設定
                preparedStatement.setString(1, jsonString);

                // SQL実行
                preparedStatement.executeUpdate();

                System.out.println("Data saved to RDS successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String convertToJsonString(HashMap<String, Object> jsonData) {
        // ここでJSON文字列に変換する方法を実装（例: Jacksonライブラリを使用）
        // この例では単純な文字列に変換していますが、実際のデータ構造に応じて適切な変換を行ってください。
        return "{ \"key1\": \"value1\", \"key2\": \"value2\" }";
    }
}
