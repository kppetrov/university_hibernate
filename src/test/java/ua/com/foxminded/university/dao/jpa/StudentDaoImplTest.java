package ua.com.foxminded.university.dao.jpa;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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
import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;

@SpringJUnitConfig(classes = DataConfigForTesting.class)
@Transactional
class StudentDaoImplTest {
    private static final String ID_NOT_EXIST = "The %s with id=%d does not exist";

    @Autowired
    private StudentDaoImpl dao;
    
    private Group group1 = new Group(1, "group1");
    private Group group2 = new Group(2, "group2");
    private Student student1 = new Student(1, "first_name1", "last_name1", Gender.MAIL, LocalDate.of(2001, 01, 01), group1);
    private Student student2 = new Student(2, "first_name2", "last_name2", Gender.FEMAIL, LocalDate.of(2002, 02, 02), group1);
    private Student student3 = new Student(3, "first_name3", "last_name3", Gender.FEMAIL, LocalDate.of(2003, 03, 03), group2);
    private Student student4 = new Student(4, "first_name4", "last_name4", Gender.FEMAIL, LocalDate.of(2004, 04, 04), group2);

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAll() {
        List<Student> expected = new ArrayList<>();
        expected.add(student1);
        expected.add(student2);
        expected.add(student3);
        expected.add(student4);
        List<Student> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetById() {
        Student actual1 = dao.getById(student1.getId());
        assertEquals(student1, actual1);
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldThrowExceptionWhenStudentWithSuchIdNotExist() {
        int id = 10;
        String msg = String.format(ID_NOT_EXIST, Student.class, id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testInsert() {
        Student student = new Student();
        student.setFirstName("first_name");
        student.setLastName("last_name");
        student.setGender(Gender.FEMAIL);
        student.setBirthdate(LocalDate.of(1980, 01, 01));
        student.setGroup(group1);
        Student newStudent = dao.insert(student);
        Student actual = dao.getById(newStudent.getId());
        assertAll(
                () -> assertEquals(newStudent, actual),
                () -> assertEquals(newStudent.getGroup(), actual.getGroup())
                );
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testUpdate() {
        Student student = new Student(1, "new first_name", "new last_name", Gender.MAIL, LocalDate.of(1980, 01, 01), group2);
        dao.update(student);
        Student actual = dao.getById(student.getId());
        assertAll(
                () -> assertEquals(student, actual),
                () -> assertEquals(student.getGroup(), actual.getGroup())
                );
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testDelete() {
        int id = 1;
        String msg = String.format(ID_NOT_EXIST, Student.class, id);
        dao.delete(id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }
}
