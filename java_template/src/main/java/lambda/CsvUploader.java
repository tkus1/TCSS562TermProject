package lambda;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CsvUploader {

    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String filePath;

    public CsvUploader(String bucketName, String filePath) {
        this.s3Client = AmazonS3ClientBuilder.defaultClient();
        this.bucketName = bucketName;
        this.filePath = filePath;
    }

    public void uploadResultSetToS3(ResultSet resultSet) {
        try {
            // CSVデータの作成
            String csvData = resultSetToCsv(resultSet);

            // S3にアップロード
            uploadToS3(csvData);

            System.out.println("Successfully uploaded CSV data to S3.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String resultSetToCsv(ResultSet resultSet) throws SQLException {
        // バッファ用のStringBuilderを作成
        StringBuilder resultStringBuilder = new StringBuilder();

        // ResultSetからデータを取得し、StringBuilderに追加
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // ヘッダー行を作成
        for (int i = 1; i <= columnCount; i++) {
            resultStringBuilder.append(metaData.getColumnName(i));
            if (i < columnCount) {
                resultStringBuilder.append(",");
            }
        }
        resultStringBuilder.append("\n");

        // データ行を作成
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                resultStringBuilder.append(resultSet.getString(i));
                if (i < columnCount) {
                    resultStringBuilder.append(",");
                }
            }
            resultStringBuilder.append("\n");
        }

        return resultStringBuilder.toString();
    }

    private void uploadToS3(String csvData) {
        byte[] byteArray = csvData.getBytes(StandardCharsets.UTF_8);
        try (InputStream inputStream = new ByteArrayInputStream(byteArray)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(byteArray.length);
            metadata.setContentType("text/csv");

            s3Client.putObject(new PutObjectRequest(bucketName, filePath, inputStream, metadata));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
