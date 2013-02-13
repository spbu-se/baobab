DROP TABLE IF EXISTS CalendarTopic;
DROP TABLE IF EXISTS Calendar;
DROP TABLE IF EXISTS EventAttendee;
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
  FOREIGN KEY(group_id) REFERENCES AttendeeGroup(id),
  CHECK(type >= 2 AND group_id IS NOT NULL OR type < 2 AND group_id IS NULL));
  
-- Many-to-many relationship between attendees and groups
CREATE TABLE GroupMember(
  group_id INT, 
  attendee_id INT,
  FOREIGN KEY(group_id) REFERENCES AttendeeGroup(id),
  FOREIGN KEY(attendee_id) REFERENCES Attendee(id));
  
CREATE TABLE Auditorium(
  num VARCHAR(64) PRIMARY KEY,
  capacity INT CHECK (capacity > 0)
);

CREATE TABLE Topic(
  uid VARCHAR(32) NOT NULL PRIMARY KEY,
  name VARCHAR(256),
  type INT NOT NULL CHECK(type >= 0 AND type <= 5),
  url VARCHAR(256)
);

CREATE TABLE Event(
  id INT PRIMARY KEY AUTO_INCREMENT,
  `date` DATE NOT NULL,
  time_slot_id INT NOT NULL,
  topic_id VARCHAR(32) NOT NULL,
  auditorium_num VARCHAR(64),
  UNIQUE(`date`, time_slot_id, topic_id),
  FOREIGN KEY (time_slot_id) REFERENCES TimeSlot(id),
  FOREIGN KEY (auditorium_num) REFERENCES Auditorium(num),
  FOREIGN KEY (topic_id) REFERENCES Topic(uid)
);

CREATE TABLE TopicOwner(
  topic_id VARCHAR(32),
  attendee_id INT,
  FOREIGN KEY (topic_id) REFERENCES Topic(uid),
  FOREIGN KEY (attendee_id) REFERENCES Attendee(id)
);

CREATE TABLE TopicAttendee(
  topic_id VARCHAR(32),
  attendee_id INT,
  FOREIGN KEY (topic_id) REFERENCES Topic(uid),
  FOREIGN KEY (attendee_id) REFERENCES Attendee(id)  
);

-- This table keeps relationships between attendees and event instances.
-- Attendees may opt to attend just a single event or to miss a single event.
CREATE TABLE EventAttendee(
  event_id INT NOT NULL,
  attendee_uid VARCHAR(64) NOT NULL,
  is_attending BOOLEAN NOT NULL DEFAULT TRUE,
  UNIQUE(event_id, attendee_uid),
  FOREIGN KEY (event_id) REFERENCES Event(id),
  FOREIGN KEY(attendee_uid) REFERENCES Attendee(uid)
);

-- This table is just an enumeration of the available calendars
CREATE TABLE Calendar(
  uid VARCHAR(32) PRIMARY KEY
);

-- This table implements M:N relationship between calendars and topics
CREATE TABLE CalendarTopic(
  calendar_uid VARCHAR(32),
  topic_uid VARCHAR(32),
  FOREIGN KEY (calendar_uid) REFERENCES Calendar(uid),
  FOREIGN KEY (topic_uid) REFERENCES Topic(uid)
);

DROP PROCEDURE IF EXISTS Topic_AddEvent;

CREATE PROCEDURE Topic_AddEvent(_topic_id VARCHAR(32), _event_date DATE, _time_slot_id INT, _auditorium_num VARCHAR(64), OUT _event_id INT)
BEGIN
DECLARE evt_id INT;
SELECT id INTO evt_id FROM Event WHERE topic_id = _topic_id AND time_slot_id = _time_slot_id AND date = _event_date;
IF evt_id IS NULL THEN
  INSERT INTO Event (topic_id, `date`, time_slot_id, auditorium_num) VALUES (_topic_id, _event_date, _time_slot_id, _auditorium_num);
  SELECT id INTO evt_id FROM Event WHERE topic_id = _topic_id AND time_slot_id = _time_slot_id AND `date` = _event_date;
END IF;
SET _event_id := evt_id;
END

DROP VIEW IF EXISTS ExamsView;

CREATE VIEW ExamsView AS
SELECT att.uid AS att_uid, att.name AS att_name, att.type AS att_type, 
    ts.id AS ts_id, ts.name AS ts_name, ts.start_min AS ts_start_min, ts.finish_min AS ts_finish_min, ts.day AS ts_day, ts.is_odd AS ts_is_odd,
    au.num AS au_num, au.capacity AS au_capacity,
    top.uid AS topic_uid, top.type AS topic_type, top.name AS topic_name,
    att2.uid AS owner_uid, att2.name AS owner_name, att2.type AS owner_type,
    ev.date AS ev_date,
    ct.calendar_uid AS calendar_uid
    FROM CalendarTopic ct 
    JOIN Topic top ON (top.uid = ct.topic_uid)
    JOIN Event ev ON (ev.topic_id = ct.topic_uid)
    LEFT OUTER JOIN TopicOwner topow ON (ev.topic_id = topow.topic_id)
    LEFT OUTER JOIN Attendee att2 ON (topow.attendee_id = att2.id)
    JOIN EventAttendee ea ON (ea.event_id = ev.id)
    JOIN Attendee att ON (ea.attendee_uid = att.uid)
    JOIN TimeSlot ts ON (ts.id = ev.time_slot_id)
    JOIN Auditorium au ON (au.num = ev.auditorium_num)    
    ORDER BY att.uid, ev.date, ts.start_min, top.name;