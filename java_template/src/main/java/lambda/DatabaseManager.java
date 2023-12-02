package lambda;


import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseManager {

    private Connection connection;


    public DatabaseManager(Properties properties) {
        System.out.println("DatabaseManager constructor invoked");
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
    public void insertTable(HashMap<String, Object> jsonData, String tableName) throws SQLException {
        //set order processing time
        java.sql.Date orderDate = convertStringToSqlDate((String) jsonData.get("Order Date"));
        java.sql.Date shipDate = convertStringToSqlDate((String) jsonData.get("Ship Date"));
        int orderProcessingTime;
        if(jsonData.get("Order Processing Time") == null){
            long differenceInMillis = shipDate.getTime() - orderDate.getTime();
            long differenceInDays = differenceInMillis / (24 * 60 * 60 * 1000);
            orderProcessingTime =(int) differenceInDays;
        }else{
            System.out.println(jsonData.get("Order Processing Time"));
            orderProcessingTime = Integer.valueOf((String) jsonData.get("Order Processing Time"));
        }
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
                orderPriority = (String) jsonData.get("Order Priority");
        }
        //calculate gross margin from total profit and total revenue;
        Float totalRevenue = Float.parseFloat((String) jsonData.get("Total Revenue"));
        Float totalProfit  = Float.parseFloat((String) jsonData.get("Total Profit"));
        Float grossMargin  = totalProfit/totalRevenue;

        String insertQuery = "insert ignore into " + tableName +
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
            SimpleDateFormat dateFormat;
            if (dateString.contains("/")){
                dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            }else{
                dateFormat = new SimpleDateFormat("yyyy-dd-mm");
            }
            //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            System.out.println(dateString);
            java.util.Date utilDate =  dateFormat.parse(dateString);

            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDataTableByJSON(HashMap<String, Object> jsonData, String tableName) throws SQLException{
        ResultSet selectResult = buildQuery(connection, jsonData, tableName).executeQuery();
        System.out.println("ResultSet created successfully");
        String jsonResult = convertResultSetToJSONString(selectResult);
        System.out.println("result string is:" + jsonResult);
        return jsonResult;
    }
    private PreparedStatement buildQuery(Connection connection, HashMap<String, Object> requestData, String tableName) throws SQLException {
        // get information from HashMap
        Object columnsObject = requestData.get("columns");
        List<String> columns;
        if (columnsObject instanceof String) {
            // in case the length of "columns" is 1
            columns = Collections.singletonList((String) columnsObject);
        } else if (columnsObject instanceof List) {
            // in case "columns" is a list
            columns = (List<String>) columnsObject;
        } else {
            columns = Collections.emptyList();
        }

        // create select
        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        for (String column : columns) {
            queryBuilder.append(column).append(", ");
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
        queryBuilder.append(" FROM ").append(tableName);
        System.out.println(queryBuilder.toString());

        // create where
        if(requestData.keySet().contains("filters")){

            HashMap<String, String> filters = (HashMap<String, String>) requestData.get("filters");
            queryBuilder.append(" WHERE ");
            for (HashMap.Entry<String, String> entry : filters.entrySet()) {
                queryBuilder.append(entry.getKey()).append(" = ").append('"').append(entry.getValue()).append('"').append(" AND ");
            }
            queryBuilder.delete(queryBuilder.length() - 5, queryBuilder.length());  // delete the last AND
        }

        if(requestData.keySet().contains("groupby")){

            Object groupbyObject = requestData.get("groupby");
            List<String> groupbys;
            if (groupbyObject instanceof String) {
                groupbys = Collections.singletonList((String) groupbyObject);
            } else if (groupbyObject instanceof List) {
                groupbys = (List<String>) groupbyObject;
            } else {
                groupbys = Collections.emptyList();
            }

            queryBuilder.append(" GROUP BY ");
            for (String groupby : groupbys) {
                queryBuilder.append(groupby).append(", ");
            }
            queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());  // delete the meaningless ", "
        }

        PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());
        System.out.println(queryBuilder.toString());

        return preparedStatement;
    }

    public String convertResultSetToJSONString(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            StringBuilder jsonString = new StringBuilder("[");
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    row.put(columnName, columnValue);
                }
                jsonString.append(mapToJsonString(row)).append(",");
            }

            if (jsonString.charAt(jsonString.length() - 1) == ',') {
                jsonString.deleteCharAt(jsonString.length() - 1);
            }

            jsonString.append("]");
            return jsonString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String mapToJsonString(Map<String, Object> map) {
        StringBuilder jsonString = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jsonString.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        if (jsonString.charAt(jsonString.length() - 1) == ',') {
            jsonString.deleteCharAt(jsonString.length() - 1);
        }
        jsonString.append("}");
        return jsonString.toString();
    }
    public void updateTableFromCsv(String csvData, String tableName) throws SQLException {
        // CSVデータを行ごとに分割
        String[] rows = csvData.split("\n");

        // ヘッダー行を無視してデータを処理
        HashMap<String, Object> jsonData;
        for (int i = 1; i < rows.length; i++) {
            String[] columns = rows[i].split(",");

            // データをHashMapに変換
            jsonData = new HashMap<>();
            jsonData.put("Region", columns[0].trim());
            jsonData.put("Country", columns[1].trim());
            jsonData.put("Item Type", columns[2].trim());
            jsonData.put("Sales Channel", columns[3].trim());
            jsonData.put("Order Priority", columns[4].trim());
            jsonData.put("Order Date", columns[5].trim());
            jsonData.put("Order ID", columns[6].trim());
            jsonData.put("Ship Date", columns[7].trim());
            jsonData.put("Order Processing Time", columns[8].trim());
            jsonData.put("Units Sold", columns[9].trim());
            jsonData.put("Unit Price", columns[10].trim());
            jsonData.put("Unit Cost", columns[11].trim());
            jsonData.put("Total Revenue", columns[12].trim());
            jsonData.put("Total Cost", columns[13].trim());
            jsonData.put("Total Profit", columns[14].trim());
            jsonData.put("GROSS Margin", columns[15].trim());
            // 他の列も同様に追加

            // データテーブルを書き換えるメソッドを呼び出し
            insertTable(jsonData, tableName);
        }

    }

}