package lambda;

import java.sql.*;
import java.util.HashMap;
import java.util.Properties;

public class DatabaseManager {

    private final String url;
    private final String username;
    private final String driver;
    private final String tableName;
    private final String password;
    private Connection connection;
    private static HashMap<String, String> S3TableMap;  //ToDo list table name in S3

    public DatabaseManager(Properties properties, String tableName) {

        this.url = properties.getProperty("url");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
        this.driver = properties.getProperty("driver");
        this.tableName = tableName;  //ToDo specify target table name
        try{
            this.connection = DriverManager.getConnection(url,username,password);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void insertTable(HashMap<String, Object> jsonData) throws SQLException {
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
        ResultSet resultSet = selectStatement.executeQuery();
        return  resultSet;
    }
}