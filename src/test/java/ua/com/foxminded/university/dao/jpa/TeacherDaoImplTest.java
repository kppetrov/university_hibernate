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
import ua.com.foxminded.university.model.Teacher;

@SpringJUnitConfig(classes = DataConfigForTesting.class)
@Transactional
class TeacherDaoImplTest {
    private static final String ID_NOT_EXIST = "The %s with id=%d does not exist";

    @Autowired
    private TeacherDaoImpl dao;
    
    private Teacher teacher1 = new Teacher(1L, "first_name1", "last_name1", Gender.MAIL, LocalDate.of(1971, 01, 01));
    private Teacher teacher2 = new Teacher(2L, "first_name2", "last_name2", Gender.FEMAIL, LocalDate.of(1972, 02, 02));  
    private Teacher teacher3 = new Teacher(3L, "first_name3", "last_name3", Gender.FEMAIL, LocalDate.of(1973, 03, 03));  

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAll() {
        List<Teacher> expected = new ArrayList<>();
        expected.add(teacher1);
        expected.add(teacher2);
        expected.add(teacher3);
        List<Teacher> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetById() {
        Teacher actual1 = dao.getById(teacher1.getId());
        assertEquals(teacher1, actual1);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldThrowExceptionWhenTeacherWithSuchIdNotExist() {
        Long id = 10L;
        String msg = String.format(ID_NOT_EXIST, Teacher.class, id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }
    
    @Test
    void testInsert() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("first_name");
        teacher.setLastName("last_name");
        teacher.setGender(Gender.FEMAIL);
        teacher.setBirthdate(LocalDate.of(1980, 01, 01));
        teacher = dao.insert(teacher);
        Teacher actual = dao.getById(teacher.getId());
        assertEquals(teacher, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testUpdate() {
        Teacher teacher = new Teacher(1L, "new first_name", "new last_name", Gender.MAIL, LocalDate.of(1980, 01, 01));
        dao.update(teacher);
        Teacher actual = dao.getById(teacher.getId());
        assertEquals(teacher, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testDelete() {
        Long id = 3L;
        String msg = String.format(ID_NOT_EXIST, Teacher.class, id);
        dao.delete(id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

}
