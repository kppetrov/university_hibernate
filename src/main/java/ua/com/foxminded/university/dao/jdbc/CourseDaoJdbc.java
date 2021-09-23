package ua.com.foxminded.university.dao.jdbc;

import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_GET_ALL;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_GET_BY_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_INSERT;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_UPDATE;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_DELETE;    
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_REMOVE_GROUP_FROM_COURSE;
import static ua.com.foxminded.university.dao.jdbc.Query.COURSE_ADD_GROUP_TO_COURSE;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.CourseDao;
import ua.com.foxminded.university.dao.DaoException;
import ua.com.foxminded.university.dao.GroupDao;
import ua.com.foxminded.university.dao.LessonDao;
import ua.com.foxminded.university.dao.jdbc.mappers.CourseMapper;
import ua.com.foxminded.university.model.Course;
import ua.com.foxminded.university.model.Group;

@Repository
public class CourseDaoJdbc extends AbstractDAO implements CourseDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseDaoJdbc.class);
    
    private CourseMapper courseMapper;
    private LessonDao lessonDao;
    private GroupDao groupDao;   
    
    @Autowired
    public void setCourseMapper(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }
    
    @Autowired
    public void setLessonDao(LessonDao lessonDao) {
        this.lessonDao = lessonDao;
    }
    
    @Autowired
    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }
    
    @Override
    public List<Course> getAll() {        
        try {
            return jdbcTemplate.query(COURSE_GET_ALL, courseMapper);
        } catch (Exception e) {
            String msg = "Cannot get all courses";
            LOGGER.error(msg, e);
            throw new DaoException(msg, e);
        } 
    }

    @Override
    public Course getById(int id) {        
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            List<Course> courses = jdbcTemplate.query(COURSE_GET_BY_ID, namedParameters, courseMapper);
            if (courses.isEmpty()) {
                return new Course();
            }
            return courses.get(0);
        } catch (Exception e) {
            String msg = "Cannot get course by id. Id = " + id;
            LOGGER.error(msg, e);
            throw new DaoException(msg, e);
        }
    }
    
    @Override
    public Course getByIdWithDetail(int id) {
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            List<Course> courses = jdbcTemplate.query(COURSE_GET_BY_ID, namedParameters, courseMapper);
            if (courses.isEmpty()) {
                return new Course();
            }
            Course course = courses.get(0);
            course.setGroups(groupDao.getByCourseId(id));
            course.setLessons(lessonDao.getByCourseId(id));
            return course; 
        } catch (Exception e) {
            String msg = "Cannot get classroom by id with detail. Id = " + id;
            LOGGER.error(msg, e);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public Course insert(Course item) {
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("name", item.getName());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(COURSE_INSERT, namedParameters, keyHolder, new String[] { "id" });
            Course course =  new Course();
            course.setId(keyHolder.getKeyAs(Integer.class));
            course.setName(item.getName());
            return course;
        } catch (Exception e) {            
            String msg = "Cannot create course. " + item;
            LOGGER.error(msg, e);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public int update(Course item) {
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("id", item.getId())
                    .addValue("name", item.getName());
            return jdbcTemplate.update(COURSE_UPDATE, namedParameters);
        } catch (Exception e) {
            String msg = "Cannot update course. " + item;
            LOGGER.error(msg, e);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public int delete(int id) {
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.update(COURSE_DELETE, namedParameters);

        } catch (Exception e) {            
            String msg = "Cannot remove course. Id = " + id;
            LOGGER.error(msg, e);
            throw new DaoException(msg, e);

        }
    }

    @Override
    public int updateGroups(Course item) {
        try {
            int result = 0;    
            List<Integer> groupIds = item.getGroups().stream().map(Group::getId).collect(Collectors.toList());        
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("course_id", item.getId())
                    .addValue("group_ids", groupIds);

            result += jdbcTemplate.update(COURSE_REMOVE_GROUP_FROM_COURSE, namedParameters);
            result += jdbcTemplate.update(COURSE_ADD_GROUP_TO_COURSE, namedParameters);        
            return result;
        } catch (Exception e) {
            String msg = "Cannot update course groups. " + item;
            LOGGER.error(msg, e);
            throw new DaoException(msg, e);
        }
    }    
}
