package com.pm10project;

import java.sql.*;
import java.util.Vector;

@SuppressWarnings("FieldCanBeLocal")
class DbManager {

    static String TABLE1 = "POLLUTION.ACTUAL";
    static String TABLE2 = "POLLUTION.HISTORICAL";

    private static String foldername = "pollution_data_DB";
    private static String dbname = "POLLUTION_DATA";
    private static String username = "app_controller";
    private static String password = "p0llut10n";

    // load the db drivers
    private static boolean loadDatabaseDriver() {
        String driverName = "org.apache.derby.jdbc.EmbeddedDriver";

        try {
            Class.forName(driverName);
            return true;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // set the db home directory
    private static void setDBSystemDir() {
        String userHomeDir = System.getProperty("user.home", ".");
        String systemDir = userHomeDir + "/." + foldername;

        // Set the db system directory.
        System.setProperty("derby.system.home", systemDir);
    }

    // create the tb tables for data saving
    private static void createTables(Connection dbConnection) {
        String tab1 = "CREATE table " + TABLE1 + " (" +
                "ID BIGINT PRIMARY KEY," +
                "LONGITUDE FLOAT NOT NULL," +
                "LATITUDE FLOAT NOT NULL," +
                "PM1 FLOAT NOT NULL," +
                "PM2 FLOAT NOT NULL," +
                "TIMESTAMP BIGINT NOT NULL" +
                ")";

        String tab2 = "CREATE table " + TABLE2 + " (" +
                "ID BIGINT NOT NULL," +
                "LONGITUDE FLOAT NOT NULL," +
                "LATITUDE FLOAT NOT NULL," +
                "PM1 FLOAT NOT NULL," +
                "PM2 FLOAT NOT NULL," +
                "TIMESTAMP BIGINT NOT NULL," +
                "PRIMARY KEY (ID, TIMESTAMP)" +
                ")";

        try {
            Statement statement = dbConnection.createStatement();
            statement.execute(tab1);
            statement.execute(tab2);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // crete a new db if not exists
    static void createDb() {
        if(loadDatabaseDriver()) {
            setDBSystemDir();

            String strUrlCreate = String.format("jdbc:derby:%s;user=%s;password=%s;create=true",
                    dbname, username, password);

            try {
                Connection dbConnection = DriverManager.getConnection(strUrlCreate);

                // create tables if not exist
                createTables(dbConnection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // create a valid connection to pollution data db
    static Connection dbConnect() {
        // all action to get a valid connection
        Connection dbConnection = null;

        if(loadDatabaseDriver()) {
            setDBSystemDir();

            String strUrlConnect = String.format("jdbc:derby:%s;user=%s;password=%s", dbname, username, password);

            try {
                dbConnection = DriverManager.getConnection(strUrlConnect);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return dbConnection;
    }

    // execute a select query on a given table
    static Vector<Pm10Data> select(Connection dbConnection, String table, String query) {
        String formattedQuery = String.format(query, table);
        Vector<Pm10Data> allData = new Vector<>();

        try {
            Statement statement = dbConnection.createStatement();
            ResultSet rset = statement.executeQuery(formattedQuery);

            while(rset.next()) {   // Move the cursor to the next row, return false if no more row
                Pm10Data row = new Pm10Data(
                        rset.getLong("ID"),
                        rset.getDouble("LATITUDE"),
                        rset.getDouble("LONGITUDE"),
                        rset.getDouble("PM1"),
                        rset.getDouble("PM2"),
                        rset.getLong("TIMESTAMP")
                );

                allData.add(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return allData;
    }

    // execute an insert, update or delete query on a given table
    static int query(Connection dbConnection, String table, String query) {
        String formattedQuery = String.format(query, table);

        try {
            Statement statement = dbConnection.createStatement();
            statement.execute(formattedQuery);
            return Result.OK;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Result.WRITING_ERROR;
        }
    }
}
