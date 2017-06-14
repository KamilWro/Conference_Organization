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
public class Recommended_talks extends Command {

    private Connection connection;
    private String end_timestamp;
    private String start_timestamp;
    private int limit;
    private String password;
    private String login;

    public Recommended_talks() {
        name="recommended_talks";
    }

    private void init(JSONObject argJSON) throws JSONException {
        login=argJSON.getString("login");
        password=argJSON.getString("password");
        limit=argJSON.getInt("limit");
        start_timestamp=argJSON.getString("start_timestamp");
        end_timestamp=argJSON.getString("end_timestamp");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws SQLException, JSONException {
        connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationUser(login,password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "WITH talk1 AS ( " +
                " SELECT * FROM talk WHERE date_start>?::TIMESTAMP AND date_start<?::TIMESTAMP AND status='public' " +
                "),sum AS(" +
                " (SELECT id_talk, rating FROM evaluation JOIN talk1 USING (id_talk)) " +
                " UNION " +
                " ( SELECT id_talk, init_evaluation AS rating FROM talk1 )" +
                "),avg AS (" +
                "   SELECT id_talk,AVG(rating) AS average FROM sum RIGHT JOIN talk1 USING (id_talk) GROUP BY id_talk " +
                "),att AS( " +
                "   SELECT id_talk, COUNT(*) AS sum FROM attendance GROUP BY id_talk " +
                "),restAtt AS (" +
                "   SELECT id_talk, COALESCE(sum,0) AS sum FROM att RIGHT JOIN talk1 USING(id_talk)" +
                "),popEvent AS ( " +
                "  (SELECT event_name, id_talk, COUNT(*) AS popUser FROM registration JOIN talk1 USING (event_name) GROUP BY event_name,id_talk)" +
                "),restPopEvent AS (" +
                "  SELECT id_talk,event_name,COALESCE(popUser,0) AS popUser FROM popEvent RIGHT JOIN talk1 USING(id_talk,event_name)" +
                "),friendEvent AS( " +
                "   SELECT event_name, id_talk, COUNT(*) AS popFriend FROM friend JOIN registration ON (login=login2 AND login1=?) JOIN talk1 USING (event_name) " +
                "   GROUP BY event_name, id_talk  " +
                "),restFriendEvent AS (" +
                "   SELECT id_talk,event_name, COALESCE(popFriend,0) AS popFriend FROM friendEvent RIGHT JOIN talk1 USING(id_talk,event_name)" +
                "),speakerFriend AS(" +
                "   SELECT id_talk,COUNT(*) AS speakFriend FROM talk1 JOIN friend ON (login2=speaker_login AND login1=?)" +
                "   GROUP BY id_talk" +
                "),restSpeakerFriend AS (" +
                "   SELECT id_talk, COALESCE(speakFriend,0) AS speakFriend FROM speakerFriend RIGHT JOIN talk1 USING(id_talk)" +
                ")(SELECT id_talk,speaker_login,date_start, title, room, " +
                "  ROUND((average + (sum*0.7)+ (popUser*0.6)+ (popFriend*0.8)+(speakFriend*1.6)),2) AS score" +
                " FROM avg " +
                "  JOIN restatt USING (id_talk) " +
                "  JOIN restPopEvent USING(id_talk) " +
                "  JOIN restFriendEvent USING (id_talk) " +
                "  JOIN talk1 USING (id_talk)" +
                "  JOIN restSpeakerFriend USING (id_talk)" +
                " ORDER BY score DESC" +
                " );"
            );
            prepStmt.setString(1,start_timestamp);
            prepStmt.setString(2,end_timestamp);
            prepStmt.setString(3,login);
            prepStmt.setString(4,login);
            ResultSet rs = prepStmt.executeQuery();
            JSONArray arrJSON= getArray(rs,limit,"talk","speakerlogin","start_timestamp","title","room","score");
            result= new JSONObject(answerOk);
            result.put("data",arrJSON);
        }catch(Exception e){
            connection.rollback();
            result= new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }
}
