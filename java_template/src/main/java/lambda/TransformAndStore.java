package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import saaf.Inspector;

import java.io.FileInputStream;
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
        Inspector inspector = new Inspector();
        inspector.inspectAll();
        
        //****************START FUNCTION IMPLEMENTATION*************************


        //Add custom key/value attribute to SAAF's output. (OPTIONAL)
        inspector.addAttribute("message", "Hello " + request.get("Region")
                + "! This is a custom attribute added as output from SAAF!");
        
        //Create and populate a separate response object for function output. (OPTIONAL)
        lambda.Response response = (lambda.Response) new lambda.Response();
        inspector.consumeResponse(response);
        String tableName = "tableName";     //ToDo specify table name
        String bucketName = "bucketName";   //ToDo specify bucket name
        String filePath = "filePath";       //ToDo specify file path
        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream("db.properties"));
            DatabaseManager databaseManager = new DatabaseManager(properties, tableName);
            databaseManager.insertTable(request);
            ResultSet resultSet = databaseManager.getTableData(tableName);
            StorageManager strageManager = new StorageManager(bucketName, filePath);
            response.setValue((String) request.get("Region"));


        }
        catch (Exception e)
        {
            logger.log("Got an exception working with MySQL! ");
            logger.log(e.getMessage());
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
