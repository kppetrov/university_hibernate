package ua.com.foxminded.university.dao.jpa;

import static org.junit.jupiter.api.Assertions.*;

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

@SpringJUnitConfig(classes = DataConfigForTesting.class)
@Transactional
class ClassroomDaoImplTest {
    private static final String ID_NOT_EXIST = "The %s with id=%d does not exist";

    @Autowired
    private ClassroomDaoImpl dao;

    private Classroom classroom1 = new Classroom(1, "classroom1");
    private Classroom classroom2 = new Classroom(2, "classroom2");
    private Classroom classroom3 = new Classroom(3, "classroom3");

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAll() {
        List<Classroom> expected = new ArrayList<>();
        expected.add(classroom1);
        expected.add(classroom2);
        expected.add(classroom3);
        List<Classroom> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
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
    void shouldThrowExceptionWhenClassroomWithSuchIdNotExist() {
        int id = 10;
        String msg = String.format(ID_NOT_EXIST, Classroom.class, id);
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    void testInsert() {
        Classroom classroom = new Classroom();
        classroom.setName("classroom");
        classroom = dao.insert(classroom);
        Classroom actual = dao.getById(classroom.getId());
        assertEquals(classroom.getName(), actual.getName());
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testUpdate() {
        Classroom classroom = new Classroom(1, "new name");
        dao.update(classroom);
        Classroom actual = dao.getById(classroom.getId());
        assertEquals(classroom, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testDelete() {
        int id = 3;
        dao.delete(id);
        String msg = String.format(ID_NOT_EXIST, Classroom.class, id);
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }
}
