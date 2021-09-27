package ua.com.foxminded.university.dao.jdbc;

import static ua.com.foxminded.university.dao.jdbc.Query.TEACHER_GET_ALL;
import static ua.com.foxminded.university.dao.jdbc.Query.TEACHER_GET_BY_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.TEACHER_INSERT;
import static ua.com.foxminded.university.dao.jdbc.Query.TEACHER_UPDATE;
import static ua.com.foxminded.university.dao.jdbc.Query.TEACHER_DELETE;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.TeacherDao;
import ua.com.foxminded.university.dao.jdbc.mappers.TeacherMapper;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Teacher;

@Repository
public class TeacherDaoJdbc extends AbstractDAO implements TeacherDao {    
    private TeacherMapper teacherMapper;   
    
    @Autowired
    public void setTeacherMapper(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    @Override
    public List<Teacher> getAll() {
        try {
            return jdbcTemplate.query(TEACHER_GET_ALL, teacherMapper);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot get all teachers", e);
        }  
    }

    @Override
    public Teacher getById(int id) {
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            List<Teacher> teachers = jdbcTemplate.query(TEACHER_GET_BY_ID, namedParameters, teacherMapper);
            if (teachers.isEmpty()) {
                return new Teacher();
            }
            return teachers.get(0);
        } catch (DataAccessException e) {

            throw new DaoException("Cannot get teacher by id. id = " + id, e);
        }
    }

    @Override
    public Teacher insert(Teacher item) {
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("first_name", item.getFirstName())
                    .addValue("last_name", item.getLastName())
                    .addValue("gender", item.getGender().getValue())
                    .addValue("birthdate", Date.valueOf(item.getBirthdate()));
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(TEACHER_INSERT, namedParameters, keyHolder, new String[] { "id" });
            return new Teacher(keyHolder.getKeyAs(Integer.class), item.getFirstName(), item.getLastName(), item.getGender(),
                    item.getBirthdate());
        } catch (DataAccessException e) {            
            throw new DaoException("Cannot create teacher. " + item, e);
        }
    }

    @Override
    public int update(Teacher item) {
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("id", item.getId())                
                    .addValue("first_name", item.getFirstName())
                    .addValue("last_name", item.getLastName())
                    .addValue("gender", item.getGender().getValue())
                    .addValue("birthdate", Date.valueOf(item.getBirthdate()));
            return jdbcTemplate.update(TEACHER_UPDATE, namedParameters);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot update teacher. " + item, e);
        }
    }

    @Override
    public int delete(int id) {
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            return jdbcTemplate.update(TEACHER_DELETE, namedParameters);
        } catch (DataAccessException e) {            
            throw new DaoException("Cannot remove teacher. id = " + id, e);
        }
    }
}
