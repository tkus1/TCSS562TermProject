package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import saaf.Inspector;
import saaf.Response;

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
        
        //Collect initial data.
        Inspector inspector = new Inspector();
        inspector.inspectAll();
        
        //****************START FUNCTION IMPLEMENTATION*************************


        //Add custom key/value attribute to SAAF's output. (OPTIONAL)
        inspector.addAttribute("message", "Hello " + request.get("Region")
                + "! This is a custom attribute added as output from SAAF!");
        
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response response = new Response();
        response.setValue("Hello "
                + "! This is from a response object!");
        
        inspector.consumeResponse(response);
        Response r = new Response();

        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream("db.properties"));

            String url = properties.getProperty("url");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String driver = properties.getProperty("driver");

            r.setValue(request.getName());
            // Manually loading the JDBC Driver is commented out
            // No longer required since JDBC 4
            //Class.forName(driver);
            Connection con = DriverManager.getConnection(url,username,password);

            PreparedStatement ps1 = con.prepareStatement("insert into mytable values('" + request.getName() + "','b','c');");
            ps1.execute();
            ps1 = con.prepareStatement("select * from mytable;");
            ResultSet rs1 = ps1.executeQuery();
            LinkedList<String> ll_1 = new LinkedList<String>();
            while (rs1.next())
            {
                logger.log("name=" + rs1.getString("name"));
                ll_1.add(rs1.getString("name"));
            }

            PreparedStatement ps = con.prepareStatement("select version() as version;");
            ResultSet rs = ps.executeQuery();
            LinkedList<String> ll = new LinkedList<String>();
            while (rs.next())
            {
                logger.log("version=" + rs.getString("version"));
                ll.add(rs.getString("version"));
            }
            rs.close();
            con.close();
            r.setNames(ll_1);
            r.setsqlVersion(ll);
            System.out.println(r.getsqlVersionString());
        }
        catch (Exception e)
        {
            logger.log("Got an exception working with MySQL! ");
            logger.log(e.getMessage());
        }

        //Print log information to the Lambda log as needed
        //logger.log("log message...");
        inspector.consumeResponse(r);
        //****************END FUNCTION IMPLEMENTATION***************************
                
        //Collect final information such as total runtime and cpu deltas.
        inspector.inspectAllDeltas();
        return inspector.finish();
    }
}
