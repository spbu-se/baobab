package ru.spbu.math.baobab.server;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.EvenOddWeek;
import ru.spbu.math.baobab.model.Event;
import ru.spbu.math.baobab.model.TimeInstant;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.Topic;
import ru.spbu.math.baobab.model.TopicExtent;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Some in-memory test data for exam schedule
 * 
 * @author dbarashev
 */
class TestData {
  private final TimeSlotExtentImpl myTimeSlotExtent = new TimeSlotExtentImpl();
  private final AuditoriumExtentImpl myAuditoriumExtent = new AuditoriumExtentImpl();
  private final AttendeeExtentImpl myAttendeeExtent = new AttendeeExtentImpl();
  private final TopicExtentImpl myTopicExtent = new TopicExtentImpl();
  private final LinkedListMultimap<Attendee, Event> mySchedule = LinkedListMultimap.<Attendee, Event>create();

  TestData() {
    List<TimeSlot> timeSlots = Lists.newArrayList();
    for (int day = 1; day <= 7; day++) {
      timeSlots.add(myTimeSlotExtent.create("экзамен", new TimeInstant(10, 0), new TimeInstant(15, 0), day, EvenOddWeek.ALL));
    }
    
    Auditorium aud01 = myAuditoriumExtent.create("01", 0);
    Auditorium aud04 = myAuditoriumExtent.create("04", 0);
    Auditorium aud2508 = myAuditoriumExtent.create("2508", 0);
    
    Attendee group241 = myAttendeeExtent.create("241", "241", Attendee.Type.ACADEMIC_GROUP);
    Attendee group242 = myAttendeeExtent.create("242", "242", Attendee.Type.ACADEMIC_GROUP);
    Attendee bibikov = myAttendeeExtent.create("Бибиков Ю.Н.", "Бибиков Ю.Н.", Attendee.Type.TEACHER);
    Attendee makarov = myAttendeeExtent.create("Макаров Б.М.", "Макаров Б.М.", Attendee.Type.TEACHER);
    Attendee aliev = myAttendeeExtent.create("Алиев А.А.", "Алиев А.А.", Attendee.Type.TEACHER);
    Attendee zhukov = myAttendeeExtent.create("Жуков И.Б.", "Жуков И.Б.", Attendee.Type.TEACHER);
    
    Topic examCalculus = myTopicExtent.createTopic("матан-03-exam", Topic.Type.EXAM, "Математический анализ");
    examCalculus.addOwner(makarov);
    Event eventCalculus = examCalculus.addEvent(new Date(2013, Calendar.JANUARY, 11), timeSlots.get(4), aud2508);
    eventCalculus.addAttendee(group241);
    mySchedule.put(group241, eventCalculus);
    
    Topic examGeometry = myTopicExtent.createTopic("гит-03-exam", Topic.Type.EXAM, "Геометрия и топология");
    examGeometry.addOwner(bibikov);
    Event eventGeometry = examGeometry.addEvent(new Date(2013, Calendar.JANUARY, 15), timeSlots.get(1), aud04);
    eventGeometry.addAttendee(group241);
    eventGeometry.addAttendee(group242);
    examGeometry.setUrl("http://cs6270.userapi.com/u19639064/docs/235e0eb1ddd4/Voprosy_Geometria_3_Semestr.pdf");
    mySchedule.put(group241, eventGeometry);
    
    Topic examInformatics = myTopicExtent.createTopic("инф-03-exam", Topic.Type.EXAM, "Информатика");
    examInformatics.addOwner(aliev);
    Event eventInformatics = examInformatics.addEvent(new Date(2013, Calendar.JANUARY, 19), timeSlots.get(5), aud01);
    eventInformatics.addAttendee(group241);
    mySchedule.put(group241, eventInformatics);
    
    Topic examAlgebra = myTopicExtent.createTopic("атч-03-exam", Topic.Type.EXAM, "Алгебра и теория чисел");
    examAlgebra.addOwner(zhukov);
    Event eventAlgebra = examAlgebra.addEvent(new Date(2013, Calendar.JANUARY, 24), timeSlots.get(3), aud2508);
    eventAlgebra.addAttendee(group241);
    mySchedule.put(group241, eventAlgebra);
  }
  
  Multimap<Attendee, Event> getExamSchedule() {
    return mySchedule;
  }
  
  TopicExtent getTopicExtent() {
    return myTopicExtent;
  }
}
