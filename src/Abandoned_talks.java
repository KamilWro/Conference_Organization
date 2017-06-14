import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by kamil on 03.06.17.
 */
public class Abandoned_talks extends Command {

    private String login;
    private String password;
    private int limit;

    public Abandoned_talks() {
        name="abandoned_talks";
    }

    private void init(JSONObject argJSON) throws JSONException {
        login=argJSON.getString("login");
        password=argJSON.getString("password");
        limit=argJSON.getInt("limit");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws SQLException, JSONException {
        Connection connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationOrganizer(login,password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "WITH regTalk AS ( " +
                 " SELECT id_talk, COUNT(*) AS number1" +
                 " FROM talk JOIN registration USING (event_name)" +
                 " GROUP BY id_talk" +
                 "), attTalk AS (" +
                 " SELECT id_talk, COUNT(*) AS number2" +
                 "  FROM registration r JOIN talk t USING(event_name) JOIN attendance a USING(id_talk)" +
                 "  WHERE r.login=a.login" +
                 "  GROUP BY t.id_talk" +
                 "), restAttTalk AS (SELECT id_talk,COALESCE(number2,0) AS number2 FROM attTalk RIGHT JOIN regTalk USING(id_talk) )" +
                 "( " +
                 " SELECT  id_talk, date_start, title, room, (number1- number2) AS number " +
                 " FROM restAttTalk JOIN regTalk USING (id_talk) JOIN talk USING (id_talk) " +
                 " ORDER BY number DESC" +
                 " );"
            );
            ResultSet rs = prepStmt.executeQuery();
            JSONArray arrJSON = getArray(rs,limit,"talk","start_timestamp","title","room","number");
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
