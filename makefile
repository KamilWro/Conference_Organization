JC = javac 
DIR = ./src/
JFLAGS = -classpath $(DIR):json.jar:postgresql.jar
JVM= java 
.SUFFIXES: .java .class
.java.class:
	 $(JC) $(JFLAGS) $*.java 

DATABASE = $(DIR)DataBase.class
ICOMMAND = $(DIR)Command.class
COMMANDS = \
	$(DIR)Abandoned_talks.class \
	$(DIR)Event.class \
	$(DIR)Recommended_talks.class \
	$(DIR)Friends_events.class \
	$(DIR)Registration.class \
	$(DIR)Attendance.class \
	$(DIR)Friends.class \
	$(DIR)Rejected_talks.class \
	$(DIR)Attended_talks.class \
	$(DIR)Friends_talks.class \
	$(DIR)Reject.class \
	$(DIR)Best_talks.class \
	$(DIR)Most_popular_talks.class \
	$(DIR)Talk.class \
	$(DIR)Organizer.class \
	$(DIR)User.class \
	$(DIR)Proposal.class \
	$(DIR)User_plan.class \
	$(DIR)Day_plan.class \
	$(DIR)Proposals.class \
	$(DIR)Evaluation.class \
	$(DIR)Recently_added_talks.class 
MAIN	= $(DIR)Main.class
APPLICATION = $(DIR)Application.class

default: Main.class

Main.class: $(DIR)Main.java $(APPLICATION) $(ICOMMAND) $(COMMANDS)
Application.class: $(DIR)Application.java $(DATABASE) $(ICOMMAND) $(COMMANDS)
Command.class: $(DIR)Command.java $(DATABASE)
DataBase.class: $(DIR)DataBase.java
%.class:$(DIR)%.java $(ICOMMAND) $(DATABASE)

clean :
		rm -f $(DATABASE) $(ICOMMAND) $(COMMANDS) $(MAIN) $(APPLICATION)
		
run: $(MAIN)
ifeq ( $(FILE) , $(EMPTY) )
	$(JVM) $(JFLAGS) Main
else
	$(JVM) $(JFLAGS) Main<$(FILE)
endif
	
