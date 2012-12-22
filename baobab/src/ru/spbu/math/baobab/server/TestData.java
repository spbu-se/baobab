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
    Attendee group243 = myAttendeeExtent.create("243", "243", Attendee.Type.ACADEMIC_GROUP);
    Attendee group244 = myAttendeeExtent.create("244", "244", Attendee.Type.ACADEMIC_GROUP);
    Attendee group245 = myAttendeeExtent.create("245", "245", Attendee.Type.ACADEMIC_GROUP);
    
    Attendee bibikov = myAttendeeExtent.create("Бибиков Ю.Н.", "Бибиков Ю.Н.", Attendee.Type.TEACHER);
    Attendee makarov = myAttendeeExtent.create("Макаров Б.М.", "Макаров Б.М.", Attendee.Type.TEACHER);
    Attendee aliev = myAttendeeExtent.create("Алиев А.А.", "Алиев А.А.", Attendee.Type.TEACHER);
    Attendee zhukov = myAttendeeExtent.create("Жуков И.Б.", "Жуков И.Б.", Attendee.Type.TEACHER);
    
    Topic examCalculus = myTopicExtent.createTopic("матан-03-exam", Topic.Type.EXAM, "Математический анализ");
    examCalculus.addOwner(makarov);
    
    Topic examGeometry = myTopicExtent.createTopic("гит-03-exam", Topic.Type.EXAM, "Геометрия и топология");
    examGeometry.addOwner(bibikov);
    examGeometry.setUrl("http://cs6270.userapi.com/u19639064/docs/235e0eb1ddd4/Voprosy_Geometria_3_Semestr.pdf");
 
    Topic examInformatics = myTopicExtent.createTopic("инф-03-exam", Topic.Type.EXAM, "Информатика");
    examInformatics.addOwner(aliev);

    Topic examAlgebra = myTopicExtent.createTopic("атч-03-exam", Topic.Type.EXAM, "Алгебра и теория чисел");
    examAlgebra.addOwner(zhukov);

    {
      Event eventCalculus = examCalculus.addEvent(new Date(2013, Calendar.JANUARY, 11), timeSlots.get(4), aud2508);
      eventCalculus.addAttendee(group241);
      mySchedule.put(group241, eventCalculus);
      mySchedule.put(group243, eventCalculus);
      mySchedule.put(group245, eventCalculus);
      
      Event eventGeometry = examGeometry.addEvent(new Date(2013, Calendar.JANUARY, 15), timeSlots.get(1), aud04);
      eventGeometry.addAttendee(group241);
      mySchedule.put(group241, eventGeometry);
      mySchedule.put(group243, eventGeometry);
      mySchedule.put(group245, eventGeometry);

      Event eventInformatics = examInformatics.addEvent(new Date(2013, Calendar.JANUARY, 19), timeSlots.get(5), aud01);
      eventInformatics.addAttendee(group241);
      mySchedule.put(group241, eventInformatics);
      mySchedule.put(group243, eventInformatics);
      mySchedule.put(group245, eventInformatics);

      Event eventAlgebra = examAlgebra.addEvent(new Date(2013, Calendar.JANUARY, 24), timeSlots.get(3), aud2508);
      eventAlgebra.addAttendee(group241);
      mySchedule.put(group241, eventAlgebra);
      mySchedule.put(group243, eventAlgebra);
      mySchedule.put(group245, eventAlgebra);
    }    

    {
      Event eventCalculus = examCalculus.addEvent(new Date(2013, Calendar.JANUARY, 10), timeSlots.get(3), aud2508);
      eventCalculus.addAttendee(group242);
      mySchedule.put(group242, eventCalculus);
      mySchedule.put(group244, eventCalculus);
      
      Event eventGeometry = examGeometry.addEvent(new Date(2013, Calendar.JANUARY, 14), timeSlots.get(0), aud04);
      eventGeometry.addAttendee(group242);
      mySchedule.put(group242, eventGeometry);
      mySchedule.put(group244, eventGeometry);
      
      Event eventInformatics = examInformatics.addEvent(new Date(2013, Calendar.JANUARY, 18), timeSlots.get(4), aud01);
      eventInformatics.addAttendee(group242);
      mySchedule.put(group242, eventInformatics);
      mySchedule.put(group244, eventInformatics);

      Event eventAlgebra = examAlgebra.addEvent(new Date(2013, Calendar.JANUARY, 23), timeSlots.get(2), aud2508);
      eventAlgebra.addAttendee(group242);
      mySchedule.put(group242, eventAlgebra);
      mySchedule.put(group244, eventAlgebra);
    }    

  }
  
  Multimap<Attendee, Event> getExamSchedule() {
    return mySchedule;
  }
  
  TopicExtent getTopicExtent() {
    return myTopicExtent;
  }
}
