package ua.com.foxminded.university.dao.jpa;

import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.StudentDao;
import ua.com.foxminded.university.model.Student;

@Repository("StudentDaoImpl")
public class StudentDaoImpl extends GenericDaoImpl<Student> implements StudentDao {

    public StudentDaoImpl() {
        super.setEntityClass(Student.class);
    }
}
