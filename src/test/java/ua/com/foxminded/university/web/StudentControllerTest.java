package ua.com.foxminded.university.web;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ua.com.foxminded.university.config.WebConfig;
import ua.com.foxminded.university.model.Gender;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.model.Student;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.service.StudentService;
import ua.com.foxminded.university.web.model.GroupModel;
import ua.com.foxminded.university.web.model.StudentListModel;
import ua.com.foxminded.university.web.model.StudentModel;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
class StudentControllerTest {
    private static final String STUDENT_FIRST_NAME = "first_name";
    private static final String STUDENT_LAST_NAME = "last_name";
    private static final LocalDate BIRTHDATE = LocalDate.of(2000, 01, 01);
    private static final String GROUP_NAME = "group";

    private MockMvc mockMvc;
    @Mock
    private StudentService studentService;
    @Mock
    private GroupService groupService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private StudentController controller;

    private Group group = new Group(1, GROUP_NAME);
    private GroupModel groupModel = new GroupModel(1, GROUP_NAME);
    private List<Group> groups = Arrays.asList(group);
    private List<GroupModel> groupModels = Arrays.asList(groupModel);    
    private Student student = new Student(1, STUDENT_FIRST_NAME, STUDENT_FIRST_NAME, Gender.MAIL, BIRTHDATE, group);
    private StudentListModel studentListModel = new StudentListModel(1, student.getFirstName(), student.getLastName(),
            student.getGroup().getName());
    private StudentModel studentModel = new StudentModel(1, STUDENT_FIRST_NAME, STUDENT_LAST_NAME, Gender.MAIL,
            BIRTHDATE, 1, GROUP_NAME);

    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Student> students = Arrays.asList(student);
        List<StudentListModel> expected = Arrays.asList(studentListModel);
        when(modelMapper.map(student, StudentListModel.class)).thenReturn(studentListModel);
        when(studentService.getAll()).thenReturn(students);
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attribute("students", expected));
        verify(studentService, times(1)).getAll();
        verifyNoMoreInteractions(studentService);
    }
    
    @Test
    void testShow() throws Exception {
        when(studentService.getById(1)).thenReturn(student);
        when(modelMapper.map(student, StudentModel.class)).thenReturn(studentModel);
        mockMvc.perform(get("/students/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("students/show"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attribute("student", studentModel));
        verify(studentService, times(1)).getById(1);
        verifyNoMoreInteractions(studentService);
    }
    
    @Test
    void testEditForm() throws Exception {
        when(groupService.getAll()).thenReturn(groups);
        when(modelMapper.map(group, GroupModel.class)).thenReturn(groupModel);
        when(studentService.getById(1)).thenReturn(student);
        when(modelMapper.map(student, StudentModel.class)).thenReturn(studentModel);
        mockMvc.perform(get("/students/edit/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attribute("student", studentModel))
                .andExpect(model().attributeExists("groups"))
                .andExpect(model().attribute("groups", groupModels));
        verify(studentService, times(1)).getById(1);
        verifyNoMoreInteractions(studentService);
    }
    
    @Test
    void testCreateForm() throws Exception {
        when(groupService.getAll()).thenReturn(groups);
        when(modelMapper.map(group, GroupModel.class)).thenReturn(groupModel);
        mockMvc.perform(get("/students/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attribute("student", new StudentModel()))
                .andExpect(model().attributeExists("groups"))
                .andExpect(model().attribute("groups", groupModels));
    }    
   
    @Test
    void testEdit() throws Exception { 
        Student editStudent = new Student(1, STUDENT_FIRST_NAME, STUDENT_FIRST_NAME, Gender.MAIL, BIRTHDATE, new Group(1, null));
        StudentModel editStudentModel = new StudentModel(1, STUDENT_FIRST_NAME, STUDENT_LAST_NAME, Gender.MAIL,
                BIRTHDATE, 1, null);        
        when(modelMapper.map(editStudentModel, Student.class)).thenReturn(editStudent);   
        mockMvc.perform(post("/students/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("firstName", STUDENT_FIRST_NAME)
                        .param("lastName", STUDENT_LAST_NAME)
                        .param("gender", "MAIL")
                        .param("birthdate", "2000-01-01")
                        .param("groupId", "1")
                .sessionAttr("student", new StudentModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/students/1"));
        
        ArgumentCaptor<StudentModel> formObjectArgument = ArgumentCaptor.forClass(StudentModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Student.class));
        verifyNoMoreInteractions(modelMapper); 
        StudentModel formObject = formObjectArgument.getValue();
      
        assertAll(
                () -> assertEquals(1, formObject.getId()), 
                () -> assertEquals(STUDENT_FIRST_NAME, formObject.getFirstName()),
                () -> assertEquals(STUDENT_LAST_NAME, formObject.getLastName()),
                () -> assertEquals(Gender.MAIL, formObject.getGender()),
                () -> assertEquals(BIRTHDATE, formObject.getBirthdate()),
                () -> assertEquals(1, formObject.getGroupId())
                );
                
        verify(studentService, times(1)).update(student);
        verifyNoMoreInteractions(studentService);
    }
    
    @Test
    void testCreate() throws Exception {
        Student newStudent = new Student(0, STUDENT_FIRST_NAME, STUDENT_FIRST_NAME, Gender.MAIL, BIRTHDATE, new Group(1, null));
        StudentModel newStudentModel = new StudentModel(0, STUDENT_FIRST_NAME, STUDENT_LAST_NAME, Gender.MAIL,
                BIRTHDATE, 1, null);        
        when(modelMapper.map(newStudentModel, Student.class)).thenReturn(newStudent);   
        when(studentService.insert(newStudent)).thenReturn(student);
        mockMvc.perform(post("/students/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", STUDENT_FIRST_NAME)
                        .param("lastName", STUDENT_LAST_NAME)
                        .param("gender", "MAIL")
                        .param("birthdate", "2000-01-01")
                        .param("groupId", "1")
                .sessionAttr("student", new StudentModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/students"));
        
        ArgumentCaptor<StudentModel> formObjectArgument = ArgumentCaptor.forClass(StudentModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Student.class));
        verifyNoMoreInteractions(modelMapper); 
        StudentModel formObject = formObjectArgument.getValue(); 
        
        assertAll(
                () -> assertEquals(0, formObject.getId()), 
                () -> assertEquals(STUDENT_FIRST_NAME, formObject.getFirstName()),
                () -> assertEquals(STUDENT_LAST_NAME, formObject.getLastName()),
                () -> assertEquals(Gender.MAIL, formObject.getGender()),
                () -> assertEquals(BIRTHDATE, formObject.getBirthdate()),
                () -> assertEquals(1, formObject.getGroupId())
                );                
        
        verify(studentService, times(1)).insert(newStudent);
        verifyNoMoreInteractions(studentService);
    }
    
    @Test
    void testRemove() throws Exception {
        mockMvc.perform(get("/students/remove/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/students"));
        verify(studentService, times(1)).delete(1);
        verifyNoMoreInteractions(studentService);
    }
}
