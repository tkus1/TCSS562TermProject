package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import saaf.Inspector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * uwt.lambda_test::handleRequest
 *
 * @author Wes Lloyd
 * @author Robert Cordingly
 */
public class TransformAndStore implements RequestHandler<List<HashMap<String, Object>>, HashMap<String, Object>> {

    /**
     * Lambda Function Handler
     * 
     * @param request Hashmap containing request JSON attributes.
     * @param context 
     * @return HashMap that Lambda will automatically convert into JSON.
     */
    public HashMap<String, Object> handleRequest(List<HashMap<String, Object>> requestList, Context context) {
        // Create logger
        LambdaLogger logger = context.getLogger();
        //Collect initial data.

        Inspector inspector = new Inspector();
        inspector.inspectAll();
        //****************START FUNCTION IMPLEMENTATION*************************

        inspector.addAttribute("message", "Hello ! This is a custom attribute added as output from SAAF!");
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response response = new Response();
        //inspector.consumeResponse(response);
        String tableName = "salesRecordTab";
        String bucketName = "term-project-tcss562.team7";
        String filePath = "salesRecords.csv";

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
            DatabaseManager databaseManager = new DatabaseManager(properties);
            for (HashMap<String, Object> request :requestList){
                System.out.println(request.get("Region"));

                databaseManager.insertTable(request, tableName);
            }


            ResultSet resultSet = databaseManager.getTableData(tableName);
            System.out.println(resultSet.toString());
            CsvUploader csvUploader = new CsvUploader(bucketName, filePath);
            csvUploader.uploadResultSetToS3(resultSet);

            //response.setValue((String) request.get("Region"));

        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            logger.log("Got an exception working with MySQL! ");
            logger.log(e.getMessage());
        }

        //Print log information to the Lambda log as needed
        logger.log("log message...");
        //inspector.consumeResponse(response);


        //****************END FUNCTION IMPLEMENTATION***************************
                
        //Collect final information such as total runtime and cpu deltas.
        inspector.inspectAllDeltas();
        return inspector.finish();
    }
}
