package ua.com.foxminded.university.web.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;

class StudentListModelTest {
    private ModelMapper modelMapper = new ModelMapper();
    private Student student = new Student(1L, "first_name", "last_name", Gender.MAIL, LocalDate.of(2001, 01, 01),
            new Group(1L, "group1"));
    private StudentListModel studentListModel = new StudentListModel(student.getId(), student.getFirstName(),
            student.getLastName(), student.getGroup().getName());

    @Test
    void whenConvertStudentEntityToStudentListModelThenCorrect() {
        StudentListModel model = modelMapper.map(student, StudentListModel.class);
        assertEquals(studentListModel, model);
    }

    @Test
    void whenConvertStudentListModelToStudentEntityThenCorrect() {
        Student entity = modelMapper.map(studentListModel, Student.class);
        assertEquals(student.getId(), entity.getId());
        assertEquals(student.getFirstName(), entity.getFirstName());
        assertEquals(student.getLastName(), entity.getLastName());
        assertEquals(student.getGroup().getName(), entity.getGroup().getName());
    }
}
