import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by kamil on 31.05.17.
 */
public class Friends extends Command {

    private String login1;
    private String password;
    private String login2;

    public Friends() {
        name="friends";
    }

    private void init(JSONObject arg) throws JSONException {
        login1=arg.getString("login1");
        password=arg.getString("password");
        login2=arg.getString("login2");
    }
    @Override
    public JSONObject command(JSONObject objJSON) throws JSONException, SQLException {
        Connection connection = DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationUser(login1, password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "INSERT INTO friends_req VALUES (?,?);");
            prepStmt.setString(1, login1);
            prepStmt.setString(2, login2);
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
