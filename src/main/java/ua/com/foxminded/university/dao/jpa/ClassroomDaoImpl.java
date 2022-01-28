package ua.com.foxminded.university.dao.jpa;


import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.ClassroomDao;
import ua.com.foxminded.university.model.Classroom;

@Repository("ClassroomDaoImpl")
public class ClassroomDaoImpl extends GenericDaoImpl<Classroom> implements ClassroomDao {
    
    public ClassroomDaoImpl() {
        super.setEntityClass(Classroom.class);
    }
}
