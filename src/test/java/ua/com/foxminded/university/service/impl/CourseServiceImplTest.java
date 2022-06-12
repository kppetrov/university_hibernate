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

import ua.com.foxminded.university.dao.CourseDao;
import ua.com.foxminded.university.model.Course;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {
    @Mock
    private CourseDao courseDao;
    @InjectMocks
    private CourseServiceImpl service;

    private Course course = new Course(1L, "course");    
    
    @Test
    void testGetAll() {
        List<Course> courses = new ArrayList<>();
        courses.add(course);
        when(courseDao.getAll()).thenReturn(courses);
        List<Course> actual = service.getAll();
        verify(courseDao, times(1)).getAll();
        assertEquals(courses, actual);
    }

    @Test
    void testGetById() {
        when(courseDao.getById(course.getId())).thenReturn(course);
        Course actual = service.getById(course.getId());
        verify(courseDao, times(1)).getById(course.getId());
        assertEquals(course, actual);
    }

    @Test
    void testInsert() {
        service.insert(course);
        verify(courseDao, times(1)).insert(course);
    }

    @Test
    void testUpdate() {
        service.update(course);
        verify(courseDao, times(1)).update(course);
    }

    @Test
    void testDelete() {
        service.delete(course.getId());
        verify(courseDao, times(1)).delete(course.getId());
    }
    

    @Test
    void testGetByIdWithDetail() {       
        when(courseDao.getByIdWithDetail(course.getId())).thenReturn(course);
        Course actual = service.getByIdWithDetail(course.getId());
        verify(courseDao, times(1)).getByIdWithDetail(course.getId());
        assertEquals(course, actual);
    }

    @Test
    void testUpdateGroups() {
        service.updateGroups(course);
        verify(courseDao, times(1)).updateGroups(course); 
    }
}
