/**
 * 
 */
package ua.com.foxminded.university.dao.jpa;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Lesson;
import ua.com.foxminded.university.model.Period;
import ua.com.foxminded.university.model.Teacher;

@SpringJUnitConfig(classes = DataConfigForTesting.class)
@Transactional
class CourseDaoImplTest {
    private static final String ID_NOT_EXIST = "The %s with id=%d does not exist";
    
    @Autowired
    private CourseDaoImpl dao; 

    private Course course1 = new Course(1, "course1");
    private Course course2 = new Course(2, "course2");
    private Course course3 = new Course(3, "course3");
    private Group group1 = new Group(1, "group1");
    private Teacher teacher1 = new Teacher(1, "first_name1", "last_name1", Gender.MAIL, LocalDate.of(1971, 01, 01));
    private Period period1 = new Period(1, "period1", LocalTime.of(8, 0), LocalTime.of(9, 30));
    private Period period2 = new Period(2, "period2", LocalTime.of(9, 50), LocalTime.of(11, 20));
    private Classroom classroom1 = new Classroom(1, "classroom1");
    private Lesson lesson1 = new Lesson(1, course1, LocalDate.of(2021, 01, 01), period1, teacher1, classroom1);
    private Lesson lesson2 = new Lesson(2, course1, LocalDate.of(2021, 01, 01), period2, teacher1, classroom1);

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAll() {
        List<Course> expected = new ArrayList<>();
        expected.add(course1);
        expected.add(course2);
        expected.add(course3);        
        List<Course> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetById() {
        Course actual1 = dao.getById(course1.getId());
        assertEquals(course1, actual1);
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldThrowExceptionWhenCourseWithSuchIdNotExist() {
        int id = 10;
        String msg = String.format(ID_NOT_EXIST, Course.class, id);       
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    void testInsert() {
        Course course = new Course();
        course.setName("course");        
        course = dao.insert(course);
        Course actual = dao.getById(course.getId());
        assertEquals(course, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testUpdate() {
        Course course = new Course(1, "new name");        
        dao.update(course);
        Course actual = dao.getById(course.getId());
        assertEquals(course, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testDelete() {
        int id = 3;
        dao.delete(id);
        String msg = String.format(ID_NOT_EXIST, Course.class, id);       
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetByIdWithDetail() {       
        course1.getGroups().clear();
        course1.addGroup(group1);     
        course1.getLessons().clear();
        course1.addLesson(lesson1);
        course1.addLesson(lesson2); 

        Course actual1 = dao.getByIdWithDetail(course1.getId());
        assertAll(
                () -> assertEquals(course1, actual1), 
                () -> assertEquals(course1.getGroups(), actual1.getGroups()),
                () -> assertEquals(course1.getLessons(), actual1.getLessons())
                );
    }
}
