import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by kamil on 04.06.17.
 */
public class Friends_events extends Command {

    private String eventname;
    private String password;
    private String login;

    public Friends_events() {
        name="friends_events";
    }


    private void init(JSONObject argJSON) throws JSONException {
        login=argJSON.getString("login");
        password=argJSON.getString("password");
        eventname=argJSON.getString("eventname");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws SQLException, JSONException {
        Connection connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationUser(login,password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "SELECT login1, event_name, login2  " +
                "FROM friend JOIN registration ON (login=login2) " +
                "WHERE login1=? AND event_name=? " +
                ";"
            );
            prepStmt.setString(1, login);
            prepStmt.setString(2, eventname);
            ResultSet rs = prepStmt.executeQuery();
            JSONArray arrJSON=getArray(rs,0,"login","eventname","friendlogin");
            result=new JSONObject(answerOk);
            result.put("data",arrJSON);
        }catch(SQLException e){
            connection.rollback();
            result=new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }

}
