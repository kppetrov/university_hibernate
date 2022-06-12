package ua.com.foxminded.university.web.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;

class StudentModelTest {
    private ModelMapper modelMapper = new ModelMapper();
    private Student student = new Student(1L, "first_name", "last_name", Gender.MAIL, LocalDate.of(2001, 01, 01),
            new Group(1L, "group1"));
    private StudentModel studentModel = new StudentModel(student.getId(), student.getFirstName(), student.getLastName(),
            student.getGender(), student.getBirthdate(), student.getGroup().getId(), student.getGroup().getName());

    @Test
    void whenConvertStudentEntityToStudentModelThenCorrect() {
        StudentModel model = modelMapper.map(student, StudentModel.class);
        assertEquals(studentModel, model);
    }

    @Test
    void whenConvertStudentModelToStudentEntityThenCorrect() {
        Student entity = modelMapper.map(studentModel, Student.class);
        assertEquals(student, entity);
    }
}
