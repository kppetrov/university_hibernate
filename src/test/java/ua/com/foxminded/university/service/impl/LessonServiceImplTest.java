package ua.com.foxminded.university.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.LessonDao;
import ua.com.foxminded.university.exception.ServiceException;
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.model.Course;
import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Lesson;
import ua.com.foxminded.university.model.Period;
import ua.com.foxminded.university.model.Teacher;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {
    private static final String TEACHER_IS_BUSY = "Cannot create lesson. The teacher is busy at this time.";
    private static final String CLASSROOM_IS_OCCUPIED = "Cannot create lesson. The classroom is occupied at this time.";
    private static final String TEACHER_IS_BUSY_UPDATE = "Cannot update lesson. The teacher is busy at this time.";
    private static final String CLASSROOM_IS_OCCUPIED_UPDATE = "Cannot update lesson. The classroom is occupied at this time.";

    @Mock
    private LessonDao lessonDao;
    @InjectMocks
    private LessonServiceImpl service;

    private Course course = new Course(1, "course");
    private Teacher teacher = new Teacher(1, "first_name", "last_name", Gender.MAIL, LocalDate.of(1971, 01, 01));
    private Period period = new Period(1, "period", LocalTime.of(8, 0), LocalTime.of(9, 30));
    private Classroom classroom = new Classroom(1, "classroom");
    private Lesson lesson = new Lesson(1, course, LocalDate.of(2021, 01, 01), period, teacher, classroom);
    private Lesson alreadyExistsLesson = new Lesson(2, course, LocalDate.of(2021, 01, 01), period, teacher, classroom);

    @Test
    void testGetAll() {
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(lesson);
        when(lessonDao.getAll()).thenReturn(lessons);
        List<Lesson> actual = service.getAll();
        verify(lessonDao, times(1)).getAll();
        assertEquals(lessons, actual);
    }

    @Test
    void testGetById() {
        when(lessonDao.getById(lesson.getId())).thenReturn(lesson);
        Lesson actual = service.getById(lesson.getId());
        verify(lessonDao, times(1)).getById(lesson.getId());
        assertEquals(lesson, actual);
    }

    @Test
    void testInsert() {
        when(lessonDao.getByDatePeriodIdClassroomId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getClassroom().getId())).thenReturn(Optional.empty());
        when(lessonDao.getByDatePeriodIdTeacherId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getTeacher().getId())).thenReturn(Optional.empty());
        service.insert(lesson);
        verify(lessonDao, times(1)).insert(lesson);
    }

    @Test
    void insertTeacherIsBusyThrowException() {
        when(lessonDao.getByDatePeriodIdTeacherId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getTeacher().getId())).thenReturn(Optional.of(alreadyExistsLesson));
        ServiceException exception = assertThrows(ServiceException.class, () -> service.insert(lesson));
        assertEquals(TEACHER_IS_BUSY, exception.getMessage());
    }

    @Test
    void insertClassroomIsOccupiedThrowException() {
        when(lessonDao.getByDatePeriodIdTeacherId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getTeacher().getId())).thenReturn(Optional.empty());
        when(lessonDao.getByDatePeriodIdClassroomId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getClassroom().getId())).thenReturn(Optional.of(alreadyExistsLesson));
        ServiceException exception = assertThrows(ServiceException.class, () -> service.insert(lesson));
        assertEquals(CLASSROOM_IS_OCCUPIED, exception.getMessage());
    }

    @Test
    void testUpdate() {
        when(lessonDao.getByDatePeriodIdClassroomId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getClassroom().getId())).thenReturn(Optional.of(lesson));
        when(lessonDao.getByDatePeriodIdTeacherId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getTeacher().getId())).thenReturn(Optional.of(lesson));
        service.update(lesson);
        verify(lessonDao, times(1)).update(lesson);
    }

    @Test
    void updateTeacherIsBusyThrowException() {
        when(lessonDao.getByDatePeriodIdTeacherId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getTeacher().getId())).thenReturn(Optional.of(alreadyExistsLesson));
        ServiceException exception = assertThrows(ServiceException.class, () -> service.update(lesson));
        assertEquals(TEACHER_IS_BUSY_UPDATE, exception.getMessage());
    }

    @Test
    void updateClassroomIsOccupiedThrowException() {
        when(lessonDao.getByDatePeriodIdTeacherId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getTeacher().getId())).thenReturn(Optional.empty());
        when(lessonDao.getByDatePeriodIdClassroomId(lesson.getDate(), lesson.getPeriod().getId(),
                lesson.getClassroom().getId())).thenReturn(Optional.of(alreadyExistsLesson));
        ServiceException exception = assertThrows(ServiceException.class, () -> service.update(lesson));
        assertEquals(CLASSROOM_IS_OCCUPIED_UPDATE, exception.getMessage());
    }

    @Test
    void testDelete() {
        service.delete(lesson.getId());
        verify(lessonDao, times(1)).delete(lesson.getId());
    }

    @Test
    void testGetByCourseId() {
        int course_id = 1;
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(lesson);
        when(lessonDao.getByCourseId(course_id)).thenReturn(lessons);
        List<Lesson> actual = service.getByCourseId(course_id);
        verify(lessonDao, times(1)).getByCourseId(course_id);
        assertEquals(lessons, actual);
    }

    @Test
    void testGetByGroupId() {
        int group_id = 1;
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(lesson);
        when(lessonDao.getByGroupId(group_id)).thenReturn(lessons);
        List<Lesson> actual = service.getByGroupId(group_id);
        verify(lessonDao, times(1)).getByGroupId(group_id);
        assertEquals(lessons, actual);
    }

    @Test
    void testGetByTeacherId() {
        int teacher_id = 1;
        List<Lesson> lessons = new ArrayList<>();
        lessons.add(lesson);
        when(lessonDao.getByTeacherId(teacher_id)).thenReturn(lessons);
        List<Lesson> actual = service.getByTeacherId(teacher_id);
        verify(lessonDao, times(1)).getByTeacherId(teacher_id);
        assertEquals(lessons, actual);
    }
}
