package ua.com.foxminded.university.dao.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import ua.com.foxminded.university.DataConfigForTesting;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.model.Course;
import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Lesson;
import ua.com.foxminded.university.model.Period;
import ua.com.foxminded.university.model.Teacher;

@SpringJUnitConfig(classes = DataConfigForTesting.class)
class CourseDaoJdbcTest {
    private static final String ID_NOT_EXIST = "The course with id=%d does not exist";
    
    @Autowired
    private CourseDaoJdbc dao; 

    private Course course1 = new Course(1, "course1", new ArrayList<>(), new ArrayList<>());
    private Course course2 = new Course(2, "course2", new ArrayList<>(), new ArrayList<>());
    private Group group1 = new Group(1, "group1");
    private Group group2 = new Group(2, "group2");
    private Teacher teacher1 = new Teacher(1, "first_name1", "last_name1", Gender.MAIL, LocalDate.of(1971, 01, 01));
    private Teacher teacher2 = new Teacher(2, "first_name2", "last_name2", Gender.FEMAIL, LocalDate.of(1972, 02, 02));
    private Period period1 = new Period(1, "period1", LocalTime.of(8, 0), LocalTime.of(9, 30));
    private Period period2 = new Period(2, "period2", LocalTime.of(9, 40), LocalTime.of(11, 10));
    private Classroom classroom1 = new Classroom(1, "classroom1");
    private Classroom classroom2 = new Classroom(2, "classroom2");
    private Lesson lesson1 = new Lesson(1, course1, LocalDate.of(2021, 01, 01), period1, teacher1, classroom1);
    private Lesson lesson2 = new Lesson(2, course1, LocalDate.of(2021, 01, 01), period2, teacher1, classroom1);
    private Lesson lesson3 = new Lesson(3, course2, LocalDate.of(2021, 01, 01), period1, teacher2, classroom2);
    private Lesson lesson4 = new Lesson(4, course2, LocalDate.of(2021, 01, 01), period2, teacher2, classroom2);

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testGetAll() {
        List<Course> expected = new ArrayList<>();
        expected.add(course1);
        expected.add(course2);
        
        List<Course> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testGetById() {
        Course actual1 = dao.getById(course1.getId());
        Course actual2 = dao.getById(course2.getId());
        assertAll(
                () -> assertEquals(course1, actual1), 
                () -> assertEquals(course2, actual2)
                );
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void shouldThrowExceptionWhenCourseWithSuchIdNotExist() {
        int id = 10;
        String msg = String.format(ID_NOT_EXIST, id);       
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testInsert() {
        Course course = new Course();
        course.setName("course");
        
        course = dao.insert(course);
        Course actual = dao.getById(course.getId());
        assertEquals(course, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdate() {
        Course course = new Course(1, "new name");        
        dao.update(course);
        Course actual = dao.getById(course.getId());
        assertEquals(course, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testDelete() {
        int id = 1;
        dao.delete(id);
        String msg = String.format(ID_NOT_EXIST, id);       
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testGetByIdWithDetail() {       
        course1.getGroups().clear();
        course1.addGroup(group1);        
        course2.getGroups().clear();
        course2.addGroup(group2);        
        course1.getLessons().clear();
        course1.addLesson(lesson1);
        course1.addLesson(lesson2);    
        course2.getLessons().clear();
        course2.addLesson(lesson3);
        course2.addLesson(lesson4);        

        Course actual1 = dao.getByIdWithDetail(course1.getId());
        Course actual2 = dao.getByIdWithDetail(course2.getId());
        assertAll(
                () -> assertEquals(course1, actual1), 
                () -> assertEquals(course1.getGroups(), actual1.getGroups()),
                () -> assertEquals(course1.getLessons(), actual1.getLessons()),
                () -> assertEquals(course2, actual2), 
                () -> assertEquals(course2.getGroups(), actual2.getGroups()),
                () -> assertEquals(course2.getLessons(), actual2.getLessons())
                );
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateGroups() {
        course1.getGroups().clear();
        course1.addGroup(group2);     
        course2.getGroups().clear();
        course2.addGroup(group1);
        
       int countUpdate1 = dao.updateGroups(course1);
       int countUpdate2 = dao.updateGroups(course2);       
       Course actual1 = dao.getByIdWithDetail(course1.getId());
       Course actual2 = dao.getByIdWithDetail(course2.getId());
       assertAll(
               () -> assertEquals(2, countUpdate1), 
               () -> assertEquals(course1, actual1), 
               () -> assertEquals(course1.getGroups(), actual1.getGroups()),
               () -> assertEquals(2, countUpdate2), 
               () -> assertEquals(course2, actual2), 
               () -> assertEquals(course2.getGroups(), actual2.getGroups())
               );       
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateGroupsInAbsenceGroups() {
        course1.getGroups().clear(); 
        course2.getGroups().clear();
        
       int countUpdate1 = dao.updateGroups(course1);
       int countUpdate2 = dao.updateGroups(course2);       
       Course actual1 = dao.getByIdWithDetail(course1.getId());
       Course actual2 = dao.getByIdWithDetail(course2.getId());
       assertAll(
               () -> assertEquals(1, countUpdate1), 
               () -> assertEquals(course1, actual1), 
               () -> assertEquals(course1.getGroups(), actual1.getGroups()),
               () -> assertEquals(1, countUpdate2), 
               () -> assertEquals(course2, actual2), 
               () -> assertEquals(course2.getGroups(), actual2.getGroups())
               );       
    }
}
