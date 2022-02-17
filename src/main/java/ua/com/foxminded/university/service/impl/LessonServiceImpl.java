package ua.com.foxminded.university.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.com.foxminded.university.dao.LessonDao;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.exception.ServiceException;
import ua.com.foxminded.university.model.Lesson;
import ua.com.foxminded.university.service.LessonService;

@Service
@Transactional
public class LessonServiceImpl implements LessonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LessonServiceImpl.class);
    private static final String TEACHER_IS_BUSY = "Cannot create lesson. The teacher is busy at this time.";
    private static final String CLASSROOM_IS_OCCUPIED = "Cannot create lesson. The classroom is occupied at this time.";
    private static final String TEACHER_IS_BUSY_UPDATE = "Cannot update lesson. The teacher is busy at this time.";
    private static final String CLASSROOM_IS_OCCUPIED_UPDATE = "Cannot update lesson. The classroom is occupied at this time.";
    
    private LessonDao lessonDao;

    @Autowired
    public void setLessonDao(LessonDao lessonDao) {
        this.lessonDao = lessonDao;
    }

    @Override
    public List<Lesson> getAll() {
        try {
            return lessonDao.getAll();
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Lesson getById(int id) {
        try {
            return lessonDao.getById(id);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Lesson insert(Lesson item) {
        if (teacherIsBusy(item.getDate(), item.getPeriod().getId(), item.getTeacher().getId())) {
            LOGGER.info(TEACHER_IS_BUSY);
            throw new ServiceException(TEACHER_IS_BUSY);        }
        if (classroomIsOccupied(item.getDate(), item.getPeriod().getId(), item.getClassroom().getId())) {
            LOGGER.info(CLASSROOM_IS_OCCUPIED);
            throw new ServiceException(CLASSROOM_IS_OCCUPIED);
        }
        try {
            return lessonDao.insert(item);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void update(Lesson item) {
        if (teacherIsBusy(item.getDate(), item.getPeriod().getId(), item.getTeacher().getId(), item.getId())) {
            LOGGER.info(TEACHER_IS_BUSY_UPDATE);
            throw new ServiceException(TEACHER_IS_BUSY_UPDATE);
        }
        if (classroomIsOccupied(item.getDate(), item.getPeriod().getId(), item.getClassroom().getId(), item.getId())) {
            LOGGER.info(CLASSROOM_IS_OCCUPIED_UPDATE);
            throw new ServiceException(CLASSROOM_IS_OCCUPIED_UPDATE);
        }
        try {
            lessonDao.update(item);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private boolean teacherIsBusy(LocalDate date, int periodId, int teacherId, int exceptLessonId) {
        LOGGER.debug("Check if teacher is busy");
        try {
            Optional<Lesson> optionalLesson = lessonDao.getByDatePeriodIdTeacherId(date, periodId, teacherId);
            if (optionalLesson.isPresent()) {
                return optionalLesson.get().getId() != exceptLessonId;
            }
            return false;
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private boolean classroomIsOccupied(LocalDate date, int periodId, int classroomId, int exceptLessonId) {
        LOGGER.debug("Check if classroom is occupied");
        try {
            Optional<Lesson> optionalLesson = lessonDao.getByDatePeriodIdClassroomId(date, periodId, classroomId);
            if (optionalLesson.isPresent()) {
                return optionalLesson.get().getId() != exceptLessonId;
            }
            return false;
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private boolean teacherIsBusy(LocalDate date, int periodId, int teacherId) {
        return teacherIsBusy(date, periodId, teacherId, -1);
    }

    private boolean classroomIsOccupied(LocalDate date, int periodId, int classroomId) {
        return classroomIsOccupied(date, periodId, classroomId, -1);
    }

    @Override
    public void delete(int id) {
        try {
            lessonDao.delete(id);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Lesson> getByCourseId(int curseId) {
        try {
            return lessonDao.getByCourseId(curseId);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Lesson> getByGroupId(int groupId) {
        try {
            return lessonDao.getByGroupId(groupId);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<Lesson> getByTeacherId(int teacherId) {
        try {
            return lessonDao.getByTeacherId(teacherId);
        } catch (DaoException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
