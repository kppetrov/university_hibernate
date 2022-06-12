package ua.com.foxminded.university.web.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import ua.com.foxminded.university.model.Group;

class GroupModelTest {
    private ModelMapper modelMapper = new ModelMapper();
    private Group group = new Group(1L, "group1");
    private GroupModel groupModel = new GroupModel(group.getId(), group.getName());

    @Test
    void whenConvertGroupEntityToGroupModelThenCorrect() {
        GroupModel model = modelMapper.map(group, GroupModel.class);
        assertEquals(groupModel, model);
    }

    @Test
    void whenConvertGroupModelToGroupEntityThenCorrect() {
        Group entity = modelMapper.map(groupModel, Group.class);
        assertEquals(group, entity);
    }
}
