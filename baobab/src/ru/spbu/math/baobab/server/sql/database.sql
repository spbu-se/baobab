DROP TABLE IF EXISTS TopicAttendee;
DROP TABLE IF EXISTS TopicOwner;
DROP TABLE IF EXISTS Event;
DROP TABLE IF EXISTS Topic;
DROP TABLE IF EXISTS Auditorium;
DROP TABLE IF EXISTS GroupMember;
DROP TABLE IF EXISTS Attendee;
DROP TABLE IF EXISTS AttendeeGroup;
DROP TABLE IF EXISTS TimeSlot;

  -- start_min: start time in minutes since the day start
  -- finish_min: finish time in minutes since the day start
  -- name: time slot name
  -- day: time slot day in [1..7] range. Week starts on Monday
  -- is_odd: time slot flashing flag. See EvenOddWeek enum 
CREATE TABLE TimeSlot (
  id VARCHAR(32) PRIMARY KEY, 
  start_min INT NOT NULL CHECK(start_min >= 0 AND start_min < 1440),
  finish_min INT NOT NULL CHECK(finish_min > start_min AND finish_min < 1440),
  name VARCHAR(32) NOT NULL,
  day INT CHECK(day >= 1 AND day <= 7),
  is_odd INT DEFAULT 0 CHECK(is_odd >= 0 AND is_odd <= 2),
  UNIQUE (name, day, is_odd));
  
-- Represents a list of group members for attendees whose type is a group type
CREATE TABLE AttendeeGroup(id INT PRIMARY KEY AUTO_INCREMENT);

-- Attendee entities (see Attendee interface in the java model for details)
-- uid: user-defined ID, corresponds to Attendee.getID()
-- name: attendee name
-- type: attendee type, constants correspond to Attendee.Type enum
-- group_id: reference to group member list which may exist only if attendee type is a group type 
CREATE TABLE Attendee(
  uid VARCHAR(64) PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  type INT NOT NULL CHECK(type >= 0 AND type <= 4) ,
  group_id INT,
  FOREIGN KEY(group_id) REFERENCES AttendeeGroup(id),
  CHECK(type >= 2 AND group_id IS NOT NULL OR type < 2 AND group_id IS NULL));
  
-- Many-to-many relationship between attendees and groups
CREATE TABLE GroupMember(
  group_id INT, 
  attendee_id VARCHAR(64),
  FOREIGN KEY(group_id) REFERENCES AttendeeGroup(id),
  FOREIGN KEY(attendee_id) REFERENCES Attendee(uid));
  
CREATE TABLE Auditorium(
  num VARCHAR(10) PRIMARY KEY,
  capacity INT CHECK (capacity > 0)
);

CREATE TABLE Topic(
  uid VARCHAR(32) PRIMARY KEY,
  name VARCHAR(256),
  type INT NOT NULL CHECK(type >= 0 AND type <= 5)
);

CREATE TABLE Event(
  id INT PRIMARY KEY AUTO_INCREMENT,
  `date` DATE NOT NULL,
  time_slot_id VARCHAR(32) NOT NULL,
  topic_id VARCHAR(32) NOT NULL,
  auditorium_num VARCHAR(10),
  FOREIGN KEY (time_slot_id) REFERENCES TimeSlot(id),
  FOREIGN KEY (auditorium_num) REFERENCES Auditorium(num),
  FOREIGN KEY (topic_id) REFERENCES Topic(uid)
);

CREATE TABLE TopicOwner(
  topic_id VARCHAR(32),
  attendee_id VARCHAR(64),
  FOREIGN KEY (topic_id) REFERENCES Topic(uid),
  FOREIGN KEY (attendee_id) REFERENCES Attendee(uid)
);

CREATE TABLE TopicAttendee(
  topic_id VARCHAR(32),
  attendee_id VARCHAR(64),
  FOREIGN KEY (topic_id) REFERENCES Topic(uid),
  FOREIGN KEY (attendee_id) REFERENCES Attendee(uid)  
);