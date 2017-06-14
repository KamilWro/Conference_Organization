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
public class Attended_talks extends Command {

    private String password;
    private String login;

    public Attended_talks() {
        name="attended_talks";
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
            authorizationUser(login,password);
            PreparedStatement prepStmt = connection.prepareStatement(
            "SELECT  id_talk, date_start, title, room " +
                "FROM attendance JOIN talk USING (id_talk)" +
                "WHERE login=?;"
            );
            prepStmt.setString(1, login);
            ResultSet rs = prepStmt.executeQuery();

            JSONArray arrJSON = getArray(rs,0,"talk","start_timestamp","title","room");
            result=new JSONObject(answerOk);
            result.put("data",arrJSON);
        }catch(Exception e){
            connection.rollback();
            result= new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }
}
