package ua.com.foxminded.university.dao.jpa;

import java.util.List;

import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.GroupDao;
import ua.com.foxminded.university.model.Group;

@Repository
public class GroupDaoImpl extends GenericDaoImpl<Group> implements GroupDao {

    public GroupDaoImpl() {
        super.setEntityClass(Group.class);
    }
    
    @Override
    public Group getByIdWithDetail(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Group> getByCourseId(Long curseId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int updateStudents(Group item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Group getByName(String name) {
        throw new UnsupportedOperationException();
    }

}
