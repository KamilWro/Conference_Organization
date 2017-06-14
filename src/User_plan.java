import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by kamil on 31.05.17.
 */
public class User_plan extends Command {
    int limit;
    String login;

    public User_plan() {
        name="user_plan";
    }
    private void init(JSONObject arg) throws JSONException {
        limit=arg.getInt("limit");
        login=arg.getString("login");
    }
    @Override
    public JSONObject command(JSONObject obj) throws JSONException, SQLException {
        Connection connection=DataBase.getInstance().getConnection();
        try {
            JSONObject arg = obj.getJSONObject(name);
            init(arg);
            PreparedStatement prepStmt = connection.prepareStatement(
            "SELECT  reg.login,t.id_talk, t.date_start, t.title, t.room " +
                 "FROM registration reg JOIN talk t USING (event_name) " +
                 "WHERE reg.login=? AND now() <= t.date_start " +
                 "ORDER BY 3;"
            );
            prepStmt.setString(1, login);
            ResultSet rs = prepStmt.executeQuery();
            JSONArray arrJSON=getArray(rs,limit,"login","talk","start_timestamp","title","room");
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
