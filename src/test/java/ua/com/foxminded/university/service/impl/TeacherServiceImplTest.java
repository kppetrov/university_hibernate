package ua.com.foxminded.university.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.TeacherDao;
import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Teacher;

@ExtendWith(MockitoExtension.class)
class TeacherServiceImplTest {
    @Mock
    private TeacherDao teacherDao;
    @InjectMocks
    private TeacherServiceImpl service;

    private Teacher teacher = new Teacher(1L, "first_name", "last_name", Gender.MAIL, LocalDate.of(1971, 01, 01));

    @Test
    void testGetAll() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(teacher);
        when(teacherDao.getAll()).thenReturn(teachers);
        List<Teacher> actual = service.getAll();
        verify(teacherDao, times(1)).getAll();
        assertEquals(teachers, actual);
    }

    @Test
    void testGetById() {
        when(teacherDao.getById(teacher.getId())).thenReturn(teacher);
        Teacher actual = service.getById(teacher.getId());
        verify(teacherDao, times(1)).getById(teacher.getId());
        assertEquals(teacher, actual);
    }

    @Test
    void testInsert() {
        service.insert(teacher);
        verify(teacherDao, times(1)).insert(teacher);
    }

    @Test
    void testUpdate() {
        service.update(teacher);
        verify(teacherDao, times(1)).update(teacher);
    }

    @Test
    void testDelete() {
        service.delete(teacher.getId());
        verify(teacherDao, times(1)).delete(teacher.getId());
    }
}
