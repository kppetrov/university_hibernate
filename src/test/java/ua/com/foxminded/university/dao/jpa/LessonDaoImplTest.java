package ua.com.foxminded.university.dao.jpa;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import ua.com.foxminded.university.DataConfigForTesting;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.model.Course;
import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Lesson;
import ua.com.foxminded.university.model.Period;
import ua.com.foxminded.university.model.Teacher;

@SpringJUnitConfig(classes = DataConfigForTesting.class)
@Transactional
class LessonDaoImplTest {
    private static final String ID_NOT_EXIST = "The %s with id=%d does not exist";

    @Autowired
    private LessonDaoImpl dao;

    private Course course1 = new Course(1, "course1");
    private Course course2 = new Course(2, "course2");
    private Teacher teacher1 = new Teacher(1, "first_name1", "last_name1", Gender.MAIL, LocalDate.of(1971, 01, 01));
    private Teacher teacher2 = new Teacher(2, "first_name2", "last_name2", Gender.FEMAIL, LocalDate.of(1972, 02, 02));
    private Period period1 = new Period(1, "period1", LocalTime.of(8, 0), LocalTime.of(9, 30));
    private Period period2 = new Period(2, "period2", LocalTime.of(9, 50), LocalTime.of(11, 20));
    private Classroom classroom1 = new Classroom(1, "classroom1");
    private Classroom classroom2 = new Classroom(2, "classroom2");
    private Lesson lesson1 = new Lesson(1, course1, LocalDate.of(2021, 01, 01), period1, teacher1, classroom1);
    private Lesson lesson2 = new Lesson(2, course1, LocalDate.of(2021, 01, 01), period2, teacher1, classroom1);
    private Lesson lesson3 = new Lesson(3, course2, LocalDate.of(2021, 01, 01), period1, teacher2, classroom2);
    private Lesson lesson4 = new Lesson(4, course2, LocalDate.of(2021, 01, 01), period2, teacher2, classroom2);
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAll() {
        List<Lesson> expected = new ArrayList<>();
        expected.add(lesson1);
        expected.add(lesson2);
        expected.add(lesson3);
        expected.add(lesson4);
        List<Lesson> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetById() {
        Lesson actual1 = dao.getById(lesson1.getId());
        Lesson actual2 = dao.getById(lesson2.getId());
        Lesson actual3 = dao.getById(lesson3.getId());
        Lesson actual4 = dao.getById(lesson4.getId());
        assertAll(
                () -> assertEquals(lesson1, actual1), 
                () -> assertEquals(lesson2, actual2),
                () -> assertEquals(lesson3, actual3),
                () -> assertEquals(lesson4, actual4)
                );
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldThrowExceptionWhenLessonWithSuchIdNotExist() {
        int id = 10;
        String msg = String.format(ID_NOT_EXIST, Lesson.class, id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testInsert() {
        Lesson lesson = new Lesson();
        lesson.setCourse(course1);
        lesson.setClassroom(classroom1);
        lesson.setTeacher(teacher1);
        lesson.setDate(LocalDate.of(2021, 02, 02));
        lesson.setPeriod(period1);
        lesson = dao.insert(lesson);
        Lesson actual = dao.getById(lesson.getId());
        assertEquals(lesson, actual);
    }

    @Test
    void testUpdate() {
        Lesson lesson = new Lesson(1, course1, LocalDate.of(2021, 01, 03), period2, teacher1, classroom2);
        dao.update(lesson);
        Lesson actual = dao.getById(lesson.getId());
        assertEquals(lesson, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testDelete() {     
        int id = 1;
        String msg = String.format(ID_NOT_EXIST, Lesson.class, id);
        dao.delete(id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetByDatePeriodIdTeacherId() {
        Optional<Lesson> actual1 = dao.getByDatePeriodIdTeacherId(LocalDate.of(2021, 01, 01), period1.getId(), teacher1.getId());
        Optional<Lesson> actual2 = dao.getByDatePeriodIdTeacherId(LocalDate.of(2021, 01, 01), period2.getId(), teacher1.getId());
        Optional<Lesson> actual3 = dao.getByDatePeriodIdTeacherId(LocalDate.of(2021, 01, 01), period1.getId(), teacher2.getId());
        Optional<Lesson> actual4 = dao.getByDatePeriodIdTeacherId(LocalDate.of(2021, 01, 01), period2.getId(), teacher2.getId());
        assertAll(
                () -> assertEquals(lesson1, actual1.get()), 
                () -> assertEquals(lesson2, actual2.get()),
                () -> assertEquals(lesson3, actual3.get()),
                () -> assertEquals(lesson4, actual4.get())
                );
    } 
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetByDatePeriodIdClassroomId() {
        Optional<Lesson> actual1 = dao.getByDatePeriodIdClassroomId(LocalDate.of(2021, 01, 01), period1.getId(), classroom1.getId());
        Optional<Lesson> actual2 = dao.getByDatePeriodIdClassroomId(LocalDate.of(2021, 01, 01), period2.getId(), classroom1.getId());
        Optional<Lesson> actual3 = dao.getByDatePeriodIdClassroomId(LocalDate.of(2021, 01, 01), period1.getId(), classroom2.getId());
        Optional<Lesson> actual4 = dao.getByDatePeriodIdClassroomId(LocalDate.of(2021, 01, 01), period2.getId(), classroom2.getId());
        assertAll(
                () -> assertEquals(lesson1, actual1.get()), 
                () -> assertEquals(lesson2, actual2.get()),
                () -> assertEquals(lesson3, actual3.get()),
                () -> assertEquals(lesson4, actual4.get())
                );
    }
}
