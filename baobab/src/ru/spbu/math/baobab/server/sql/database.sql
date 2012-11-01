DROP TABLE IF EXISTS GroupMember;
DROP TABLE IF EXISTS Attendee;
DROP TABLE IF EXISTS AttendeeGroup;
DROP TABLE IF EXISTS TimeSlot;

  -- start_min: start time in minutes since the day start
  -- finish_min: finish time in minutes since the day start
  -- name: time slot name
  -- day: time slot day in [1..7] range. Week starts on Monday
  -- is_odd: time slot flashing flag. See EvenOddWeek enum 
CREATE TABLE TimeSlot (id INT PRIMARY KEY AUTO_INCREMENT, 
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
CREATE TABLE Attendee(id INT PRIMARY KEY AUTO_INCREMENT,
  uid VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(256) NOT NULL,
  type INT NOT NULL CHECK(type >= 0 AND type <= 4) ,
  group_id INT,
  FOREIGN KEY(group_id) REFERENCES AttendeeGroup(id));
  
-- Many-to-many relationship between attendees and groups
CREATE TABLE GroupMember(
  group_id INT, 
  attendee_id INT,
  FOREIGN KEY(group_id) REFERENCES AttendeeGroup(id),
  FOREIGN KEY(attendee_id) REFERENCES Attendee(id));
  