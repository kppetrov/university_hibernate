package ua.com.foxminded.university.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.PeriodDao;
import ua.com.foxminded.university.model.Period;

@ExtendWith(MockitoExtension.class)
class PeriodServiceImplTest {
    @Mock
    private PeriodDao periodDao;
    @InjectMocks
    private PeriodServiceImpl service;
    
    private Period period = new Period(1L, "period", LocalTime.of(8, 0), LocalTime.of(9, 30));

    @Test
    void testGetAll() {
        List<Period> periods = new ArrayList<>();
        periods.add(period);
        when(periodDao.getAll()).thenReturn(periods);
        List<Period> actual = service.getAll();
        verify(periodDao, times(1)).getAll();
        assertEquals(periods, actual);
    }

    @Test
    void testGetById() {
        when(periodDao.getById(period.getId())).thenReturn(period);
        Period actual = service.getById(period.getId());
        verify(periodDao, times(1)).getById(period.getId());
        assertEquals(period, actual);
    }

    @Test
    void testInsert() {
        service.insert(period);
        verify(periodDao, times(1)).insert(period);
    }

    @Test
    void testUpdate() {
        service.update(period);
        verify(periodDao, times(1)).update(period);
    }

    @Test
    void testDelete() {
        service.delete(period.getId());
        verify(periodDao, times(1)).delete(period.getId());
    }
}
