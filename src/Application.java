import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by kamil on 27.05.17.
 */
public class Application {
    private String answerOk = "{ \"status\": \"OK\"}";
    private String answerError = "{ \"status\": \"ERROR\"}";
    private String answerNotImplemented = "{ \"status\": \"NOT IMPLEMENTED\"}";
    private List<Command> commands= new ArrayList<Command>();
    private ArrayDeque<JSONObject> answers= new ArrayDeque<JSONObject>();

    public Application() {}

    public void addCommand(Command command){
        commands.add(command);
    }

    public void questions(JSONObject questionJSON) throws JSONException {
        try {
            JSONObject answerJSON = result(questionJSON);
            answers.addLast(answerJSON);
        } catch (Exception e) {
            JSONObject result = new JSONObject(answerError);
            result.put("Error", e.getMessage());
            answers.add(result);
        }
    }

    private JSONObject result(JSONObject commandJSON) throws JSONException, SQLException {
        for (Command command1 : commands)
            if (command1.isCommand(commandJSON.keys().next().toString())) {
                return command1.command(commandJSON);
            }
        return new JSONObject(answerNotImplemented);
    }

    public void connect(JSONObject obj) throws JSONException, SQLException, ClassNotFoundException {
        DataBase dataBase=DataBase.getInstance();
        dataBase.init(obj);
        try {
            dataBase.connect();
        } catch (IOException e) {}
        answers.add(new JSONObject(answerOk));
    }

    public JSONObject answer(){
        return answers.removeFirst();
    }

    public boolean isAnswer(){
        return !answers.isEmpty();
    }
    public void close() throws SQLException {
        DataBase.getInstance().close();
    }
}
