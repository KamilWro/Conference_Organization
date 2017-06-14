import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

/**
 * Created by kamil on 03.06.17.
 */
public class Best_talks extends Command {
    private Connection connection;
    private int all;
    private int limit;
    private String end_timestamp;
    private String start_timestamp;

    public Best_talks() {
        name="best_talks";
    }
    private void init(JSONObject argJSON) throws JSONException {
        start_timestamp= argJSON.getString("start_timestamp");
        end_timestamp= argJSON.getString("end_timestamp");
        limit=argJSON.getInt("limit");
        all=argJSON.getInt("all");
    }



    @Override
    public JSONObject command(JSONObject objJSON) throws SQLException, JSONException {
        connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            ResultSet rs;
            if(all==1){
                rs=allRating();
            }else{
                rs=userRating();
            }
            JSONArray arrJSON= getArray(rs,limit,"talk","start_timestamp","title","room");
            result= new JSONObject(answerOk);
            result.put("data",arrJSON);
        }catch(Exception e){
            connection.rollback();
            result= new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }

    private ResultSet userRating() throws SQLException {
        ResultSet rs;
        PreparedStatement prepStmt = connection.prepareStatement(
        "WITH talks AS (" +
            "  SELECT id_talk, init_evaluation" +
            "  FROM talk  " +
            "  WHERE date_start>?::TIMESTAMP AND date_start<?::TIMESTAMP AND now()<=date_start AND status='public' " +
            "),sum AS(" +
            "  (SELECT e.id_talk,e.rating " +
            "   FROM evaluation e JOIN attendance a USING(login, id_talk) JOIN talks USING (id_talk)" +
            "  )" +
            "  UNION " +
            "  (SELECT id_talk, init_evaluation AS rating" +
            "   FROM talks" +
            "  )" +
            "), avg AS ( " +
            "  SELECT id_talk,AVG(rating) AS average" +
            "  FROM sum " +
            "  GROUP BY id_talk" +
            ") (SELECT  id_talk,date_start, title, room, average " +
            "   FROM avg JOIN talk USING (id_talk) " +
            "   ORDER BY average DESC" +
            "   );"
        );
        prepStmt.setString(1, start_timestamp);
        prepStmt.setString(2, end_timestamp);
        rs = prepStmt.executeQuery();
        return rs;
    }

    private ResultSet allRating() throws SQLException {
        ResultSet rs;
        PreparedStatement prepStmt = connection.prepareStatement(
        "WITH talks AS (" +
            "   SELECT id_talk" +
            "   FROM talk " +
            "   WHERE date_start>?::TIMESTAMP AND date_start<?::TIMESTAMP AND now()<=date_start AND status='public'" +
            "),sum AS( " +
            "   (SELECT id_talk, rating" +
            "    FROM evaluation JOIN talks USING (id_talk)" +
            "   )" +
            "   UNION " +
            "   (SELECT id_talk, init_evaluation AS rating" +
            "    FROM talk JOIN talks USING (id_talk)" +
            "   )" +
            "),avg AS ( " +
            "   SELECT id_talk,AVG(rating) AS average" +
            "   FROM sum " +
            "   GROUP BY id_talk" +
            ") (SELECT  id_talk,date_start, title, room,average " +
            "   FROM avg JOIN talk USING (id_talk) " +
            "   ORDER BY average DESC" +
            ");"
        );
        prepStmt.setString(1, start_timestamp);
        prepStmt.setString(2, end_timestamp);
        rs = prepStmt.executeQuery();
        return rs;
    }

}
