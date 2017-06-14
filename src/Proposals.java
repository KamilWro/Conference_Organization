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
public class Proposals extends Command {

    private String login;
    private String password;

    public Proposals() {
        name="proposals";
    }

    private void init(JSONObject argJSON) throws JSONException {
        login=argJSON.getString("login");
        password=argJSON.getString("password");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws SQLException, JSONException {
        Connection connection=DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationOrganizer(login,password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "SELECT id_talk, speaker_login,date_start,title  " +
                "FROM talk " +
                "WHERE status='waiting'" +
                ";"
            );
            ResultSet rs = prepStmt.executeQuery();
            JSONArray arrJSON=getArray(rs,0,"talk","speakerlogin","start_timestamp","title");
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
