package lambda;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CsvReader {

    private final AmazonS3 s3Client;

    public CsvReader() {
        this.s3Client = AmazonS3ClientBuilder.defaultClient();
    }

    public String readCsvData(String bucketName, String filePath) {
        try {
            // Create an S3 Object Request
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filePath);

            // Get the S3 object
            S3Object s3Object = s3Client.getObject(getObjectRequest);

            // Read the content of the S3 object
            S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
            return readInputStream(objectInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String readInputStream(S3ObjectInputStream objectInputStream) throws IOException {
        StringBuilder csvData = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(objectInputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                csvData.append(line).append("\n");
            }
        }
        return csvData.toString();
    }

    public static void main(String[] args) {
        // Usage example
        CsvReader s3CsvReader = new CsvReader();
        String bucketName = "your-s3-bucket-name";
        String filePath = "your-file-path.csv";

        String csvData = s3CsvReader.readCsvData(bucketName, filePath);
        System.out.println("CSV Data: \n" + csvData);
    }
}
