package ua.com.foxminded.university.dao.jdbc;

import static org.junit.jupiter.api.Assertions.*;

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

@SpringJUnitConfig(classes = DataConfigForTesting.class)
class ClassroomDaoJdbcTest {
    private static final String ID_NOT_EXIST = "The classroom with id=%d does not exist";
    
    @Autowired
    private ClassroomDaoJdbc dao;
    
    private Classroom classroom1 = new Classroom(1, "classroom1");
    private Classroom classroom2 = new Classroom(2, "classroom2");    

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testGetAll() {
        List<Classroom> expected = new ArrayList<>();
        expected.add(classroom1);
        expected.add(classroom2);
        List<Classroom> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testGetById() {
        Classroom actual1 = dao.getById(classroom1.getId());
        Classroom actual2 = dao.getById(classroom2.getId());
        assertAll(
                () -> assertEquals(classroom1, actual1), 
                () -> assertEquals(classroom2, actual2)
                );
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void shouldThrowExceptionWhenClassroomWithSuchIdNotExist() {
        int id = 10;
        String msg = String.format(ID_NOT_EXIST, id);
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testInsert() {
        Classroom classroom = new Classroom();
        classroom.setName("classroom");
        classroom = dao.insert(classroom);
        Classroom actual = dao.getById(classroom.getId());
        assertEquals(classroom, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdate() {
        Classroom classroom = new Classroom(1, "new name");
        dao.update(classroom);
        Classroom actual = dao.getById(classroom.getId());
        assertEquals(classroom, actual);
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
}
