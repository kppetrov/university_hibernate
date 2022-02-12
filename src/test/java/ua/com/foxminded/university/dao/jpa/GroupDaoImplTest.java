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
import ua.com.foxminded.university.model.Group;

@SpringJUnitConfig(classes = DataConfigForTesting.class)
@Transactional
class GroupDaoImplTest {
    private static final String ID_NOT_EXIST = "The %s with id=%d does not exist";
    
    @Autowired
    private GroupDaoImpl dao;

    private Group group1 = new Group(1, "group1");
    private Group group2 = new Group(2, "group2");
    private Group group3 = new Group(3, "group3");

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAll() {
        List<Group> expected = new ArrayList<>();
        expected.add(group1);
        expected.add(group2);
        expected.add(group3);
        List<Group> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetById() {
        Group actual1 = dao.getById(group1.getId());
        Group actual2 = dao.getById(group2.getId());
        assertAll(
                () -> assertEquals(group1, actual1), 
                () -> assertEquals(group2, actual2)
                );
    }    
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldThrowExceptionWhenGroupWithSuchIdNotExist() {
        int id = 10;
        String msg = String.format(ID_NOT_EXIST, Group.class, id);       
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }
    
    @Test
    void testInsert() {
        Group group = new Group();
        group.setName("group");
        group = dao.insert(group);
        Group actual = dao.getById(group.getId());
        assertEquals(group, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testUpdate() {
        Group group = new Group(1, "new name");
        dao.update(group);
        Group actual = dao.getById(group.getId());
        assertEquals(group, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testDelete() {
        int id = 3;
        String msg = String.format(ID_NOT_EXIST, Group.class, id);
        dao.delete(id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldThrowExceptionWhenGroupNameAlreadyExists() {
        String msg = String.format("Cannot create %s.", group1);
        DaoException exception = assertThrows(DaoException.class, () -> dao.insert(group1));
        assertEquals(msg, exception.getMessage());
    }    
}
