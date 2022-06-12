package ua.com.foxminded.university.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.ClassroomDao;
import ua.com.foxminded.university.model.Classroom;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceImplTest {
    @Mock
    private ClassroomDao classroomDao;
    @InjectMocks
    private ClassroomServiceImpl service;
    
    private Classroom classroom = new Classroom(1L, "classroom");

    @Test
    void testGetAll() {
        List<Classroom> classrooms = new ArrayList<>();
        classrooms.add(classroom);
        when(classroomDao.getAll()).thenReturn(classrooms);        
        List<Classroom> actual = service.getAll();        
        verify(classroomDao, times(1)).getAll();
        assertEquals(classrooms, actual);
    }

    @Test
    void testGetById() {
        when(classroomDao.getById(classroom.getId())).thenReturn(classroom); 
        Classroom actual = service.getById(classroom.getId());
        verify(classroomDao, times(1)).getById(classroom.getId());
        assertEquals(classroom, actual);
    }

    @Test
    void testInsert() {
        service.insert(classroom);
        verify(classroomDao, times(1)).insert(classroom);
    }

    @Test
    void testUpdate() {
        service.update(classroom);
        verify(classroomDao, times(1)).update(classroom);
    }

    @Test
    void testDelete() {
        service.delete(classroom.getId());
        verify(classroomDao, times(1)).delete(classroom.getId());
    }
}
