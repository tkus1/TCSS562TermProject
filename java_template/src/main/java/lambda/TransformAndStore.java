package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import saaf.Inspector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

/**
 * uwt.lambda_test::handleRequest
 *
 * @author Wes Lloyd
 * @author Robert Cordingly
 */
public class TransformAndStore implements RequestHandler<HashMap<String, Object>, HashMap<String, Object>> {

    /**
     * Lambda Function Handler
     * 
     * @param request Hashmap containing request JSON attributes.
     * @param context 
     * @return HashMap that Lambda will automatically convert into JSON.
     */
    public HashMap<String, Object> handleRequest(HashMap<String, Object> request, Context context) {
        // Create logger
        LambdaLogger logger = context.getLogger();
        //Collect initial data.
        System.out.println("Logger new done");
        Inspector inspector = new Inspector();
        System.out.println("Inspector new done");
        inspector.inspectAll();
        System.out.println("done inspectAll");
        //****************START FUNCTION IMPLEMENTATION*************************


        //Add custom key/value attribute to SAAF's output. (OPTIONAL)
        ;
        inspector.addAttribute("message", "Hello " + request.get("Region")
                + "! This is a custom attribute added as output from SAAF!");
        System.out.println("message hello done");
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response response = new Response();
        System.out.println("response new done");
        response.setValue("Region is " + request.get("Region"));
        System.out.println("response.setValue done");
        //inspector.consumeResponse(response);
        System.out.println("consumeResponse done");
        String tableName = "salesRecordTab";     //ToDo specify table name
        String bucketName = "bucketNameS3";   //ToDo specify bucket name
        String filePath = "filePathS3";       //ToDo specify file path
        System.out.println("set String variables");
        System.out.println(request.get("Region"));
        System.out.println("underscore" + request.get("Item_Type"));
        System.out.println("blank" + request.get("Item Type"));
        try
        {
            Properties properties = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
                if (input == null) {
                    System.out.println("unable to find db.properties");
                }
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("break1");
            DatabaseManager databaseManager = new DatabaseManager(properties, tableName);
            System.out.println("Inserting table...");
            databaseManager.insertTable(request);
            System.out.println("break3");
            ResultSet resultSet = databaseManager.getTableData(tableName);
            StorageManager storageManager = new StorageManager(bucketName, filePath);
            response.setValue((String) request.get("Region"));
            System.out.println("break4");

        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        //    logger.log("Got an exception working with MySQL! ");
        //    logger.log(e.getMessage());
        }

        //Print log information to the Lambda log as needed
        //logger.log("log message...");
        inspector.consumeResponse(response);
        //****************END FUNCTION IMPLEMENTATION***************************
                
        //Collect final information such as total runtime and cpu deltas.
        inspector.inspectAllDeltas();
        return inspector.finish();
    }
}
