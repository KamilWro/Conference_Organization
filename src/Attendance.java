import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

/**
 * Created by kamil on 29.05.17.
 */
public class Attendance extends Command {
    private Connection connection;
    private String login;
    private String talk;
    private String password;

    public Attendance() {
        name="attendance";
    }

    private void init(JSONObject arg) throws JSONException {
        login=arg.getString("login");
        password=arg.getString("password");
        talk=arg.getString("talk");
    }
    @Override
    public JSONObject command(JSONObject objJSON) throws JSONException, SQLException {
        connection = DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            if (!isRowInTalk(talk,"public"))
                throw new SQLException("Talk doeas not exist: "+talk);

            authorizationUser(login, password);
            PreparedStatement prepStmt = connection.prepareStatement(
                    "INSERT INTO attendance VALUES (?,?);");
            prepStmt.setString(1, talk);
            prepStmt.setString(2, login);
            prepStmt.execute();
            connection.commit();
            result = new JSONObject(answerOk);
        } catch (SQLException e) {
            connection.rollback();
            result= new JSONObject(answerError);
            result.put("Error",e.getMessage());
        }
        return result;
    }
}
