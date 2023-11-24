package lambda;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.HashMap;

public class TestMain{
    public static void main(String[] args){
        System.out.println("hello java");
        HashMap<String, Object> jsonData = new HashMap<>();
        jsonData.put("Region", "Australia and Oceania");
        jsonData.put("Country", "Tuvalu");
        jsonData.put("Item Type", "Baby Food");
        jsonData.put("Sales Channel", "Offline");
        jsonData.put("Order Priority", "H");
        jsonData.put("Order Date", "5/28/2010");
        jsonData.put("Order ID", "669165933");
        jsonData.put("Ship Date", "6/27/2010");
        jsonData.put("Units Sold", "9925");
        jsonData.put("Unit Price", "255.28");
        jsonData.put("Unit Cost", "159.42");
        jsonData.put("Total Revenue", "2533654.00");
        jsonData.put("Total Cost", "1582243.50");
        jsonData.put("Total Profit", "951410.50");
        TransformAndStore transformAndStore = new TransformAndStore();
        com.amazonaws.services.lambda.runtime.Context context = new Context() {
            @Override
            public String getAwsRequestId() {
                return null;
            }

            @Override
            public String getLogGroupName() {
                return null;
            }

            @Override
            public String getLogStreamName() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }

            @Override
            public String getFunctionVersion() {
                return null;
            }

            @Override
            public String getInvokedFunctionArn() {
                return null;
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return null;
            }
        };
        transformAndStore.handleRequest(jsonData,context);
    }
}