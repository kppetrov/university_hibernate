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
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ua.com.foxminded.university.config.WebConfig;
import ua.com.foxminded.university.model.Course;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.service.CourseService;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.web.formatter.GroupModelFormatter;
import ua.com.foxminded.university.web.model.CourseModel;
import ua.com.foxminded.university.web.model.CourseModelWithGroups;
import ua.com.foxminded.university.web.model.GroupModel;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
class CourseControllerTest {
    private static final String COURSE_NAME = "course";
    private static final String GROUP_NAME = "group";
    private static final String GROUP_NAME2 = "group2";
    
    private MockMvc mockMvc;
    @Mock
    private CourseService courseService;
    @Mock
    private GroupService groupService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CourseController controller;
    
    private FormattingConversionService formattingConversionService = new FormattingConversionService();
    private GroupModelFormatter groupModelFormatter = new GroupModelFormatter();

    private Group group = new Group(1, GROUP_NAME);
    private GroupModel groupModel = new GroupModel(1, GROUP_NAME);
    private Group group2 = new Group(2, GROUP_NAME2);
    private GroupModel groupModel2 = new GroupModel(2, GROUP_NAME);
    private Course course = new Course(1, COURSE_NAME, null, Arrays.asList(group, group2));
    private CourseModelWithGroups courseModelWithGroups = new CourseModelWithGroups(1, COURSE_NAME, Arrays.asList(groupModel, groupModel2));
    private CourseModel courseModel = new CourseModel(1, COURSE_NAME); 

    @BeforeEach
    public void beforeEach() throws Exception {
        formattingConversionService.addFormatter(groupModelFormatter);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setConversionService(formattingConversionService).build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Course> courses = Arrays.asList(course);
        List<CourseModel> expected = Arrays.asList(courseModel);
        when(courseService.getAll()).thenReturn(courses);    
        when(modelMapper.map(course, CourseModel.class)).thenReturn(courseModel);
        mockMvc.perform(get("/courses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("courses/list"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("courses", expected));
        verify(courseService, times(1)).getAll();
        verifyNoMoreInteractions(courseService);
    }
    
    @Test
    void testShow() throws Exception {
        when(courseService.getByIdWithDetail(1)).thenReturn(course);
        when(modelMapper.map(course, CourseModelWithGroups.class)).thenReturn(courseModelWithGroups);
        mockMvc.perform(get("/courses/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("courses/show"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attribute("course", courseModelWithGroups));
        verify(courseService, times(1)).getByIdWithDetail(1);
        verifyNoMoreInteractions(courseService);
    }
    
    @Test
    void testEditForm() throws Exception {
        List<Group> groups = Arrays.asList(group);
        List<GroupModel> groupModels = Arrays.asList(groupModel);
        when(groupService.getAll()).thenReturn(groups);
        when(modelMapper.map(group, GroupModel.class)).thenReturn(groupModel);
        when(courseService.getByIdWithDetail(1)).thenReturn(course);
        when(modelMapper.map(course, CourseModelWithGroups.class)).thenReturn(courseModelWithGroups);
        mockMvc.perform(get("/courses/edit/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("courses/updateForm"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attribute("course", courseModelWithGroups))
                .andExpect(model().attributeExists("allGroups"))
                .andExpect(model().attribute("allGroups", groupModels));
        verify(courseService, times(1)).getByIdWithDetail(1);
        verifyNoMoreInteractions(courseService);
        verify(groupService, times(1)).getAll();
        verifyNoMoreInteractions(groupService);
    }
    
    @Test
    void testCreateForm() throws Exception {
        mockMvc.perform(get("/courses/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("courses/newForm"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attribute("course", new CourseModel()));
    }
    
    @Test
    void testEdit() throws Exception {
        
        List<GroupModel> expectedGroups = Arrays.asList(new GroupModel(1, null), new GroupModel(2, null));
        CourseModelWithGroups courseModel = new CourseModelWithGroups(1, COURSE_NAME, expectedGroups);
      
        when(modelMapper.map(courseModel, Course.class)).thenReturn(course);
        
        List<String> values = Arrays.asList("1", "2");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("groups", values);
        
        mockMvc.perform(post("/courses/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("name", COURSE_NAME)
                        .params(params)
                .sessionAttr("course", new CourseModelWithGroups()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/courses/1"));
        
        ArgumentCaptor<CourseModelWithGroups> formObjectArgument = ArgumentCaptor.forClass(CourseModelWithGroups.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Course.class));
        verifyNoMoreInteractions(modelMapper); 
        CourseModelWithGroups formObject = formObjectArgument.getValue();
      
        assertAll(
                () -> assertEquals(COURSE_NAME, formObject.getName()), 
                () -> assertEquals(1, formObject.getId()),
                () -> assertEquals(expectedGroups, formObject.getGroups())
                );
                
        verify(courseService, times(1)).update(course);
        verifyNoMoreInteractions(courseService);
    }
    
    @Test
    void testCreate() throws Exception {
        Course newCourse = new Course(0, COURSE_NAME);
        CourseModel newCourseModel = new CourseModel(0, COURSE_NAME);
      
        when(modelMapper.map(newCourseModel, Course.class)).thenReturn(newCourse);
        when(courseService.insert(newCourse)).thenReturn(course);
        
        mockMvc.perform(post("/courses/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", COURSE_NAME)
                .sessionAttr("course", new CourseModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/courses"));
        
        ArgumentCaptor<CourseModel> formObjectArgument = ArgumentCaptor.forClass(CourseModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Course.class));
        verifyNoMoreInteractions(modelMapper);
        CourseModel formObject = formObjectArgument.getValue();
        
        assertAll(
                () -> assertEquals(COURSE_NAME, formObject.getName()), 
                () -> assertEquals(0, formObject.getId())
                ); 
    }
    
    @Test
    void testRemove() throws Exception {
        mockMvc.perform(get("/courses/remove/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/courses"));
        verify(courseService, times(1)).delete(1);
        verifyNoMoreInteractions(courseService);
    }
}

