package system;

import java.sql.*;

public class config {
    
    // Connection Method to SQLite
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC"); // Load the SQLite JDBC driver
            con = DriverManager.getConnection("jdbc:sqlite:axci.db"); // Establish connection
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
        return con;
    }

    //-----------------------------------------------
    // ADD, UPDATE, DELETE Records Methods
    //-----------------------------------------------
    public void addRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, values);
            pstmt.executeUpdate();
            System.out.println("Record added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }
    
    public void updateRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, values);
            pstmt.executeUpdate();
            System.out.println("Record updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }

    public void deleteRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, values);
            pstmt.executeUpdate();
            System.out.println("Record deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    //-----------------------------------------------
    // VIEW RECORDS METHODS (WITHOUT AND WITH PARAMS)
    //-----------------------------------------------

    // Method to view records without parameters
    public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

            // Print headers
            printTableHeaders(columnHeaders);

            // Print rows
            while (rs.next()) {
                printTableRow(rs, columnNames);
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    // Method to view records with parameters (parameterized queries)
    public void viewRecordsWithParams(String sqlQuery, String[] columnHeaders, String[] columnNames, Object... params) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            // Set dynamic parameters
            setPreparedStatementValues(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Print headers
                printTableHeaders(columnHeaders);

                // Print rows
                while (rs.next()) {
                    printTableRow(rs, columnNames);
                }
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving records with params: " + e.getMessage());
        }
    }

    //-----------------------------------------------
    // Helper Methods for Table Formatting
    //-----------------------------------------------
    private void printTableHeaders(String[] columnHeaders) {
        StringBuilder headerLine = new StringBuilder("---------------------------------------------------------------------------------------------------------------------------------------------------------------\n| ");
        for (String header : columnHeaders) {
            headerLine.append(String.format("%-20s | ", header));
        }
        headerLine.append("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(headerLine);
    }

    private void printTableRow(ResultSet rs, String[] columnNames) throws SQLException {
        StringBuilder row = new StringBuilder("| ");
        for (String colName : columnNames) {
            String value = rs.getString(colName);
            row.append(String.format("%-20s | ", value != null ? value : ""));
        }
        System.out.println(row);
    }

    //-----------------------------------------------
    // Helper Method for Setting PreparedStatement Values
    //-----------------------------------------------
    private void setPreparedStatementValues(PreparedStatement pstmt, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) values[i]);
            } else if (values[i] instanceof Double) {
                pstmt.setDouble(i + 1, (Double) values[i]);
            } else if (values[i] instanceof Float) {
                pstmt.setFloat(i + 1, (Float) values[i]);
            } else if (values[i] instanceof Long) {
                pstmt.setLong(i + 1, (Long) values[i]);
            } else if (values[i] instanceof Boolean) {
                pstmt.setBoolean(i + 1, (Boolean) values[i]);
            } else if (values[i] instanceof java.util.Date) {
                pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) values[i]).getTime()));
            } else if (values[i] instanceof java.sql.Date) {
                pstmt.setDate(i + 1, (java.sql.Date) values[i]);
            } else if (values[i] instanceof java.sql.Timestamp) {
                pstmt.setTimestamp(i + 1, (java.sql.Timestamp) values[i]);
            } else {
                pstmt.setString(i + 1, values[i].toString());
            }
        }
    }

    //-----------------------------------------------
    // GET SINGLE VALUE METHOD
    //-----------------------------------------------
    public double getSingleValue(String sql, Object... params) {
        double result = 0.0;
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, params);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                result = rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving single value: " + e.getMessage());
        }
        return result;
    }
}
