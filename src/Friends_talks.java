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
public class Friends_talks extends Command {

    private String password;
    private String login;
    private String start_timestamp;
    private String end_timestamp;
    private int limit;

    public Friends_talks() {
        name="friends_talks";
    }

    private void init(JSONObject argJSON) throws JSONException {
        password=argJSON.getString("password");
        login=argJSON.getString("login");
        start_timestamp=argJSON.getString("start_timestamp");
        end_timestamp=argJSON.getString("end_timestamp");
        limit=argJSON.getInt("limit");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws JSONException, SQLException {
        Connection connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationUser(login,password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "WITH talks AS (" +
                "  SELECT id_talk, speaker_login, date_start, title, room" +
                "   FROM talk  " +
                "  WHERE date_start>?::TIMESTAMP AND date_start<?::TIMESTAMP AND status='public' " +
                ") (SELECT  id_talk,speaker_login, date_start, title, room " +
                "   FROM talks JOIN friend ON (login2=speaker_login)" +
                "   WHERE login1=?" +
                "   ORDER BY date_start" +
                ");"
            );
            prepStmt.setString(1,start_timestamp);
            prepStmt.setString(2,end_timestamp);
            prepStmt.setString(3,login);
            ResultSet rs = prepStmt.executeQuery();
            JSONArray arrJSON=getArray(rs,limit,"talk","speakerlogin","start_timestamp","title","room");
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
