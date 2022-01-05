package ua.com.foxminded.university.dao.jdbc;

import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_GET_ALL;
import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_GET_BY_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_GET_BY_NAME;
import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_GET_BY_COURSE_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_GET_BY_ID_DETAIL;
import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_INSERT;
import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_UPDATE;
import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_DELETE;
import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_REMOVE_STUDENTS_FROM_GROUP;
import static ua.com.foxminded.university.dao.jdbc.Query.GROUP_ADD_STUDENTS_TO_GROUP;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import ua.com.foxminded.university.dao.GroupDao;
import ua.com.foxminded.university.dao.jdbc.mappers.GroupMapper;
import ua.com.foxminded.university.dao.jdbc.mappers.GroupWithStudentsExtractor;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;

@Repository
public class GroupDaoJdbc extends AbstractDAO implements GroupDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupDaoJdbc.class);
    private static final String ID_NOT_EXIST = "The group with id=%d does not exist";
    private GroupMapper groupMapper;
    private GroupWithStudentsExtractor groupWithDetailExtractor;

    @Autowired
    public void setGroupMapper(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    @Autowired
    public void setGroupWithDetailExtractor(GroupWithStudentsExtractor groupWithDetailExtractor) {
        this.groupWithDetailExtractor = groupWithDetailExtractor;
    }

    @Override
    public List<Group> getAll() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting all groups");
        }
        try {
            return jdbcTemplate.query(GROUP_GET_ALL, groupMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get all groups", e);
        }
    }

    @Override
    public Group getById(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting group by id. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.queryForObject(GROUP_GET_BY_ID, namedParameters, groupMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            String msg = String.format(ID_NOT_EXIST, id);
            throw new DaoException(msg, e);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get group by id. id=" + id, e);
        }
    }

    @Override
    public Group getByName(String name) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting group by name. name={}", name);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("name", name);
            List<Group> groups = jdbcTemplate.query(GROUP_GET_BY_NAME, namedParameters, groupMapper);
            if (groups.isEmpty()) {
                return new Group();
            }
            return groups.get(0);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get group by name. name=" + name, e);
        }
    }

    @Override
    public Group getByIdWithDetail(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting group by id with detail. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.query(GROUP_GET_BY_ID_DETAIL, namedParameters, groupWithDetailExtractor);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get group by id with detail. id=" + id, e);
        }
    }

    @Override
    public List<Group> getByCourseId(int courseId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting group by curseId. curseId={}", courseId);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("course_id", courseId);
            return jdbcTemplate.query(GROUP_GET_BY_COURSE_ID, namedParameters, groupMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get group by curseId. curseId=" + courseId, e);
        }
    }

    @Override
    public Group insert(Group item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating group. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("name", item.getName());
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(GROUP_INSERT, namedParameters, keyHolder, new String[] { "id" });
            return new Group(keyHolder.getKeyAs(Integer.class), item.getName(), new ArrayList<>());
        } catch (DataAccessException e) {
            throw new DaoException("Cannot create group. " + item, e);
        }
    }

    @Override
    public void update(Group item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating group. {}", item);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", item.getId())
                    .addValue("name", item.getName());
            jdbcTemplate.update(GROUP_UPDATE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot update group. " + item, e);
        }
    }

    @Override
    public void delete(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removung group. id={}", id);
        }
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            jdbcTemplate.update(GROUP_DELETE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot remove group. id=" + id, e);
        }
    }

    @Override
    public int updateStudents(Group item) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating group students. {}", item);
        }
        try {
            int result = 0;
            List<Integer> studentsIds = item.getStudents().stream().map(Student::getId).collect(Collectors.toList());
            SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("group_id", item.getId())
                    .addValue("students_ids", studentsIds);
            result += jdbcTemplate.update(GROUP_REMOVE_STUDENTS_FROM_GROUP, namedParameters);
            result += jdbcTemplate.update(GROUP_ADD_STUDENTS_TO_GROUP, namedParameters);
            return result;
        } catch (DataAccessException e) {
            throw new DaoException("Cannot update group students. " + item, e);
        }
    }
}
