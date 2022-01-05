package ua.com.foxminded.university.dao.jdbc;

import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_GET_ALL;
import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_GET_BY_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_INSERT;
import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_UPDATE;
import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_DELETE;

import java.sql.Date;
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

import ua.com.foxminded.university.dao.StudentDao;
import ua.com.foxminded.university.dao.jdbc.mappers.StudentMapper;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Student;

@Repository
public class StudentDaoJdbc extends AbstractDAO implements StudentDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentDaoJdbc.class);    
    private static final String ID_NOT_EXIST = "The student with id=%d does not exist";
    private StudentMapper studentMapper;

    @Autowired
    public void setStudentMapper(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public List<Student> getAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting all students");
        }
        try {
            return jdbcTemplate.query(STUDENT_GET_ALL, studentMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get all students", e);
        }
    }

    @Override
    public Student getById(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting student by id. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(STUDENT_GET_BY_ID, namedParameters, studentMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            String msg = String.format(ID_NOT_EXIST, id);
            throw new DaoException(msg, e);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get student by id. id=" + id, e);
        }
    }

    @Override
    public Student insert(Student item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating student. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("group_id", item.getGroup().getId())
                    .addValue("first_name", item.getFirstName())
                    .addValue("last_name", item.getLastName())
                    .addValue("gender", item.getGender().getValue())
                    .addValue("birthdate", Date.valueOf(item.getBirthdate()));
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(STUDENT_INSERT, namedParameters, keyHolder, new String[] { "id" });
            return new Student(keyHolder.getKeyAs(Integer.class), item.getFirstName(), item.getLastName(),
                    item.getGender(), item.getBirthdate(), item.getGroup());
        } catch (DataAccessException e) {
            throw new DaoException("Cannot create student. " + item, e);
        }
    }

    @Override
    public void update(Student item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating student. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("id", item.getId())
                    .addValue("group_id", item.getGroup().getId())
                    .addValue("first_name", item.getFirstName())
                    .addValue("last_name", item.getLastName())
                    .addValue("gender", item.getGender().getValue())
                    .addValue("birthdate", Date.valueOf(item.getBirthdate()));
            jdbcTemplate.update(STUDENT_UPDATE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot update student. " + item, e);
        }
    }

    @Override
    public void delete(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removung student. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            jdbcTemplate.update(STUDENT_DELETE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot remove student. id=" + id, e);
        }
    }
}
