import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by kamil on 29.05.17.
 */
public class Reject extends Command {
    private Connection connection;
    private String login;
    private String password;
    private String talk;

    public Reject() {
        name="reject";
    }
    private void init(JSONObject arg) throws JSONException {
        login=arg.getString("login");
        password=arg.getString("password");
        talk=arg.getString("talk");
    }

    @Override
    public JSONObject command(JSONObject obj) throws JSONException, SQLException {
        connection = DataBase.getInstance().getConnection();
        try {
            JSONObject arg = obj.getJSONObject(name);
            init(arg);
            authorizationOrganizer(login, password);
            if (!isRowInTalk(talk,"waiting"))
                throw new SQLException("No talk was found.");

            PreparedStatement prepStmt = connection.prepareStatement(
            "UPDATE talk " +
                "SET status = 'rejected'" +
                "WHERE id_talk = ?"
            );
            prepStmt.setString(1, talk);
            prepStmt.execute();
            connection.commit();
            result = new JSONObject(answerOk);
        }catch (SQLException e){
            connection.rollback();
            result= new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }
}
