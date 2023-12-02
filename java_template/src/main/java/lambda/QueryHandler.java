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

public class QueryHandler implements RequestHandler<HashMap<String, Object>, String> {

    @Override
    public String handleRequest(HashMap<String, Object> request, Context context) {
        System.out.println("Received request: " + request);
        // Create logger
        LambdaLogger logger = context.getLogger();
        //Collect initial data.

        Inspector inspector = new Inspector();
        inspector.inspectAll();
        //****************START FUNCTION IMPLEMENTATION*************************


        //Add custom key/value attribute to SAAF's output. (OPTIONAL)
        ;
        inspector.addAttribute("message", "Hello " + request.get("Region")
                + "! This is a custom attribute added as output from SAAF!");
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response response = new Response();
        response.setValue("Region is " + request.get("Region"));

        String tableName = "salesRecordTab";
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
        try {
            String result = databaseManager.getDataTableByJSON(request,tableName);
            System.out.println("returning result");
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

