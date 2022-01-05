package ua.com.foxminded.university.dao.jdbc;

import static ua.com.foxminded.university.dao.jdbc.Query.PERIOD_GET_ALL;
import static ua.com.foxminded.university.dao.jdbc.Query.PERIOD_GET_BY_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.PERIOD_INSERT;
import static ua.com.foxminded.university.dao.jdbc.Query.PERIOD_UPDATE;
import static ua.com.foxminded.university.dao.jdbc.Query.PERIOD_DELETE;

import java.sql.Time;
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

import ua.com.foxminded.university.dao.PeriodDao;
import ua.com.foxminded.university.dao.jdbc.mappers.PeriodMapper;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Period;

@Repository
public class PeriodDaoJdbc extends AbstractDAO implements PeriodDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodDaoJdbc.class);
    private static final String ID_NOT_EXIST = "The period with id=%d does not exist";
    private PeriodMapper periodMapper;

    @Autowired
    public void setPeriodMapper(PeriodMapper periodMapper) {
        this.periodMapper = periodMapper;
    }

    @Override
    public List<Period> getAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting all periods");
        }
        try {
            return jdbcTemplate.query(PERIOD_GET_ALL, periodMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get all periods", e);
        }
    }

    @Override
    public Period getById(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting period by id. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(PERIOD_GET_BY_ID, namedParameters, periodMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            String msg = String.format(ID_NOT_EXIST, id);
            throw new DaoException(msg, e);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get period by id. id=" + id, e);
        }
    }

    @Override
    public Period insert(Period item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating period. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("name", item.getName())
                    .addValue("start_time", Time.valueOf(item.getStart()))
                    .addValue("end_time", Time.valueOf(item.getEnd()));
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(PERIOD_INSERT, namedParameters, keyHolder, new String[] { "id" });
            return new Period(keyHolder.getKeyAs(Integer.class), item.getName(), item.getStart(), item.getEnd());
        } catch (DataAccessException e) {
            throw new DaoException("Cannot create period. " + item, e);
        }
    }

    @Override
    public void update(Period item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating period. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", item.getId())
                    .addValue("name", item.getName()).addValue("start_time", Time.valueOf(item.getStart()))
                    .addValue("end_time", Time.valueOf(item.getEnd()));
            jdbcTemplate.update(PERIOD_UPDATE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot update period. " + item, e);
        }
    }

    @Override
    public void delete(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removung period. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            jdbcTemplate.update(PERIOD_DELETE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot remove classroom. id=" + id, e);
        }
    }
}
