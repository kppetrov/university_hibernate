package ua.com.foxminded.university.dao.jpa;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.CourseDao;
import ua.com.foxminded.university.exception.DaoException;
import ua.com.foxminded.university.model.Course;

@Repository
public class CourseDaoImpl extends GenericDaoImpl<Course> implements CourseDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseDaoImpl.class);

    public CourseDaoImpl() {
        super.setEntityClass(Course.class);
    }

    @Override
    public Course getByIdWithDetail(int id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Getting course by id with detail. id={}", id);
        }
        try {
            return  getEntityManager()
                    .createNamedQuery(Course.FIND_COURSE_BY_ID_WITH_GROUPS, Course.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            String msg = String.format("The course with id=%d does not exist", id);
            throw new DaoException(msg, e);
        } catch (PersistenceException e) {
            String msg = String.format("Cannot get course by id with detail. id=%d", id);
            throw new DaoException(msg, e);
        }
    }

    @Override
    public int updateGroups(Course item) {
        throw new UnsupportedOperationException();
    }
}
