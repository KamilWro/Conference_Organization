import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

/**
 * Created by kamil on 03.06.17.
 */
public class Day_plan extends Command{
    private String timestamp;

    public Day_plan() {
        name="day_plan";
    }

    private void init(JSONObject argJSON) throws JSONException {
        timestamp= argJSON.getString("timestamp");
    }


    @Override
    public JSONObject command(JSONObject objJSON) throws SQLException, JSONException {
        Connection connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            PreparedStatement prepStmt = connection.prepareStatement(
            "SELECT  id_talk,date_start,title, room " +
                "FROM talk " +
                "WHERE ?::DATE = date_start::DATE AND status='public' " +
                "ORDER BY room,date_start;"
            );
            prepStmt.setString(1, timestamp);
            ResultSet rs = prepStmt.executeQuery();
            JSONArray arrJSON=getArray(rs,0,"talk","start_timestamp","title", "room");
            result= new JSONObject(answerOk);
            result.put("data",arrJSON);
        }catch(SQLException e){
            connection.rollback();
            result= new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }
}
