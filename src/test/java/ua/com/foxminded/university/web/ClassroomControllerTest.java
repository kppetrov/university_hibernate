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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ua.com.foxminded.university.config.WebConfig;
import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.service.ClassroomService;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
class ClassroomControllerTest {
    private static final String CLASSROOM_NAME = "lassroom name";
    
    private MockMvc mockMvc;
    @Mock
    private ClassroomService classroomService;
    @InjectMocks
    private ClassroomController controller;

    private Classroom classroom = new Classroom(1, CLASSROOM_NAME);

    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testList() throws Exception {
        List<Classroom> expected = Arrays.asList(classroom);
        when(classroomService.getAll()).thenReturn(expected);
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
        when(classroomService.getById(1)).thenReturn(classroom);
        mockMvc.perform(get("/classrooms/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("classrooms/show"))
                .andExpect(model().attributeExists("classroom"))
                .andExpect(model().attribute("classroom", classroom));
        verify(classroomService, times(1)).getById(1);
        verifyNoMoreInteractions(classroomService);
    }
    
    @Test
    void testEditForm() throws Exception {
        when(classroomService.getById(1)).thenReturn(classroom);
        mockMvc.perform(get("/classrooms/edit/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("classrooms/form"))
                .andExpect(model().attributeExists("classroom"))
                .andExpect(model().attribute("classroom", classroom));
        verify(classroomService, times(1)).getById(1);
        verifyNoMoreInteractions(classroomService);
    }
    
    @Test
    void testCreateForm() throws Exception {
        mockMvc.perform(get("/classrooms/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("classrooms/form"))
                .andExpect(model().attributeExists("classroom"))
                .andExpect(model().attribute("classroom", new Classroom()));
    }
    
    @Test
    void testEdit() throws Exception {
        mockMvc.perform(post("/classrooms/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("name", CLASSROOM_NAME)
                .sessionAttr("classroom", new Classroom()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/classrooms/1"));
        
        ArgumentCaptor<Classroom> formObjectArgument = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomService, times(1)).update(formObjectArgument.capture());
        verifyNoMoreInteractions(classroomService);
 
        Classroom formObject = formObjectArgument.getValue();
        
        assertAll(
                () -> assertEquals(CLASSROOM_NAME, formObject.getName()), 
                () -> assertEquals(1, formObject.getId())
                ); 
    }
    
    @Test
    void testCreate() throws Exception {
        when(classroomService.insert(isA(Classroom.class))).thenReturn(classroom);
        mockMvc.perform(post("/classrooms/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", CLASSROOM_NAME)
                .sessionAttr("classroom", new Classroom()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/classrooms"));
        
        ArgumentCaptor<Classroom> formObjectArgument = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomService, times(1)).insert(formObjectArgument.capture());
        verifyNoMoreInteractions(classroomService);
 
        Classroom formObject = formObjectArgument.getValue();
        
        assertAll(
                () -> assertEquals(CLASSROOM_NAME, formObject.getName()), 
                () -> assertEquals(0, formObject.getId())
                ); 
    }
    
    @Test
    void testRemove() throws Exception {
        mockMvc.perform(get("/classrooms/remove/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/classrooms"));
        verify(classroomService, times(1)).delete(1);
        verifyNoMoreInteractions(classroomService);
    }
}
