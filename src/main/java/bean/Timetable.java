package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("timetable")
public class Timetable {
    public List<Period> getPeriods() {
        return periods.periods;
    }

    public List<Daysdef> getDaysdefs() {
        return daysdefs.daysdefs;
    }

    public List<Subject> getSubjects() {
        return subjects.subjects;
    }

    public List<Teacher> getTeachers() {
        return teachers.teachers;
    }

    public List<Class> getClasses() {
        return classes.classes;
    }

    public List<Group> getGroups() {
        return groups.groups;
    }

    public List<Lesson> getLessons() {
        return lessons.lessons;
    }

    public List<Card> getCards() {
        return cards.cards;
    }
    public List<Classroom> getClassrooms() {
        return classrooms.classrooms;
    }

    public List<StudentSubject> getStudentSubjects() {
        return studentsubjects.studentsubjects;
    }
    public List<Student> getStudents() {
        return students.students;
    }

    @XStreamAlias("periods")
    Periods periods;
    @XStreamAlias("daysdefs")
    DaysDefs daysdefs;
    @XStreamAlias("subjects")
    Subjects subjects;
    @XStreamAlias("teachers")
    Teachers teachers;
    @XStreamAlias("classes")
    Classes classes;
    @XStreamAlias("groups")
    Groups groups;
    @XStreamAlias("lessons")
    Lessons lessons;
    @XStreamAlias("cards")
    Cards cards;
    @XStreamAlias("classrooms")
    Classrooms classrooms;
    @XStreamAlias("studentsubjects")
    StudentSubjects studentsubjects;
    @XStreamAlias("students")
    Students students;


    class Periods {
        @XStreamImplicit(itemFieldName = "period")
        List<Period> periods = new ArrayList<Period>();

    }
    class DaysDefs {
        @XStreamImplicit(itemFieldName = "daysdef")
        List<Daysdef> daysdefs = new ArrayList<Daysdef>();
    }
    class Subjects {
        @XStreamImplicit(itemFieldName = "subject")
        List<Subject> subjects = new ArrayList<Subject>();
    }
    class Teachers {
        @XStreamImplicit(itemFieldName = "teacher")
        List<Teacher> teachers = new ArrayList<Teacher>();
    }
    class Classes {
        @XStreamImplicit(itemFieldName = "class")
        List<Class> classes = new ArrayList<Class>();
    }
    class Groups {
        @XStreamImplicit(itemFieldName = "group")
        List<Group> groups = new ArrayList<Group>();
    }
    class Lessons {
        @XStreamImplicit(itemFieldName = "lesson")
        List<Lesson> lessons = new ArrayList<Lesson>();
    }
    class Cards {
        @XStreamImplicit(itemFieldName = "card")
        List<Card> cards = new ArrayList<Card>();
    }
    class Classrooms {
        @XStreamImplicit(itemFieldName = "classroom")
        List<Classroom> classrooms = new ArrayList<Classroom>();
    }
    class StudentSubjects {
        @XStreamImplicit(itemFieldName = "studentsubject")
        List<StudentSubject> studentsubjects = new ArrayList<StudentSubject>();
    }
    class Students {
        @XStreamImplicit(itemFieldName = "student")
        List<Student> students = new ArrayList<Student>();
    }


}
