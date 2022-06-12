package ua.com.foxminded.university.dao;

import ua.com.foxminded.university.model.Course;

public interface CourseDao extends GenericDao<Course> {
    public Course getByIdWithDetail(Long id);
    public int updateGroups(Course item);
}
