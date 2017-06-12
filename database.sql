DROP TRIGGER IF EXISTS tTalk_checkDate ON talk;

CREATE TABLE IF NOT EXISTS users (
  login         text      NOT NULL, 
  password      text      NOT NULL,
  role          text      NOT NULL DEFAULT 'users',
  
  CHECK (role IN ('organizer','users')), 
  CONSTRAINT pk_users PRIMARY KEY (login)
);


CREATE TABLE IF NOT EXISTS friends_req (
  login1        text      NOT NULL , 
  login2        text      NOT NULL , 
  
  CHECK (login1<>login2),
  CONSTRAINT pk_friends_req PRIMARY KEY (login1,login2),
  CONSTRAINT fk_friends_req_users1 FOREIGN KEY (login1) REFERENCES users (login) DEFERRABLE,
  CONSTRAINT fk_friends_req_users2 FOREIGN KEY (login2) REFERENCES users (login) DEFERRABLE 
);

CREATE TABLE IF NOT EXISTS event (
  event_name    text      NOT NULL,   
  date_start    timestamp NOT NULL, 
  date_end      timestamp NOT NULL,
  
  CHECK (date_start<=date_end),
  CONSTRAINT pk_event PRIMARY KEY (event_name)  
);


CREATE TABLE IF NOT EXISTS talk (
  id_talk       text        NOT NULL,
  title         text        NOT NULL,
  event_name    text,             
  speaker_login text        NOT NULL, 
  room          integer,        
  status        text        NOT NULL DEFAULT 'waiting',          
  date_start    timestamp   NOT NULL,
  init_evaluation smallint  DEFAULT 0 CHECK (init_evaluation <= 10 AND init_evaluation >= 0),
  added         timestamp   NOT NULL DEFAULT now(),
  
  CHECK (status IN ('public','rejected','waiting')),
  CONSTRAINT pk_talk PRIMARY KEY (id_talk),
  CONSTRAINT fk_talk_event FOREIGN KEY (event_name) REFERENCES event (event_name) DEFERRABLE,
  CONSTRAINT fk_talk_users FOREIGN KEY (speaker_login) REFERENCES users (login) DEFERRABLE    
);


CREATE TABLE IF NOT EXISTS attendance (
  id_talk       text        NOT NULL,   
  login         text        NOT NULL, 
  
  CONSTRAINT pk_attendance PRIMARY KEY (id_talk,login),
  CONSTRAINT fk_attendance_reg FOREIGN KEY (id_talk) REFERENCES talk (id_talk) DEFERRABLE,
  CONSTRAINT fk_attendance_users FOREIGN KEY (login) REFERENCES users (login) DEFERRABLE  
);



CREATE TABLE IF NOT EXISTS evaluation (
  login         text        NOT NULL, 
  id_talk       text        NOT NULL,
  rating        smallint    NOT NULL CHECK (rating <= 10 AND rating >= 0),  
  
  CONSTRAINT pk_evaluation PRIMARY KEY (login,id_talk),
  CONSTRAINT fk_evaluation_users FOREIGN KEY (login) REFERENCES users (login) DEFERRABLE,
  CONSTRAINT fk_evaluation_talk FOREIGN KEY (id_talk) REFERENCES talk (id_talk) DEFERRABLE    
);

CREATE TABLE IF NOT EXISTS registration (
  login      text           NOT NULL,
  event_name text           NOT NULL,
  
  CONSTRAINT pk_registration PRIMARY KEY (login,event_name),
  CONSTRAINT fk_registration_users FOREIGN KEY (login) REFERENCES users (login) DEFERRABLE,
  CONSTRAINT fk_registration_event FOREIGN KEY (event_name) REFERENCES event (event_name) DEFERRABLE  
);
  

CREATE OR REPLACE FUNCTION checkDate()
  RETURNS TRIGGER AS $X$
  DECLARE 
    start_d timestamp;
    end_d timestamp;
  BEGIN   
    IF (NEW.event_name IS NULL) 
    THEN RETURN NEW;
    END IF;
   
    SELECT date_start, date_end INTO start_d, end_d
    FROM event e 
    WHERE e.event_name = NEW.event_name;
      
    IF (NEW.date_start<start_d OR NEW.date_start>end_d)
    THEN RAISE EXCEPTION 'The talk must be presented at the time of the event.'; 
    END IF;
    
    RETURN NEW; 
  END
$X$ LANGUAGE plpgsql;

CREATE TRIGGER tTalk_checkDate BEFORE INSERT OR UPDATE ON talk FOR EACH ROW EXECUTE PROCEDURE checkDate();

CREATE OR REPLACE VIEW friend AS
  SELECT f1.login1, f1.login2 
  FROM friends_req f1 
   JOIN friends_req f2 ON (f1.login1=f2.login2 AND f1.login2=f2.login1);

