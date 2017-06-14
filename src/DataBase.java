import org.json.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;

/**
 * Created by kamil on 27.05.17.
 */

public class DataBase {
    private String nameBase;
    private String nameUser;
    private String passwordUser;
    private Connection connection = null;
    private static DataBase instance = null;

    protected DataBase() {}

    public static DataBase getInstance() {
        if(instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void init(JSONObject open) throws ClassNotFoundException, JSONException {
        nameBase = open.getJSONObject("open").getString("baza");
        nameUser = open.getJSONObject("open").getString("login");
        passwordUser = open.getJSONObject("open").getString("password");
        Class.forName("org.postgresql.Driver");
    }

    public void connect() throws SQLException, IOException {
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/" + nameBase, nameUser, passwordUser);
        connection.setAutoCommit(false);
        resetDatabase();
    }

    public void close() throws SQLException {
        connection.close();
    }

    void resetDatabase() throws SQLException, IOException {
        String s = new String();
        StringBuffer sb = new StringBuffer();
        FileReader fr = new FileReader(new File("database.sql"));
        BufferedReader br = new BufferedReader(fr);

        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
        br.close();
        Statement statement = connection.createStatement();
        statement.executeUpdate(String.valueOf(sb));
    }


}