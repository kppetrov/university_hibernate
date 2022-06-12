package ua.com.foxminded.university.dao;

import java.util.List;

import ua.com.foxminded.university.model.Group;

public interface GroupDao extends GenericDao<Group> {
    public Group getByIdWithDetail(Long id);
    public List<Group> getByCourseId(Long curseId);
    public int updateStudents(Group item);
    public Group getByName(String name);
}
