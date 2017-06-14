import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by kamil on 28.05.17.
 */
public class Registration extends Command {

    private String login;
    private String eventname;
    private String password;

    public Registration() {
        name="register_user_for_event";
    }

    private void init(JSONObject argJSON) throws JSONException {
        login=argJSON.getString("login");
        password=argJSON.getString("password");
        eventname=argJSON.getString("eventname");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws JSONException, SQLException {
        Connection connection = DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationUser(login, password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "INSERT INTO registration VALUES (?,?);");
            prepStmt.setString(1, login);
            prepStmt.setString(2, eventname);
            prepStmt.execute();
            connection.commit();
            result=new JSONObject(answerOk);
        }catch (SQLException e){
            connection.rollback();
            result=new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }
}
