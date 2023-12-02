package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import saaf.Inspector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class DataLoader implements RequestHandler<HashMap<String, Object>, HashMap<String, Object>> {
    @Override
    public HashMap<String, Object> handleRequest(HashMap<String, Object> request, Context context) {
        // Create logger
        LambdaLogger logger = context.getLogger();
        //Collect initial data.

        Inspector inspector = new Inspector();
        inspector.inspectAll();
        inspector.addAttribute("message", "Hello " + request.get("Region")
                + "! This is a custom attribute added as output from SAAF!");
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response response = new Response();
        //response.setValue("Region is " + request.get("Region")); // todo set proper key to get method
        //****************START FUNCTION IMPLEMENTATION*************************

        // AWS S3 configuration
        String s3BucketName = (String) request.get("bucketName");
        String s3FileName = (String) request.get("filePath");
        System.out.println("bucketName = " + s3BucketName);
        System.out.println("fileName = " + s3FileName);
        // AWS RDS configuration
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("unable to find db.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatabaseManager databaseManager = new DatabaseManager(properties);

        CsvReader csvReader = new CsvReader();
        String csvData = csvReader.readCsvData(s3BucketName,s3FileName);

        String tableName = "salesRecordTab";
        try {
            databaseManager.updateTableFromCsv(csvData,tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //return inspected information
        inspector.inspectAllDeltas();
        return inspector.finish();
    }
}

