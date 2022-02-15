package ua.com.foxminded.university.dao.jpa;

import static org.junit.jupiter.api.Assertions.*;

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
import ua.com.foxminded.university.model.Period;

@SpringJUnitConfig(classes = DataConfigForTesting.class)
@Transactional
class PeriodDaoImplTest {
    private static final String ID_NOT_EXIST = "The %s with id=%d does not exist";

    @Autowired
    private PeriodDaoImpl dao;

    private Period period1 = new Period(1, "period1", LocalTime.of(8, 0), LocalTime.of(9, 30));
    private Period period2 = new Period(2, "period2", LocalTime.of(9, 50), LocalTime.of(11, 20));
    private Period period3 = new Period(3, "period3", LocalTime.of(11, 50), LocalTime.of(13, 20));

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAll() {
        List<Period> expected = new ArrayList<>();
        expected.add(period1);
        expected.add(period2);
        expected.add(period3);
        List<Period> actual = dao.getAll();
        assertEquals(expected, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetById() {
        Period actual1 = dao.getById(period1.getId());
        assertEquals(period1, actual1);
    }
    
    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldThrowExceptionWhenPeriodWithSuchIdNotExist() {
        int id = 10;
        String msg = String.format(ID_NOT_EXIST, Period.class, id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }

    @Test
    void testInsert() {
        Period period = new Period();
        period.setName("period");
        period.setStart(LocalTime.of(11, 20));
        period.setEnd(LocalTime.of(12, 50));
        period = dao.insert(period);
        Period actual = dao.getById(period.getId());
        assertEquals(period, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testUpdate() {
        Period period = new Period(1, "new name", LocalTime.of(11, 20), LocalTime.of(12, 50));
        dao.update(period);
        Period actual = dao.getById(period.getId());
        assertEquals(period, actual);
    }

    @Test
    @Sql(value = { "/insert-data.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void testDelete() {
        int id = 3;
        String msg = String.format(ID_NOT_EXIST, Period.class, id);
        dao.delete(id);        
        DaoException exception = assertThrows(DaoException.class, () -> dao.getById(id));
        assertEquals(msg, exception.getMessage());
    }
}
