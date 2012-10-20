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