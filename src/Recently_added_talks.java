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
public class Recently_added_talks extends Command {

    private int limit;

    public Recently_added_talks() {
        name="recently_added_talks";
    }

    private void init(JSONObject argJSON) throws JSONException {
        limit=argJSON.getInt("limit");
    }

    @Override
    public JSONObject command(JSONObject objJSON) throws SQLException, JSONException {
        Connection connection = DataBase.getInstance().getConnection();
        try {
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            PreparedStatement prepStmt = connection.prepareStatement(
            "SELECT * FROM talk ORDER BY added;");
            ResultSet rs = prepStmt.executeQuery();
            JSONArray arrJSON= getArray(rs,limit,"talk","speakerlogin","start_timestamp","title","room");
            result= new JSONObject(answerOk);
            result.put("data",arrJSON);
        }catch (SQLException e){
            connection.rollback();
            result=new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }
}
