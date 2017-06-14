import org.json.JSONObject;

import java.util.Scanner;

/**
 * Created by kamil on 11.06.17.
 */
public class Main {
    public static void main(String[] args){
        String openDB;
        Scanner in = new Scanner(System.in);
        openDB = in.nextLine();

        Application application=new Application();
        application.addCommand(new User());
        application.addCommand(new Organizer());
        application.addCommand(new Event());
        application.addCommand(new Registration());
        application.addCommand(new Talk());
        application.addCommand(new Reject());
        application.addCommand(new Proposal());
        application.addCommand(new Friends());
        application.addCommand(new Evaluation());
        application.addCommand(new Attendance());
        application.addCommand(new User_plan());
        application.addCommand(new Attended_talks());
        application.addCommand(new Best_talks());
        application.addCommand(new Day_plan());
        application.addCommand(new Most_popular_talks());
        application.addCommand(new Abandoned_talks());
        application.addCommand(new Rejected_talks());
        application.addCommand(new Proposals());
        application.addCommand(new Friends_events());
        application.addCommand(new Friends_talks());
        application.addCommand(new Recommended_talks());
        application.addCommand(new Recently_added_talks());
        try {
            application.connect(new JSONObject(openDB));
            String inString;
            while(!(inString = in.nextLine()).equals("")) {
                application.questions(new JSONObject(inString));
            }
            while(application.isAnswer()) {
                System.out.println(application.answer());
            }
            application.close();
        } catch (Exception e) {
            String result = "{ \"status\": \"ERROR\",\"error\": \""+e.getMessage()+"\"}";
            System.out.println(result);
        }

    }
}
