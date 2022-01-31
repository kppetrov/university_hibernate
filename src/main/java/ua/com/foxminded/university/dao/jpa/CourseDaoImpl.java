package ua.com.foxminded.university.dao.jpa;

import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.CourseDao;
import ua.com.foxminded.university.model.Course;

@Repository("CourseDaoImpl")
public class CourseDaoImpl extends GenericDaoImpl<Course> implements CourseDao {

    public CourseDaoImpl() {
        super.setEntityClass(Course.class);
    }

    @Override
    public Course getByIdWithDetail(int id) {
        return  getEntityManager()
                .createNamedQuery(Course.FIND_COURSE_BY_ID_WITH_GROUPS, Course.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Override
    public int updateGroups(Course item) {
        throw new UnsupportedOperationException();
    }
}
