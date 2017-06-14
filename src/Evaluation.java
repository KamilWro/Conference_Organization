import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by kamil on 29.05.17.
 */
public class Evaluation extends Command{
    Connection connection;
    private String login;
    private String talk;
    private String password;
    private int rating;


    public Evaluation() {
        name="evaluation";
    }

    private void init(JSONObject arg) throws JSONException {
        login=arg.getString("login");
        password=arg.getString("password");
        talk=arg.getString("talk");
        rating=arg.getInt("rating");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws SQLException, JSONException {
        connection = DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationUser(login,password);
            if (!isRowInTalk(talk,"public"))
                throw new SQLException("Talk doeas not exist");
            PreparedStatement prepStmt = connection.prepareStatement(
                    "INSERT INTO evaluation VALUES (?,?,?);");
            prepStmt.setString(1, login);
            prepStmt.setString(2, talk);
            prepStmt.setInt(3, rating);
            prepStmt.execute();
            connection.commit();
            result = new JSONObject(answerOk);
        } catch (Exception e) {
            connection.rollback();
            result = new JSONObject(answerError);
            result.put("error", e.getMessage());
        }
        return result;
    }
}
