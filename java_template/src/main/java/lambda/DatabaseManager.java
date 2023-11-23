package lambda;

import java.sql.*;
import java.util.HashMap;
import java.util.Properties;

public class DatabaseManager {

    private Connection connection;
    private final String tableName;
    private static HashMap<String, String> S3TableMap;  //ToDo list table name in S3

    public DatabaseManager(Properties properties, String tableName) {
        this.tableName = tableName;
        try{
            this.connection = DriverManager.getConnection( properties.getProperty("url")
                                                          ,properties.getProperty("username")
                                                          ,properties.getProperty("password"));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void insertTable(HashMap<String, Object> jsonData) throws SQLException {
        String tableName;
        String insertQuery = "insert into " + tableName +
                " (Region, Country, Item_Type, Sales_Channel, Order_Priority, Order_Date, Order_ID, Ship_Date, Units_Sold, Unit_Price, Unit_Cost, Total_Revenue, Total_Cost, Total_Profit) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        // プレースホルダに値をセット
        insertStatement.setString(1, (String) jsonData.get("Region"));
        insertStatement.setString(2, (String) jsonData.get("Country"));
        insertStatement.setString(3, (String) jsonData.get("Item_Type"));
        insertStatement.setString(4, (String) jsonData.get("Sales_Channel"));
        insertStatement.setString(5, (String) jsonData.get("Order_Priority"));
        insertStatement.setString(6, (String) jsonData.get("Order_Date"));
        insertStatement.setLong(7, Long.parseLong((String) jsonData.get("Order_ID")));
        insertStatement.setString(8, (String) jsonData.get("Ship_Date"));
        insertStatement.setLong(9, Long.parseLong((String) jsonData.get("Units_Sold")));
        insertStatement.setDouble(10, Double.parseDouble((String) jsonData.get("Unit_Price")));
        insertStatement.setDouble(11, Double.parseDouble((String) jsonData.get("Unit_Cost")));
        insertStatement.setDouble(12, Double.parseDouble((String) jsonData.get("Total_Revenue")));
        insertStatement.setDouble(13, Double.parseDouble((String) jsonData.get("Total_Cost")));
        insertStatement.setDouble(14, Double.parseDouble((String) jsonData.get("Total_Profit")));

        //execute
        insertStatement.executeUpdate();
    }
    public void updateS3(){

    }
    public ResultSet getTableData(String tableName) throws SQLException {
        String selectQuery = "select * from " + tableName + ";";
        PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
        return selectStatement.executeQuery();
    }
}