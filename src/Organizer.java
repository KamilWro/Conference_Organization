import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by kamil on 28.05.17.
 */
public class Organizer extends Command {
    private String newlogin;
    private String secret;
    private String newpassword;

    public Organizer() {
        name="organizer";
    }

    private void init(JSONObject argJSON) throws JSONException {
        secret=argJSON.getString("secret");
        newlogin=argJSON.getString("newlogin");
        newpassword=argJSON.getString("newpassword");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws JSONException, SQLException {
        Connection connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationSecret(secret);
            PreparedStatement prepStmt = connection.prepareStatement(
            "INSERT INTO users VALUES (?,?,'organizer');");
            prepStmt.setString(1, newlogin);
            prepStmt.setString(2, newpassword);
            prepStmt.execute();
            connection.commit();
            result=new JSONObject(answerOk);
        }catch (SQLException e){
            connection.rollback();
            result=new JSONObject(answerError);
            result.put("Error",e.getMessage());
        }
        return result;
    }

    private void authorizationSecret(String key) throws SQLException {
        if (!key.equals("d8578edf8458ce06fbc5bb76a58c5ca4"))
            throw new SQLException("Invalid secret key:"+key);

    }
}
