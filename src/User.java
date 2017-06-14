import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by kamil on 28.05.17.
 */
public class User extends Command {

    private String newpassword;
    private String login;
    private String newlogin;
    private String password;

    public User() {
        name="user";
    }

    private void init(JSONObject arg) throws JSONException {
        login=arg.getString("login");
        password=arg.getString("password");
        newlogin=arg.getString("newlogin");
        newpassword=arg.getString("newpassword");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws JSONException, SQLException {
        Connection connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationOrganizer(login, password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "INSERT INTO users VALUES (?,?);");
            prepStmt.setString(1, newlogin);
            prepStmt.setString(2, newpassword);
            prepStmt.execute();
            connection.commit();
            result=new JSONObject(answerOk);
        }catch(SQLException e){
            connection.rollback();
            result= new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }

}
