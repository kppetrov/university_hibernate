package ua.com.foxminded.university.dao.jdbc;

import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_GET_ALL;
import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_GET_BY_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_GET_BY_COURSE_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_GET_BY_TEACHER_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_GET_BY_GROUP_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_GET_BY_DATE_PERIOD_ID_CLASSROOM_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_GET_BY_DATE_PERIOD_ID_TEACHER_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_INSERT;
import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_UPDATE;
import static ua.com.foxminded.university.dao.jdbc.Query.LESSON_DELETE;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.LessonDao;
import ua.com.foxminded.university.dao.jdbc.mappers.LessonMapper;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Lesson;

@Repository("LessonDaoJdbc")
public class LessonDaoJdbc extends AbstractDAO implements LessonDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(LessonDaoJdbc.class);
    private static final String ID_NOT_EXIST = "The lesson with id=%d does not exist";
    private LessonMapper lessonMapper;

    @Autowired
    public void setLessonWithDetailExtractor(LessonMapper lessonMapper) {
        this.lessonMapper = lessonMapper;
    }

    @Override
    public List<Lesson> getAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting all lessons");
        }
        try {
            return jdbcTemplate.query(LESSON_GET_ALL, lessonMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get all lessons", e);
        }
    }

    @Override
    public Lesson getById(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting lesson by id. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(LESSON_GET_BY_ID, namedParameters, lessonMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            String msg = String.format(ID_NOT_EXIST, id);
            throw new DaoException(msg, e);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get lesson by id. id=" + id, e);
        }
    }

    @Override
    public Lesson insert(Lesson item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating lesson. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("date", Date.valueOf(item.getDate()))
                    .addValue("course_id", item.getCourse().getId())
                    .addValue("period_id", item.getPeriod().getId())
                    .addValue("classroom_id", item.getClassroom().getId())
                    .addValue("teacher_id", item.getTeacher().getId());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(LESSON_INSERT, namedParameters, keyHolder, new String[] { "id" });
            return new Lesson(keyHolder.getKeyAs(Integer.class), item.getCourse(), item.getDate(), item.getPeriod(),
                    item.getTeacher(), item.getClassroom());
        } catch (DataAccessException e) {
            throw new DaoException("Cannot create lesson. " + item, e);
        }
    }

    @Override
    public void update(Lesson item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating lesson. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("id", item.getId())
                    .addValue("date", Date.valueOf(item.getDate()))
                    .addValue("course_id", item.getCourse().getId())
                    .addValue("period_id", item.getPeriod().getId())
                    .addValue("classroom_id", item.getClassroom().getId())
                    .addValue("teacher_id", item.getTeacher().getId());
            jdbcTemplate.update(LESSON_UPDATE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot update lesson. " + item, e);
        }
    }

    @Override
    public void delete(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removung lesson. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            jdbcTemplate.update(LESSON_DELETE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot remove lesson. id=" + id, e);
        }
    }

    @Override
    public List<Lesson> getByGroupId(int groupId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting lessons by groupId. groupId={}", groupId);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("group_id", groupId);
            return jdbcTemplate.query(LESSON_GET_BY_GROUP_ID, namedParameters, lessonMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get lesson by groupId. groupId=" + groupId, e);
        }
    }

    @Override
    public List<Lesson> getByTeacherId(int teacherId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting lessons by teacherId. teacherId={}", teacherId);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("teacher_id", teacherId);
            return jdbcTemplate.query(LESSON_GET_BY_TEACHER_ID, namedParameters, lessonMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get lesson by teacherId. teacherId=" + teacherId, e);
        }
    }

    @Override
    public List<Lesson> getByCourseId(int curseId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting lessons by curseId. curseId={}", curseId);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("course_id", curseId);
            return jdbcTemplate.query(LESSON_GET_BY_COURSE_ID, namedParameters, lessonMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get lesson by curseId. curseId=" + curseId, e);
        }
    }

    @Override
    public Lesson getByDatePeriodIdTeacherId(LocalDate date, int periodId, int teacherId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting lesson by date, periodId, teacherId. Date={}; periodId={}; teacherId={}", date,
                    periodId, teacherId);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("date", date)
                    .addValue("period_id", periodId)
                    .addValue("teacher_id", teacherId);
            List<Lesson> lessons = jdbcTemplate.query(LESSON_GET_BY_DATE_PERIOD_ID_TEACHER_ID, namedParameters,
                    lessonMapper);
            if (lessons.isEmpty()) {
                return new Lesson();
            }
            return lessons.get(0);
        } catch (DataAccessException e) {
            String msg = String.format(
                    "Cannot get lesson by date, periodId, teacherId. Date=%s; periodId=%d; teacherId=%d", date,
                    periodId, teacherId);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public Lesson getByDatePeriodIdClassroomId(LocalDate date, int periodId, int classroomId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting lesson by date, periodId, classroomId. Date={}; periodId={}; classroomId={}", date,
                    periodId, classroomId);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("date", date)
                    .addValue("period_id", periodId)
                    .addValue("classroom_id", classroomId);
            List<Lesson> lessons = jdbcTemplate.query(LESSON_GET_BY_DATE_PERIOD_ID_CLASSROOM_ID, namedParameters,
                    lessonMapper);
            if (lessons.isEmpty()) {
                return new Lesson();
            }
            return lessons.get(0);
        } catch (DataAccessException e) {
            String msg = String.format(
                    "Cannot get lesson by date, periodId, classroomId. Date=%s; periodId=%d; classroomId=%d", date,
                    periodId, classroomId);
            throw new DaoException(msg, e);
        }
    }
}
