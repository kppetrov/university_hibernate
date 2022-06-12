package ua.com.foxminded.university.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.service.ClassroomService;
import ua.com.foxminded.university.web.model.ClassroomModel;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
class ClassroomControllerTest {
    private static final String CLASSROOM_NAME = "classroom name";
    
    private MockMvc mockMvc;
    @Mock
    private ClassroomService classroomService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private ClassroomController controller;

    private Classroom classroom = new Classroom(1L, CLASSROOM_NAME);
    private ClassroomModel classroomModel = new ClassroomModel(1L, CLASSROOM_NAME);

    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testList() throws Exception {
        List<Classroom> classrooms = Arrays.asList(classroom);
        List<ClassroomModel> expected = Arrays.asList(classroomModel);
        when(classroomService.getAll()).thenReturn(classrooms);
        when(modelMapper.map(classroom, ClassroomModel.class)).thenReturn(classroomModel);
        mockMvc.perform(get("/classrooms"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("classrooms/list"))
                .andExpect(model().attributeExists("classrooms"))
                .andExpect(model().attribute("classrooms", expected));
        verify(classroomService, times(1)).getAll();
        verifyNoMoreInteractions(classroomService);
    }
    
    @Test
    void testShow() throws Exception {
        when(classroomService.getById(1L)).thenReturn(classroom);
        when(modelMapper.map(classroom, ClassroomModel.class)).thenReturn(classroomModel);
        mockMvc.perform(get("/classrooms/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("classrooms/show"))
                .andExpect(model().attributeExists("classroom"))
                .andExpect(model().attribute("classroom", classroomModel));
        verify(classroomService, times(1)).getById(1L);
        verifyNoMoreInteractions(classroomService);
    }
    
    @Test
    void testEditForm() throws Exception {
        when(classroomService.getById(1L)).thenReturn(classroom);
        when(modelMapper.map(classroom, ClassroomModel.class)).thenReturn(classroomModel);
        mockMvc.perform(get("/classrooms/edit/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("classrooms/form"))
                .andExpect(model().attributeExists("classroom"))
                .andExpect(model().attribute("classroom", classroomModel));
        verify(classroomService, times(1)).getById(1L);
        verifyNoMoreInteractions(classroomService);
    }
    
    @Test
    void testCreateForm() throws Exception {
        mockMvc.perform(get("/classrooms/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("classrooms/form"))
                .andExpect(model().attributeExists("classroom"))
                .andExpect(model().attribute("classroom", new ClassroomModel()));
    }
    
    @Test
    void testEdit() throws Exception {
        when(modelMapper.map(classroomModel, Classroom.class)).thenReturn(classroom);
        mockMvc.perform(post("/classrooms/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("name", CLASSROOM_NAME)
                .sessionAttr("classroom", new ClassroomModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/classrooms/1"));
        
        ArgumentCaptor<ClassroomModel> formObjectArgument = ArgumentCaptor.forClass(ClassroomModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Classroom.class));
        verifyNoMoreInteractions(modelMapper); 
        ClassroomModel formObject = formObjectArgument.getValue();
        
        assertAll(
                () -> assertEquals(CLASSROOM_NAME, formObject.getName()), 
                () -> assertEquals(1, formObject.getId())
                ); 
        
        verify(classroomService, times(1)).update(classroom);
        verifyNoMoreInteractions(classroomService);
    }
    
    @Test
    void testCreate() throws Exception {
        ClassroomModel newClassroomModel = new ClassroomModel(null, CLASSROOM_NAME);
        Classroom newClassroom = new Classroom(null, CLASSROOM_NAME);
        
        when(modelMapper.map(newClassroomModel, Classroom.class)).thenReturn(newClassroom);        
        when(classroomService.insert(newClassroom)).thenReturn(classroom);
        
        mockMvc.perform(post("/classrooms/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", CLASSROOM_NAME)
                .sessionAttr("classroom", new ClassroomModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/classrooms"));
        
        ArgumentCaptor<ClassroomModel> formObjectArgument = ArgumentCaptor.forClass(ClassroomModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Classroom.class));
        verifyNoMoreInteractions(modelMapper); 
        ClassroomModel formObject = formObjectArgument.getValue();
        
        assertAll(
                () -> assertEquals(CLASSROOM_NAME, formObject.getName()), 
                () -> assertEquals(null, formObject.getId())
                ); 
        
        verify(classroomService, times(1)).insert(newClassroom);
        verifyNoMoreInteractions(classroomService);
    }
    
    @Test
    void testRemove() throws Exception {
        mockMvc.perform(get("/classrooms/remove/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/classrooms"));
        verify(classroomService, times(1)).delete(1L);
        verifyNoMoreInteractions(classroomService);
    }
}
