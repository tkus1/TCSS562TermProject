package lambda;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;

public class DatabaseManager {

    private Connection connection;
    private final String tableName;


    public DatabaseManager(Properties properties, String tableName) {
        System.out.println("DatabaseManager constructor invoked");
        this.tableName = tableName;
        System.out.println("set tableName");
        System.out.println(properties.getProperty("url") + properties.getProperty("username") + properties.getProperty("password"));
        try{
            this.connection = DriverManager.getConnection( properties.getProperty("url")
                                                          ,properties.getProperty("username")
                                                          ,properties.getProperty("password"));
            System.out.println("connection created");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void insertTable(HashMap<String, Object> jsonData) throws SQLException {
        //set order processing time
        java.sql.Date orderDate = convertStringToSqlDate((String) jsonData.get("Order Date"));
        java.sql.Date shipDate = convertStringToSqlDate((String) jsonData.get("Ship Date"));
        //java.sql.Date orderDate = java.sql.Date.valueOf((String) jsonData.get("Order Date"));
        //java.sql.Date shipDate = java.sql.Date.valueOf((String) jsonData.get("Ship Date"));
        long differenceInMillis = shipDate.getTime() - orderDate.getTime();
        long differenceInDays = differenceInMillis / (24 * 60 * 60 * 1000);
        int orderProcessingTime =(int) differenceInDays;
        //Transform order_priority
        String orderPriority;
        switch ((String)jsonData.get("Order Priority")){
            case "H":
                orderPriority = "High";
                break;
            case "M":
                orderPriority = "Medium";
                break;
            case "L":
                orderPriority = "Low";
                break;
            case "C":
                orderPriority = "Critical";
                break;
            default:
                orderPriority = "Invalid";
        }
        //calculate gross margin from total profit and total revenue;
        Float totalRevenue = Float.parseFloat((String) jsonData.get("Total Revenue"));
        Float totalProfit  = Float.parseFloat((String) jsonData.get("Total Profit"));
        Float grossMargin  = totalRevenue/totalProfit;

        String insertQuery = "insert into " + tableName +
                " (Region, Country, Item_Type, Sales_Channel, Order_Priority, Order_Date, Order_ID, Ship_Date, Order_Processing_Time, Units_Sold, Unit_Price, Unit_Cost, Total_Revenue, Total_Cost, Total_Profit, GROSS_Margin) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?);";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        // プレースホルダに値をセット
        insertStatement.setString(1, (String) jsonData.get("Region"));
        insertStatement.setString(2, (String) jsonData.get("Country"));
        insertStatement.setString(3, (String) jsonData.get("Item Type"));
        insertStatement.setString(4, (String) jsonData.get("Sales Channel"));
        insertStatement.setString(5, orderPriority);
        insertStatement.setDate  (6, orderDate);
        insertStatement.setInt   (7, Integer.parseInt((String) jsonData.get("Order ID")));
        insertStatement.setDate  (8, shipDate);
        insertStatement.setInt   (9, orderProcessingTime);
        insertStatement.setInt   (10, Integer.parseInt((String) jsonData.get("Units Sold")));
        insertStatement.setFloat (11, Float.parseFloat((String) jsonData.get("Unit Price")));
        insertStatement.setFloat (12, Float.parseFloat((String) jsonData.get("Unit Cost")));
        insertStatement.setFloat (13, totalRevenue);
        insertStatement.setFloat (14, Float.parseFloat((String) jsonData.get("Total Cost")));
        insertStatement.setFloat (15, totalProfit);
        insertStatement.setFloat (16, grossMargin);
        //execute
        insertStatement.executeUpdate();
    }
    public ResultSet getTableData(String tableName) throws SQLException {

        String selectQuery = "select * from " + tableName + ";";
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
        return selectStatement.executeQuery();
    }
    private java.sql.Date convertStringToSqlDate(String dateString) {
        try {
            // フォーマットを指定して SimpleDateFormat インスタンスを作成
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            // 文字列を Date オブジェクトに変換
            java.util.Date utilDate =  dateFormat.parse(dateString);

            // utilDate から java.sql.Date に変換
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            // もし例外が発生した場合は、適切にハンドリングしてください。
            return null;
        }
    }
}