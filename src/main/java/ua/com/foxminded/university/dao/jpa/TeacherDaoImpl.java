package ua.com.foxminded.university.dao.jpa;

import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.TeacherDao;
import ua.com.foxminded.university.model.Teacher;

@Repository
public class TeacherDaoImpl extends GenericDaoImpl<Teacher> implements TeacherDao {

    public TeacherDaoImpl() {
        super.setEntityClass(Teacher.class);
    }
}
