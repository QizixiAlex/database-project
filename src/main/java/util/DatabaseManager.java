package util;

import java.math.BigDecimal;
import java.sql.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class DatabaseManager {
    MysqlDataSource dataSource = null;
    Connection connection = null;
    Statement statement = null;

    public DatabaseManager(String servername, String username, String password) {
        this.dataSource = new MysqlDataSource();
        this.dataSource.setUser(username);
        this.dataSource.setPassword(password);
        this.dataSource.setServerName(servername);
    }


    public Connection getConnection() {
        try {
            if (this.connection == null) {
                this.connection = this.dataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.connection;
    }


    public Statement getStatement() {
        try {
            if (this.statement == null) {
                Connection conn = this.getConnection();
                this.statement = conn.createStatement();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this.statement;
    }



    public PreparedStatement preparedStatement(String sql){
        PreparedStatement result = null;
        try {
            result = this.getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void close() {
        if (this.getStatement() != null) {
            try {
                this.getStatement().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (this.getConnection() != null){
            try {
                this.getConnection().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public ResultSet executeQuery(String queryStr) {
        ResultSet result = null;
        Statement st = this.getStatement();
        try {
            result = st.executeQuery(queryStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean execute(String queryStr) {
        boolean result = false;
        Statement st = this.getStatement();
        try {
            result = st.execute(queryStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, String> getNameTypesOfTable(String tableName) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            String selectSQL = "SELECT * FROM " + tableName + " WHERE 1=2";
            ResultSet resultSet = this.getStatement().executeQuery(selectSQL);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                result.put(metaData.getColumnName(i), metaData.getColumnClassName(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



    public void insertDataIntoTable(String tableName, List<List<String>> data) throws SQLException{
        Map<String, String> nameTypesOfTable = getNameTypesOfTable(tableName);

        String namesOfTable = "";
        int countOfValues = nameTypesOfTable.size();

        List<String> typesOfTable = new ArrayList<>();

        for (Map.Entry<String, String> entry:
                nameTypesOfTable.entrySet()) {
            namesOfTable += entry.getKey() + ",";
            typesOfTable.add(entry.getValue());
        }

        namesOfTable = namesOfTable.substring(0, namesOfTable.length()-1);

        String questionMarks = "";
        for (int i = 0; i < countOfValues; i++) {
            questionMarks += "?,";
        }
        questionMarks = questionMarks.substring(0, questionMarks.length()-1);

        String insertSQL = "INSERT IGNORE INTO " + tableName + " (" + namesOfTable + ")" + " VALUES " + "(" + questionMarks + ")";
        PreparedStatement ps = this.preparedStatement(insertSQL);

        final int batchSize = 1000;
        int count = 0;
        List<String> first_row = data.remove(0);
        for (List<String> row:
             data) {
            assert row.size() == typesOfTable.size();

            for (int i = 0; i < row.size(); i++) {
                String cellContent = row.get(i);
                if (cellContent.equals("null")) {
                    ps.setObject(i+1, null);
                    continue;
                }
                switch (typesOfTable.get(i)) {
                    case "java.lang.String":
                        ps.setString(i+1, cellContent);
                        break;
                    case "java.lang.Integer":
                        ps.setInt(i+1, Integer.parseInt(cellContent));
                        break;
                    case "java.math.BigDecimal":
                        ps.setBigDecimal(i+1, new BigDecimal(cellContent));
                        break;
                    case "java.sql.Timestamp":
                        Format oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Format newFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = (Date)oldFormatter.parseObject(cellContent);
                            String formattedTime = newFormatter.format(date);
                            ps.setTimestamp(i+1, Timestamp.valueOf(formattedTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        ps.setObject(i+1, null);
                        break;
                }

            }
            ps.addBatch();

            count++;
            if (count % batchSize == 0) {
                try{
                    ps.executeBatch();
                } catch (BatchUpdateException e) {
                    e.printStackTrace();
                }
            }
        }

        try{
            ps.executeBatch();
        } catch (BatchUpdateException e) {
            e.printStackTrace();
        }
        ps.close();
    }




}
