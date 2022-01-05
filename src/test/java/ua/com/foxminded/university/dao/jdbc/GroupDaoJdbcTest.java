package ua.com.foxminded.university.dao.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import ua.com.foxminded.university.DataConfigForTesting;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;

@SpringJUnitConfig(classes = DataConfigForTesting.class)
class GroupDaoJdbcTest {
    private static final String ID_NOT_EXIST = "The group with id=%d does not exist";
    
    @Autowired
    private GroupDaoJdbc dao;

    private Group group1 = new Group(1, "group1", new ArrayList<>());
    private Group group2 = new Group(2, "group2", new ArrayList<>());
    private Student student1 = new Student(1, "first_name1", "last_name1", Gender.MAIL, LocalDate.of(2001, 01, 01), group1);
    private Student student2 = new Student(2, "first_name2", "last_name2", Gender.FEMAIL, LocalDate.of(2002, 02, 02), group1);
    private Student student3 = new Student(3, "first_name3", "last_name3", Gender.FEMAIL, LocalDate.of(2003, 03, 03), group2);
    private Student student4 = new Student(4, "first_name4", "last_name4", Gender.FEMAIL, LocalDate.of(2004, 04, 04), group2);

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testGetAll() {
        List<Group> expected = new ArrayList<>();
        expected.add(group1);
        expected.add(group2);
        List<Group> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
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
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void shouldThrowExceptionWhenGroupWithSuchIdNotExist() {
        int id = 10;
        String msg = String.format(ID_NOT_EXIST, id);       
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testGetByName() {
        Group actual1 = dao.getByName(group1.getName());
        Group actual2 = dao.getByName(group2.getName());
        assertAll(
                () -> assertEquals(group1, actual1), 
                () -> assertEquals(group2, actual2)
                );
    }

    @Test
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testInsert() {
        Group group = new Group();
        group.setName("group");
        group = dao.insert(group);
        Group actual = dao.getById(group.getId());
        assertEquals(group, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdate() {
        Group group = new Group(1, "new name");
        dao.update(group);
        Group actual = dao.getById(group.getId());
        assertEquals(group, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testDelete() {
        int id = 1;
        String msg = String.format(ID_NOT_EXIST, id);
        dao.delete(id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testGetByIdWithDetail() {
        group1.getStudents().clear();
        group1.addStudent(student1);
        group1.addStudent(student2);
        group2.getStudents().clear();        
        group2.addStudent(student3);
        group2.addStudent(student4);
        Group actual1 = dao.getByIdWithDetail(group1.getId());
        Group actual2 = dao.getByIdWithDetail(group2.getId());
        assertAll(
                () -> assertEquals(group1, actual1), 
                () -> assertEquals(group1.getStudents(), actual1.getStudents()),
                () -> assertEquals(group2, actual2), 
                () -> assertEquals(group2.getStudents(), actual2.getStudents())
                );
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testGetByCourseId() {
        List<Group> expectedFromCourse1 = new ArrayList<>();
        expectedFromCourse1.add(group1);        
        List<Group> expectedFromCourse2 = new ArrayList<>();
        expectedFromCourse2.add(group2);

        List<Group> actualGroupsFromCourse1 = dao.getByCourseId(1);
        List<Group> actualGroupsFromCourse2 = dao.getByCourseId(2);
        assertAll(
                () -> assertEquals(expectedFromCourse1, actualGroupsFromCourse1),
                () -> assertEquals(expectedFromCourse2, actualGroupsFromCourse2)
                );
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void testUpdateStudents() {
       group1.getStudents().clear();
       group1.addStudent(student3);
       group1.addStudent(student4);
       group2.getStudents().clear();
       group2.addStudent(student1);
       group2.addStudent(student2);
       
       int countUpdate1 = dao.updateStudents(group1);
       int countUpdate2 = dao.updateStudents(group2);
       
       Group actual1 = dao.getByIdWithDetail(group1.getId());
       Group actual2 = dao.getByIdWithDetail(group2.getId());
       assertAll(
               () -> assertEquals(4, countUpdate1), 
               () -> assertEquals(group1, actual1), 
               () -> assertEquals(group1.getStudents(), actual1.getStudents()),
               () -> assertEquals(2, countUpdate2), 
               () -> assertEquals(group2, actual2), 
               () -> assertEquals(group2.getStudents(), actual2.getStudents())
               );       
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void shouldThrowExceptionWhenGroupNameAlreadyExists() {
        String msg = "Cannot create group. " + group1;
        DaoException exception = assertThrows(DaoException.class, () -> dao.insert(group1));
        assertEquals(msg, exception.getMessage());
    }    
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = { "/remove-data.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void shouldThrowExceptionWhenGroupNameAlreadyExistsUpdate() {
        Group groupWithNewName = new Group(1, "group2", new ArrayList<>());
        String msg = "Cannot update group. " + groupWithNewName;
        DaoException exception = assertThrows(DaoException.class, () -> dao.update(groupWithNewName));
        assertEquals(msg, exception.getMessage());
    }
}
