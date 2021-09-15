package ua.com.foxminded.university.dao.jdbc;

import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_GET_ALL;
import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_GET_BY_ID;
import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_INSERT;
import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_UPDATE;
import static ua.com.foxminded.university.dao.jdbc.Query.STUDENT_DELETE;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.StudentDao;
import ua.com.foxminded.university.dao.jdbc.mappers.StudentMapper;
import ua.com.foxminded.university.model.Student;

@Repository
public class StudentDaoJdbc extends AbstractDAO implements StudentDao {
    private StudentMapper studentMapper;   
    
    @Autowired
    public void setStudentMapper(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public List<Student> getAll() {
        return jdbcTemplate.query(STUDENT_GET_ALL, studentMapper);
    }

    @Override
    public Student getById(int id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        List<Student> students = jdbcTemplate.query(STUDENT_GET_BY_ID, namedParameters, studentMapper);
        if (students.isEmpty()) {
            return new Student();
        }
        return students.get(0);
    }

    @Override
    public Student insert(Student item) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("first_name", item.getFirstName())
                .addValue("last_name", item.getLastName())
                .addValue("gender", item.getGender().getValue())
                .addValue("birthdate", Date.valueOf(item.getBirthdate()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(STUDENT_INSERT, namedParameters, keyHolder, new String[] { "id" });
        return new Student(keyHolder.getKeyAs(Integer.class), item.getFirstName(), item.getLastName(), item.getGender(),
                item.getBirthdate());
    }

    @Override
    public int update(Student item) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", item.getId())                
                .addValue("first_name", item.getFirstName())
                .addValue("last_name", item.getLastName())
                .addValue("gender", item.getGender().getValue())
                .addValue("birthdate", Date.valueOf(item.getBirthdate()));
        return jdbcTemplate.update(STUDENT_UPDATE, namedParameters);
    }

    @Override
    public int delete(int id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.update(STUDENT_DELETE, namedParameters);
    }
}