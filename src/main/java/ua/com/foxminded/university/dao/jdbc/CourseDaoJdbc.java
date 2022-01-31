package ua.com.foxminded.university.dao.jdbc;

import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_GET_ALL;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_GET_BY_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_INSERT;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_UPDATE;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_DELETE;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_REMOVE_GROUP_FROM_COURSE;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_REMOVE_ALL_GROUP_FROM_COURSE;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_ADD_GROUP_TO_COURSE;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.CourseDao;
import ua.com.foxminded.university.dao.GroupDao;
import ua.com.foxminded.university.dao.LessonDao;
import ua.com.foxminded.university.dao.jdbc.mappers.CourseMapper;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Course;
import ua.com.foxminded.university.model.Group;

@Repository
public class CourseDaoJdbc extends AbstractDAO implements CourseDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseDaoJdbc.class);
    private static final String ID_NOT_EXIST = "The course with id=%d does not exist";
    private CourseMapper courseMapper;
    private LessonDao lessonDao;
    private GroupDao groupDao;

    @Autowired
    public void setCourseMapper(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @Autowired
    @Qualifier("LessonDaoJdbc")
    public void setLessonDao(LessonDao lessonDao) {
        this.lessonDao = lessonDao;
    }

    @Autowired
    @Qualifier("GroupDaoJdbc")
    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    @Override
    public List<Course> getAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting all courses");
        }
        try {
            return jdbcTemplate.query(COURSE_GET_ALL, courseMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get all courses", e);
        }
    }

    @Override
    public Course getById(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting course by id. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(COURSE_GET_BY_ID, namedParameters, courseMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            String msg = String.format(ID_NOT_EXIST, id);
            throw new DaoException(msg, e);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get course by id. id=" + id, e);
        }
    }

    @Override
    public Course getByIdWithDetail(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting course by id with detail. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            Course course = jdbcTemplate.queryForObject(COURSE_GET_BY_ID, namedParameters, courseMapper);
            course.setGroups(groupDao.getByCourseId(id));
            course.setLessons(lessonDao.getByCourseId(id));
            return course;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new DaoException("The course with id=" + id + " does not exist", e);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get classroom by id with detail. id=" + id, e);
        }
    }

    @Override
    public Course insert(Course item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating course. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("name", item.getName());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(COURSE_INSERT, namedParameters, keyHolder, new String[] { "id" });
            Course course = new Course();
            course.setId(keyHolder.getKeyAs(Integer.class));
            course.setName(item.getName());
            return course;
        } catch (DataAccessException e) {
            throw new DaoException("Cannot create course. " + item, e);
        }
    }

    @Override
    public void update(Course item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating course. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", item.getId())
                    .addValue("name", item.getName());
            jdbcTemplate.update(COURSE_UPDATE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot update course. " + item, e);
        }
    }

    @Override
    public void delete(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removung course. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            jdbcTemplate.update(COURSE_DELETE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot remove course. id=" + id, e);
        }
    }

    @Override
    public int updateGroups(Course item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating course groups. {}", item);
        }
        try {
            int result = 0;
            if (item.getGroups() == null || item.getGroups().isEmpty()) {
                SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("course_id", item.getId());
                result += jdbcTemplate.update(COURSE_REMOVE_ALL_GROUP_FROM_COURSE, namedParameters);
            } else {
                List<Integer> groupIds = item.getGroups().stream().map(Group::getId).collect(Collectors.toList());
                SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("course_id", item.getId())
                        .addValue("group_ids", groupIds);
                result += jdbcTemplate.update(COURSE_REMOVE_GROUP_FROM_COURSE, namedParameters);
                result += jdbcTemplate.update(COURSE_ADD_GROUP_TO_COURSE, namedParameters);
            }
            return result;
        } catch (DataAccessException e) {
            throw new DaoException("Cannot update course groups. " + item, e);
        }
    }
}
