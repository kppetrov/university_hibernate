package ua.com.foxminded.university.dao.jpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.LessonDao;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Lesson;

@Repository
public class LessonDaoImpl extends GenericDaoImpl<Lesson> implements LessonDao {

    public LessonDaoImpl() {
        super.setEntityClass(Lesson.class);
    }

    @Override
    public List<Lesson> getByCourseId(Long curseId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Lesson> getByGroupId(Long groupId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Lesson> getByTeacherId(Long teacherId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Lesson> getByDatePeriodIdTeacherId(LocalDate date, Long periodId, Long teacherId) {
        try {
            Lesson lesson =  getEntityManager()
                    .createNamedQuery(Lesson.FIND_LESSONS_BY_DATE_ADN_PERIOD_ID_AND_TEACHER_ID, Lesson.class)
                    .setParameter("date", date)
                    .setParameter("periodId", periodId)
                    .setParameter("teacherId", teacherId)
                    .setMaxResults(1)
                    .getSingleResult();
            return Optional.of(lesson);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            String msg = String.format(
                    "Cannot get lesson by date, periodId, teacherId. Date=%s; periodId=%d; teacherId=%d", date,
                    periodId, teacherId);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public Optional<Lesson> getByDatePeriodIdClassroomId(LocalDate date, Long periodId, Long classroomId) {
        try {
            Lesson lesson = getEntityManager()
                    .createNamedQuery(Lesson.FIND_LESSONS_BY_DATE_ADN_PERIOD_ID_AND_CLASSROOM_ID, Lesson.class)
                    .setParameter("date", date)
                    .setParameter("periodId", periodId)
                    .setParameter("classroomId", classroomId)
                    .setMaxResults(1)
                    .getSingleResult();
            return Optional.of(lesson);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            String msg = String.format(
                    "Cannot get lesson by date, periodId, classroomId. Date=%s; periodId=%d; classroomId=%d", date,
                    periodId, classroomId);
            throw new DaoException(msg, e);    
        }

    }
}
