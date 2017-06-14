import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

/**
 * Created by kamil on 03.06.17.
 */
public class Most_popular_talks extends Command {
    private String start_timestamp;
    private String end_timestamp;
    private int limit;

    public Most_popular_talks() {
        name="most_popular_talks";
    }

    public void init(JSONObject argJSON) throws JSONException {
        start_timestamp= argJSON.getString("start_timestamp");
        end_timestamp= argJSON.getString("end_timestamp");
        limit=argJSON.getInt("limit");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws SQLException, JSONException {
        Connection connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);

            PreparedStatement prepStmt = connection.prepareStatement(
            "WITH talks AS (" +
                "   SELECT id_talk" +
                "   FROM talk  " +
                "   WHERE (date_start>?::TIMESTAMP) AND (date_start<?::TIMESTAMP) AND status='public'" +
                "),att AS( " +
                "   SELECT id_talk,COUNT(*) AS sum" +
                "   FROM attendance JOIN talks USING(id_talk)" +
                "   GROUP BY id_talk " +
                ") (SELECT  id_talk,date_start, title, room " +
                "   FROM att JOIN talk USING(id_talk)" +
                "   ORDER BY sum DESC " +
                ");"
            );
            prepStmt.setString(1, start_timestamp);
            prepStmt.setString(2, end_timestamp);
            ResultSet rs = prepStmt.executeQuery();

            JSONArray arrJSON = getArray(rs,limit,"talk","start_timestamp","title","room");
            result= new JSONObject(answerOk);
            result.put("data",arrJSON);
        }catch(SQLException e){
            connection.rollback();
            result= new JSONObject(answerError);
            result.put("Error",e.getMessage());
        }
        return result;    }
}
