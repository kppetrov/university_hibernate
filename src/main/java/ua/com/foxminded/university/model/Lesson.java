package ua.com.foxminded.university.model;

import java.time.LocalDate;

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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    
    @Column(name = "date")
    private LocalDate date;
    
    @ManyToOne
    @JoinColumn(name = "period_id")
    private Period period;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;
}
