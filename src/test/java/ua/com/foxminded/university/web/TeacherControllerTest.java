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
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.service.TeacherService;
import ua.com.foxminded.university.web.model.TeacherModel;
import ua.com.foxminded.university.web.model.TeacherListModel;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
class TeacherControllerTest {
    private static final String TEACHER_FIRST_NAME = "first_name";
    private static final String TEACHER_LAST_NAME = "last_name";
    private static final LocalDate BIRTHDATE = LocalDate.of(1971, 01, 01);
    
    private MockMvc mockMvc;
    @Mock
    private TeacherService teacherService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private TeacherController controller;

    private Teacher teacher = new Teacher(1, "first_name", "last_name", Gender.MAIL, BIRTHDATE);
    private TeacherListModel teacherListModel = new TeacherListModel(1, TEACHER_FIRST_NAME, TEACHER_LAST_NAME);
    private TeacherModel teacherModel = new TeacherModel(1, TEACHER_FIRST_NAME, TEACHER_LAST_NAME, Gender.MAIL, BIRTHDATE);


    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Teacher> teachers = Arrays.asList(teacher);
        List<TeacherListModel> expected = Arrays.asList(teacherListModel);        
        when(teacherService.getAll()).thenReturn(teachers);
        when(modelMapper.map(teacher, TeacherListModel.class)).thenReturn(teacherListModel);
        mockMvc.perform(get("/teachers"))
                .andExpect(status().isOk())
                .andExpect(view().name("teachers/list"))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attribute("teachers", expected));
        verify(teacherService, times(1)).getAll();
        verifyNoMoreInteractions(teacherService);
    }
    
    @Test
    void testShow() throws Exception {
        when(teacherService.getById(1)).thenReturn(teacher);
        when(modelMapper.map(teacher, TeacherModel.class)).thenReturn(teacherModel);
        mockMvc.perform(get("/teachers/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("teachers/show"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attribute("teacher", teacherModel));
        verify(teacherService, times(1)).getById(1);
        verifyNoMoreInteractions(teacherService);
    }
    
    @Test
    void testEditForm() throws Exception {
        when(teacherService.getById(1)).thenReturn(teacher);
        when(modelMapper.map(teacher, TeacherModel.class)).thenReturn(teacherModel);
        mockMvc.perform(get("/teachers/edit/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("teachers/form"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attribute("teacher", teacherModel));
        verify(teacherService, times(1)).getById(1);
        verifyNoMoreInteractions(teacherService);
    }
    
    @Test
    void testCreateForm() throws Exception {
        mockMvc.perform(get("/teachers/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("teachers/form"))
                .andExpect(model().attributeExists("teacher"))
                .andExpect(model().attribute("teacher", new TeacherModel()));
    }    
   
    @Test
    void testEdit() throws Exception {      
        when(modelMapper.map(teacherModel, Teacher.class)).thenReturn(teacher);   
        mockMvc.perform(post("/teachers/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("firstName", TEACHER_FIRST_NAME)
                        .param("lastName", TEACHER_LAST_NAME)
                        .param("gender", "MAIL")
                        .param("birthdate", "1971-01-01")
                .sessionAttr("teacher", new TeacherModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teachers/1"));
        
        ArgumentCaptor<TeacherModel> formObjectArgument = ArgumentCaptor.forClass(TeacherModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Teacher.class));
        verifyNoMoreInteractions(modelMapper); 
        TeacherModel formObject = formObjectArgument.getValue();
      
        assertAll(
                () -> assertEquals(1, formObject.getId()), 
                () -> assertEquals(TEACHER_FIRST_NAME, formObject.getFirstName()),
                () -> assertEquals(TEACHER_LAST_NAME, formObject.getLastName()),
                () -> assertEquals(Gender.MAIL, formObject.getGender()),
                () -> assertEquals(BIRTHDATE, formObject.getBirthdate())
                );
                
        verify(teacherService, times(1)).update(teacher);
        verifyNoMoreInteractions(teacherService);
    }
    
    @Test
    void testCreate() throws Exception {
        Teacher newTteacher = new Teacher(0, "first_name", "last_name", Gender.MAIL, BIRTHDATE);
        TeacherModel newTeacherModel = new TeacherModel(0, TEACHER_FIRST_NAME, TEACHER_LAST_NAME, Gender.MAIL, BIRTHDATE);
        when(modelMapper.map(newTeacherModel, Teacher.class)).thenReturn(newTteacher);   
        when(teacherService.insert(newTteacher)).thenReturn(teacher);
        mockMvc.perform(post("/teachers/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstName", TEACHER_FIRST_NAME)
                        .param("lastName", TEACHER_LAST_NAME)
                        .param("gender", "MAIL")
                        .param("birthdate", "1971-01-01")
                .sessionAttr("teacher", new TeacherModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teachers"));
        
        ArgumentCaptor<TeacherModel> formObjectArgument = ArgumentCaptor.forClass(TeacherModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Teacher.class));
        verifyNoMoreInteractions(modelMapper); 
        TeacherModel formObject = formObjectArgument.getValue();
      
        assertAll(
                () -> assertEquals(0, formObject.getId()), 
                () -> assertEquals(TEACHER_FIRST_NAME, formObject.getFirstName()),
                () -> assertEquals(TEACHER_LAST_NAME, formObject.getLastName()),
                () -> assertEquals(Gender.MAIL, formObject.getGender()),
                () -> assertEquals(BIRTHDATE, formObject.getBirthdate())
                );
                
        verify(teacherService, times(1)).insert(newTteacher);
        verifyNoMoreInteractions(teacherService);
    }
    
    @Test
    void testRemove() throws Exception {
        mockMvc.perform(get("/teachers/remove/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/teachers"));
        verify(teacherService, times(1)).delete(1);
        verifyNoMoreInteractions(teacherService);
    }
}
