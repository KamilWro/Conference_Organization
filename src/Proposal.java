import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by kamil on 31.05.17.
 */
public class Proposal extends Command{

    private String login;
    private String password;
    private String talk;
    private String title;
    private String start_timestamp;

    public Proposal()  {
        name="proposal";
    }

    private void init(JSONObject argJSON) throws JSONException {
        login=argJSON.getString("login");
        password=argJSON.getString("password");
        talk=argJSON.getString("talk");
        title=argJSON.getString("title");
        start_timestamp= argJSON.getString("start_timestamp");
    }
    @Override
    public JSONObject command(JSONObject objJSON) throws JSONException, SQLException {
        Connection connection = DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationUser(login, password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "INSERT INTO talk (speaker_login,id_talk,title,date_start,status) VALUES (?,?,?,?::TIMESTAMP,'waiting');");
            prepStmt.setString(1, login);
            prepStmt.setString(2, talk);
            prepStmt.setString(3, title);
            prepStmt.setString(4, start_timestamp);
            prepStmt.execute();
            connection.commit();
            result=new JSONObject(answerOk);
        }catch (SQLException e){
            connection.rollback();
            result=new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }
}
