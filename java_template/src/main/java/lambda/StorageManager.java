package lambda;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class StorageManager {

    // AWS S3 バケット名
    private final String bucketName;

    // ファイル保存先パス
    private final String filePath;

    // AWS S3 クライアント
    private static final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    public StorageManager(String bucketName, String filePath){
        this.bucketName = bucketName;
        this.filePath = filePath;
    }


    // ResultSetをCSVファイルとしてS3に保存するメソッド
    public void saveResultSetToS3(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // ヘッダー行を作成
            List<String> headers = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                headers.add(metaData.getColumnName(i));
            }

            // データ行を作成
            List<String> dataRows = new ArrayList<>();
            while (resultSet.next()) {
                List<String> rowData = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.add(resultSet.getString(i));
                }
                dataRows.add(String.join(",", rowData));
            }

            // ファイルに書き込み
            File csvFile = new File(filePath);
            try (FileWriter writer = new FileWriter(csvFile)) {
                // ヘッダー行の書き込み
                writer.write(String.join(",", headers));
                writer.write("\n");

                // データ行の書き込み
                for (String dataRow : dataRows) {
                    writer.write(dataRow);
                    writer.write("\n");
                }
            }

            // S3にファイルをアップロード
            s3Client.putObject(new PutObjectRequest(bucketName, filePath, csvFile));

            System.out.println("Successfully saved csv file to " + bucketName + filePath);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
