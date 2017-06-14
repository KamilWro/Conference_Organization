import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;

/**
 * Created by kamil on 28.05.17.
 */
public class Talk extends Command {
    private String login;
    private String password;
    private String talk;
    private String speakerlogin;
    private String title;
    private String start_timestamp;
    private int room;
    private int initial_evaluation;
    private String eventname;
    private String status;
    private Connection connection;

    public Talk() {
        name="talk";
    }

    private void init(JSONObject obj) throws JSONException {
        talk=obj.getString("talk");
        try{
            eventname=obj.getString("eventname");
            if(eventname.equals(""))
                eventname=null;
        }
        catch (JSONException e){
            eventname=null;
        }

        speakerlogin=obj.getString("speakerlogin");
        title=obj.getString("title");
        initial_evaluation=obj.getInt("initial_evaluation");
        login=obj.getString("login");
        password=obj.getString("password");
        status="public";
        start_timestamp=obj.getString("start_timestamp");
        room=obj.getInt("room");
    }


    @Override
    public JSONObject command(JSONObject objJSON) throws JSONException, SQLException {
        try {
            connection = DataBase.getInstance().getConnection();
            JSONObject argJSON = objJSON.getJSONObject(name);
            init(argJSON);
            authorizationOrganizer(login, password);
            if (isRowInTalk(talk,"waiting"))
                delete();
            insert();
            connection.commit();
            result=new JSONObject(answerOk);
        } catch (SQLException e) {
            connection.rollback();
            result=new JSONObject(answerError);
            result.put("error",e.getMessage());
        }
        return result;
    }
    private void delete() throws SQLException {
        PreparedStatement prepStmt;
        prepStmt = connection.prepareStatement(
        "DELETE FROM talk WHERE id_talk = ? ;"
        );
        prepStmt.setString(1,talk);
        prepStmt.execute();
    }

    private void insert() throws SQLException {
        PreparedStatement prepStmt;
        prepStmt = connection.prepareStatement(
        "INSERT INTO talk VALUES (?,?,?,?,?,?,?::TIMESTAMP,?);"
        );
        prepStmt.setString(1, talk);
        prepStmt.setString(2, title);
        if(eventname==null){
            prepStmt.setNull(3, 0);
        }else{
            prepStmt.setString(3, eventname);
        }
        prepStmt.setString(4, speakerlogin);
        prepStmt.setInt(5, room);
        prepStmt.setString(6,status);
        prepStmt.setString(7, start_timestamp);
        prepStmt.setInt(8,initial_evaluation);
        prepStmt.execute();
    }
}
