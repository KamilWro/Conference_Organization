import org.json.JSONException;
import org.json.JSONObject;

import java.security.*;
import java.sql.*;
import java.sql.Timestamp;

/**
 * Created by kamil on 28.05.17.
 */
public class Event extends Command{

    private String start_timestamp;
    private String end_timestamp;
    private String password;
    private String login;
    private String eventname;

    public Event(){
        name="event";
    }

    private void init(JSONObject arg) throws JSONException {
        login=arg.getString("login");
        password=arg.getString("password");
        start_timestamp= arg.getString("start_timestamp");
        end_timestamp=arg.getString("end_timestamp");
        eventname=arg.getString("eventname");
    }

    @Override
    public JSONObject command(JSONObject obj) throws SQLException, JSONException {
        Connection connection = DataBase.getInstance().getConnection();
        try {
            JSONObject arg = obj.getJSONObject(name);
            init (arg);
            authorizationOrganizer(login,password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "INSERT INTO event VALUES (?,?::TIMESTAMP,?::TIMESTAMP);");
            prepStmt.setString(1, eventname);
            prepStmt.setString(2, start_timestamp);
            prepStmt.setString(3, end_timestamp);
            prepStmt.execute();
            connection.commit();
            result= new JSONObject(answerOk);
        } catch (Exception e) {
            connection.rollback();
            result= new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }

}
