package ua.com.foxminded.university.service;

import java.util.List;

import ua.com.foxminded.university.model.Group;

public interface GroupService extends GenericService<Group>{
    public Group getByIdWithDetail(Long id);
    public List<Group> getByCourseId(Long curseId);
    public int updateStudents(Group item);
    public Group getByName(String name);
}
