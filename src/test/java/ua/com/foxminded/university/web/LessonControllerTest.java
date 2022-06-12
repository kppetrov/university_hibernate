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
import java.time.LocalTime;
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
import ua.com.foxminded.university.model.Course;
import ua.com.foxminded.university.model.Lesson;
import ua.com.foxminded.university.model.Period;
import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.service.ClassroomService;
import ua.com.foxminded.university.service.CourseService;
import ua.com.foxminded.university.service.LessonService;
import ua.com.foxminded.university.service.PeriodService;
import ua.com.foxminded.university.service.TeacherService;
import ua.com.foxminded.university.web.model.ClassroomModel;
import ua.com.foxminded.university.web.model.CourseModel;
import ua.com.foxminded.university.web.model.LessonDetailModel;
import ua.com.foxminded.university.web.model.LessonEditModel;
import ua.com.foxminded.university.web.model.LessonListModel;
import ua.com.foxminded.university.web.model.PeriodModel;
import ua.com.foxminded.university.web.model.TeacherListModel;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
class LessonControllerTest {
    private static final String COURSE_NAME = "course";
    private static final String CLASSROOM_NAME = "classroom";
    private static final String PERIOD_NAME = "period";
    private static final String TEACHER_FIRST_NAME = "first_name";
    private static final String TEACHER_LAST_NAME = "last_name";
    
    private MockMvc mockMvc;
    @Mock
    private LessonService lessonService;
    @Mock
    private CourseService courseService;
    @Mock
    private PeriodService periodService;
    @Mock
    private ClassroomService classroomService;
    @Mock
    private TeacherService teacherService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private LessonController controller;

    private Course course = new Course(1L, COURSE_NAME);
    private CourseModel courseModel = new CourseModel(1L, COURSE_NAME);
    private Teacher teacher = new Teacher(1L, TEACHER_FIRST_NAME, TEACHER_LAST_NAME, null, null);
    private TeacherListModel teacherModel = new TeacherListModel(1L, TEACHER_FIRST_NAME, TEACHER_LAST_NAME);
    private Period period = new Period(1L, PERIOD_NAME, LocalTime.of(8, 0), LocalTime.of(9, 30));
    private PeriodModel periodModel = new PeriodModel(1L, PERIOD_NAME, LocalTime.of(8, 0), LocalTime.of(9, 30));
    private Classroom classroom = new Classroom(1L, CLASSROOM_NAME);
    private ClassroomModel classroomModel = new ClassroomModel(1L, CLASSROOM_NAME);
    private Lesson lesson = new Lesson(1L, course, LocalDate.of(2021, 01, 01), period, teacher, classroom);
    private LessonListModel lessonListModel = new LessonListModel(1L, COURSE_NAME, LocalDate.of(2021, 01, 01),
            PERIOD_NAME);
    private LessonDetailModel lessonDetailModel = new LessonDetailModel(0L, COURSE_NAME, LocalDate.of(2021, 01, 01),
            PERIOD_NAME, CLASSROOM_NAME, TEACHER_FIRST_NAME, TEACHER_LAST_NAME);
    private LessonEditModel lessonEditModel = new LessonEditModel(1L, 1L, LocalDate.of(2021, 01, 01), 1L, 1L, 1L);
    private List<Course> courses = Arrays.asList(course);
    private List<Classroom> classrooms = Arrays.asList(classroom);
    private List<Period> periods = Arrays.asList(period);
    private List<Teacher> teachers = Arrays.asList(teacher);    
    private List<CourseModel> courseModels = Arrays.asList(courseModel);
    private List<ClassroomModel> classroomModels = Arrays.asList(classroomModel);
    private List<PeriodModel> periodModels = Arrays.asList(periodModel);
    private  List<TeacherListModel> teacherModels = Arrays.asList(teacherModel);
    
    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Lesson> lessons = Arrays.asList(lesson);
        List<LessonListModel> expected = Arrays.asList(lessonListModel);
        when(lessonService.getAll()).thenReturn(lessons);
        when(modelMapper.map(lesson, LessonListModel.class)).thenReturn(lessonListModel);
        mockMvc.perform(get("/lessons")).andExpect(status().isOk()).andExpect(view().name("lessons/list"))
                .andExpect(model().attributeExists("lessons")).andExpect(model().attribute("lessons", expected));
        verify(lessonService, times(1)).getAll();
        verifyNoMoreInteractions(lessonService);
    }
    
    @Test
    void testShow() throws Exception {
        when(lessonService.getById(1L)).thenReturn(lesson);
        when(modelMapper.map(lesson, LessonDetailModel.class)).thenReturn(lessonDetailModel);
        mockMvc.perform(get("/lessons/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("lessons/show"))
                .andExpect(model().attributeExists("lesson"))
                .andExpect(model().attribute("lesson", lessonDetailModel));
        verify(lessonService, times(1)).getById(1L);
        verifyNoMoreInteractions(lessonService);
    }
    
    @Test
    void testEditForm() throws Exception {                
        when(lessonService.getById(1L)).thenReturn(lesson);
        when(modelMapper.map(lesson, LessonEditModel.class)).thenReturn(lessonEditModel);
        when(courseService.getAll()).thenReturn(courses);
        when(modelMapper.map(course, CourseModel.class)).thenReturn(courseModel);
        when(classroomService.getAll()).thenReturn(classrooms);
        when(modelMapper.map(classroom, ClassroomModel.class)).thenReturn(classroomModel);
        when(periodService.getAll()).thenReturn(periods);
        when(modelMapper.map(period, PeriodModel.class)).thenReturn(periodModel);
        when(teacherService.getAll()).thenReturn(teachers);
        when(modelMapper.map(teacher, TeacherListModel.class)).thenReturn(teacherModel);
        
        mockMvc.perform(get("/lessons/edit/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("lessons/form"))
                .andExpect(model().attributeExists("lesson"))
                .andExpect(model().attribute("lesson", lessonEditModel))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("courses", courseModels))
                .andExpect(model().attributeExists("classrooms"))
                .andExpect(model().attribute("classrooms", classroomModels))
                .andExpect(model().attributeExists("periods"))
                .andExpect(model().attribute("periods", periodModels))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attribute("teachers", teacherModels));
        verify(lessonService, times(1)).getById(1L);
        verifyNoMoreInteractions(lessonService);
    }
    
    @Test
    void testCreateForm() throws Exception {
        when(courseService.getAll()).thenReturn(courses);
        when(modelMapper.map(course, CourseModel.class)).thenReturn(courseModel);
        when(classroomService.getAll()).thenReturn(classrooms);
        when(modelMapper.map(classroom, ClassroomModel.class)).thenReturn(classroomModel);
        when(periodService.getAll()).thenReturn(periods);
        when(modelMapper.map(period, PeriodModel.class)).thenReturn(periodModel);
        when(teacherService.getAll()).thenReturn(teachers);
        when(modelMapper.map(teacher, TeacherListModel.class)).thenReturn(teacherModel);
        
        mockMvc.perform(get("/lessons/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("lessons/form"))
                .andExpect(model().attributeExists("lesson"))
                .andExpect(model().attribute("lesson", new LessonEditModel()))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("courses", courseModels))
                .andExpect(model().attributeExists("classrooms"))
                .andExpect(model().attribute("classrooms", classroomModels))
                .andExpect(model().attributeExists("periods"))
                .andExpect(model().attribute("periods", periodModels))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(model().attribute("teachers", teacherModels));
    }
    
    @Test
    void testEdit() throws Exception {    
        when(modelMapper.map(lessonEditModel, Lesson.class)).thenReturn(lesson);   
        mockMvc.perform(post("/lessons/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("courseId", "1")
                        .param("date", "2021-01-01")
                        .param("periodId", "1")
                        .param("classroomId", "1")
                        .param("teacherId", "1")
                .sessionAttr("lesson", new LessonEditModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/lessons/1"));
        
        ArgumentCaptor<LessonEditModel> formObjectArgument = ArgumentCaptor.forClass(LessonEditModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Lesson.class));
        verifyNoMoreInteractions(modelMapper); 
        LessonEditModel formObject = formObjectArgument.getValue();
      
        assertAll(
                () -> assertEquals(1, formObject.getId()), 
                () -> assertEquals(1, formObject.getCourseId()),
                () -> assertEquals(1, formObject.getPeriodId()),
                () -> assertEquals(1, formObject.getClassroomId()),
                () -> assertEquals(1, formObject.getTeacherId()),
                () -> assertEquals(LocalDate.of(2021, 01, 01), formObject.getDate())
                );
                
        verify(lessonService, times(1)).update(lesson);
        verifyNoMoreInteractions(lessonService);
    }
    
    @Test
    void testCreate() throws Exception {
        when(modelMapper.map(lessonEditModel, Lesson.class)).thenReturn(lesson);   
        when(lessonService.insert(lesson)).thenReturn(lesson);
        mockMvc.perform(post("/lessons/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("courseId", "1")
                        .param("date", "2021-01-01")
                        .param("periodId", "1")
                        .param("classroomId", "1")
                        .param("teacherId", "1")
                .sessionAttr("lesson", new LessonEditModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/lessons"));
        
        ArgumentCaptor<LessonEditModel> formObjectArgument = ArgumentCaptor.forClass(LessonEditModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Lesson.class));
        verifyNoMoreInteractions(modelMapper); 
        LessonEditModel formObject = formObjectArgument.getValue();
      
        assertAll(
                () -> assertEquals(1, formObject.getId()), 
                () -> assertEquals(1, formObject.getCourseId()),
                () -> assertEquals(1, formObject.getPeriodId()),
                () -> assertEquals(1, formObject.getClassroomId()),
                () -> assertEquals(1, formObject.getTeacherId()),
                () -> assertEquals(LocalDate.of(2021, 01, 01), formObject.getDate())
                );
                
        verify(lessonService, times(1)).insert(lesson);
        verifyNoMoreInteractions(lessonService);
    }
    
    @Test
    void testRemove() throws Exception {
        mockMvc.perform(get("/lessons/remove/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/lessons"));
        verify(lessonService, times(1)).delete(1L);
        verifyNoMoreInteractions(lessonService);
    }
}
