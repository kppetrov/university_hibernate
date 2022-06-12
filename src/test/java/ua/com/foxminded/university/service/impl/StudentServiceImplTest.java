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

import ua.com.foxminded.university.dao.StudentDao;
import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {
    @Mock
    private StudentDao studentDao;
    @InjectMocks
    private StudentServiceImpl service;

    private Student student = new Student(1L, "first_name", "last_name", Gender.MAIL, LocalDate.of(2001, 01, 01),
            new Group(1L, "group1"));

    @Test
    void testGetAll() {
        List<Student> students = new ArrayList<>();
        students.add(student);
        when(studentDao.getAll()).thenReturn(students);
        List<Student> actual = service.getAll();
        verify(studentDao, times(1)).getAll();
        assertEquals(students, actual);
    }

    @Test
    void testGetById() {
        when(studentDao.getById(student.getId())).thenReturn(student);
        Student actual = service.getById(student.getId());
        verify(studentDao, times(1)).getById(student.getId());
        assertEquals(student, actual);
    }

    @Test
    void testInsert() {
        service.insert(student);
        verify(studentDao, times(1)).insert(student);
    }

    @Test
    void testUpdate() {
        service.update(student);
        verify(studentDao, times(1)).update(student);
    }

    @Test
    void testDelete() {
        service.delete(student.getId());
        verify(studentDao, times(1)).delete(student.getId());
    }
}
