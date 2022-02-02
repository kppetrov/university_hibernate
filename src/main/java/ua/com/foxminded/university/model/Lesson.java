package ua.com.foxminded.university.model;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "lessons", uniqueConstraints = { 
        @UniqueConstraint(columnNames = { "date", "period_id", "classroom_id" }),
        @UniqueConstraint(columnNames = { "date", "period_id", "teacher_id" }) 
        }) 
@NamedQuery(name=Lesson.FIND_LESSONS_BY_DATE_ADN_PERIOD_ID_AND_TEACHER_ID,
            query="select distinct l from Lesson l " +
                    "where l.date = :date " + 
                    "and l.period.id = :periodId " + 
                    "and l.teacher.id = :teacherId")
@NamedQuery(name=Lesson.FIND_LESSONS_BY_DATE_ADN_PERIOD_ID_AND_CLASSROOM_ID,
            query="select distinct l from Lesson l " +
                    "where l.date = :date " + 
                    "and l.period.id = :periodId " + 
                    "and l.classroom.id = :classroomId")
public class Lesson {    

    public static final String FIND_LESSONS_BY_DATE_ADN_PERIOD_ID_AND_TEACHER_ID = "Lesson.getByDatePeriodIdTeacherId";
    public static final String FIND_LESSONS_BY_DATE_ADN_PERIOD_ID_AND_CLASSROOM_ID = "Lesson.getByDatePeriodIdClassroomId";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    
    @Column(name = "date")
    private LocalDate date;
    
    @ManyToOne
    @JoinColumn(name = "period_id")
    private Period period;
    
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    public Lesson() {
    }

    public Lesson(int id, Course course, LocalDate date, Period period, Teacher teacher, Classroom classroom) {
        this.id = id;
        this.course = course;
        this.date = date;
        this.period = period;
        this.teacher = teacher;
        this.classroom = classroom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }
    
    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classroom, course, date, id, period, teacher);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Lesson other = (Lesson) obj;
        return Objects.equals(classroom, other.classroom) && Objects.equals(course, other.course)
                && Objects.equals(date, other.date) && id == other.id && Objects.equals(period, other.period)
                && Objects.equals(teacher, other.teacher);
    }

    @Override
    public String toString() {
        return "Lesson [id=" + id + ", course=" + course + ", date=" + date + ", period=" + period + ", classroom="
                + classroom + ", teacher=" + teacher + "]";
    } 
}
