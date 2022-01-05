package ua.com.foxminded.university.dao.jdbc;

import static ua.com.foxminded.university.dao.jdbc.Query.CLASSROOM_GET_ALL;
import static ua.com.foxminded.university.dao.jdbc.Query.CLASSROOM_GET_BY_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.CLASSROOM_INSERT;
import static ua.com.foxminded.university.dao.jdbc.Query.CLASSROOM_UPDATE;
import static ua.com.foxminded.university.dao.jdbc.Query.CLASSROOM_DELETE;

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

import ua.com.foxminded.university.dao.ClassroomDao;
import ua.com.foxminded.university.dao.jdbc.mappers.ClassroomMapper;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Classroom;

@Repository
public class ClassroomDaoJdbc extends AbstractDAO implements ClassroomDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassroomDaoJdbc.class);
    private static final String ID_NOT_EXIST = "The classroom with id=%d does not exist";
    private ClassroomMapper classroomMapper;

    @Autowired
    public void setClassroomMapper(ClassroomMapper classroomMapper) {
        this.classroomMapper = classroomMapper;
    }

    @Override
    public List<Classroom> getAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting all classrooms");
        }
        try {
            return jdbcTemplate.query(CLASSROOM_GET_ALL, classroomMapper);
        } catch (DataAccessException e) {
            String msg = "Cannot get all classrooms";
            throw new DaoException(msg, e);
        }
    }

    @Override
    public Classroom getById(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting classroom by id. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(CLASSROOM_GET_BY_ID, namedParameters, classroomMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            String msg = String.format(ID_NOT_EXIST, id);
            throw new DaoException(msg, e);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get classroom by id. id=" + id, e);
        }
    }

    @Override
    public Classroom insert(Classroom item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating classroom. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("name", item.getName());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(CLASSROOM_INSERT, namedParameters, keyHolder, new String[] { "id" });
            return new Classroom(keyHolder.getKeyAs(Integer.class), item.getName());
        } catch (DataAccessException e) {
            throw new DaoException("Cannot create classroom. " + item, e);
        }
    }

    @Override
    public void update(Classroom item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating classroom. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", item.getId())
                    .addValue("name", item.getName());
            jdbcTemplate.update(CLASSROOM_UPDATE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot update classroom. " + item, e);
        }
    }

    @Override
    public void delete(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removung classroom. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            jdbcTemplate.update(CLASSROOM_DELETE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot remove classroom. id=" + id, e);
        }
    }
}
