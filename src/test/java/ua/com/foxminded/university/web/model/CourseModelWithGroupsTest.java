package ua.com.foxminded.university.web.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import ua.com.foxminded.university.model.Course;
import ua.com.foxminded.university.model.Group;

class CourseModelWithGroupsTest {
    private ModelMapper modelMapper = new ModelMapper();
    private Group group = new Group(1, "group1");
    private GroupModel groupModel = new GroupModel(group.getId(), group.getName());
    private Course course = new Course(1, "course1", null, new HashSet<>(Arrays.asList(group)));
    private CourseModelWithGroups courseModel = new CourseModelWithGroups(1, "course1",
            new HashSet<>(Arrays.asList(groupModel)));

    @Test
    void whenConvertCourseEntityToCourseModelThenCorrect() {
        CourseModelWithGroups model = modelMapper.map(course, CourseModelWithGroups.class);
        assertEquals(courseModel, model);
        assertEquals(courseModel.getGroups(), model.getGroups());
    }

    @Test
    void whenConvertCourseModelToCourseEntityThenCorrect() {
        Course entity = modelMapper.map(courseModel, Course.class);
        assertEquals(course, entity);
        assertEquals(course.getGroups(), entity.getGroups());
    }

}
