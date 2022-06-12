package ua.com.foxminded.university.web;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ua.com.foxminded.university.model.Lesson;
import ua.com.foxminded.university.service.ClassroomService;
import ua.com.foxminded.university.service.CourseService;
import ua.com.foxminded.university.service.LessonService;
import ua.com.foxminded.university.service.PeriodService;
import ua.com.foxminded.university.service.TeacherService;
import ua.com.foxminded.university.web.model.ClassroomModel;
import ua.com.foxminded.university.web.model.CourseModel;
import ua.com.foxminded.university.web.model.LessonDetailModel;
import ua.com.foxminded.university.web.model.LessonListModel;
import ua.com.foxminded.university.web.model.PeriodModel;
import ua.com.foxminded.university.web.model.LessonEditModel;
import ua.com.foxminded.university.web.model.TeacherListModel;

@RequestMapping("/lessons")
@Controller
public class LessonController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LessonController.class);

    private LessonService lessonService;
    private CourseService courseService;
    private PeriodService periodService;
    private ClassroomService classroomService;
    private TeacherService teacherService;
    private ModelMapper modelMapper;

    @GetMapping
    public String list(Model model) {
        LOGGER.debug("Listing lessons");
        List<LessonListModel> lessons = lessonService.getAll()
                .stream()
                .map(lesson -> modelMapper.map(lesson, LessonListModel.class))
                .collect(Collectors.toList());
        model.addAttribute("lessons", lessons);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No. of lessons: {}", lessons.size());
        }
        return "lessons/list";
    }
    
    @GetMapping(value = "/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Showing lesson. id = {}", id);
        }
        LessonDetailModel lessonDetailModel = modelMapper.map(lessonService.getById(id), LessonDetailModel.class);
        model.addAttribute("lesson", lessonDetailModel);
        return "lessons/show";
    }
    
    @GetMapping(value = "/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing lesson. id = {}", id);
        }
        LessonEditModel lessonModel = modelMapper.map(lessonService.getById(id), LessonEditModel.class);
        model.addAttribute("lesson", lessonModel);
        model.addAttribute("courses", getCoursesModel());
        model.addAttribute("periods", getPeriodsModel());
        model.addAttribute("classrooms", getClassroomsModel());
        model.addAttribute("teachers", getTeachersModel());
        return "lessons/form";
    }

    @GetMapping(value = "/new")
    public String createForm(Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Createing lesson");
        }
        LessonEditModel lessonModel = new LessonEditModel();
        model.addAttribute("lesson", lessonModel);
        model.addAttribute("courses", getCoursesModel());
        model.addAttribute("periods", getPeriodsModel());
        model.addAttribute("classrooms", getClassroomsModel());
        model.addAttribute("teachers", getTeachersModel());
        return "lessons/form";
    }

    @PostMapping(value = "/update")
    public String edit(@ModelAttribute("lesson") @Valid LessonEditModel lessonModel, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("courses", getCoursesModel());
            model.addAttribute("periods", getPeriodsModel());
            model.addAttribute("classrooms", getClassroomsModel());
            model.addAttribute("teachers", getTeachersModel());
            return "lessons/form";
        }
        Lesson lesson = modelMapper.map(lessonModel, Lesson.class);
        lessonService.update(lesson);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Lesson has been updated: {}", lesson);
        }
        return "redirect:/lessons/" + lesson.getId();
    }

    @PostMapping(value = "/add")
    public String create(@ModelAttribute("lesson") @Valid LessonEditModel lessonModel, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("courses", getCoursesModel());
            model.addAttribute("periods", getPeriodsModel());
            model.addAttribute("classrooms", getClassroomsModel());
            model.addAttribute("teachers", getTeachersModel());
            return "lessons/form";
        }
        Lesson newLesson = lessonService.insert(modelMapper.map(lessonModel, Lesson.class));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Lesson has been created: {}", newLesson);
        }
        return "redirect:/lessons";
    }

    @GetMapping(value = "/remove/{id}")
    public String remove(@PathVariable("id") Long id) {
        lessonService.delete(id);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Lesson has been deleted. id = {}", id);
        }
        return "redirect:/lessons";
    }
    
    private List<CourseModel> getCoursesModel() {
        return courseService.getAll()
                .stream()
                .map(group -> modelMapper.map(group, CourseModel.class))
                .collect(Collectors.toList());
    }
    
    private List<TeacherListModel> getTeachersModel() {
        return teacherService.getAll()
                .stream()
                .map(teacher -> modelMapper.map(teacher, TeacherListModel.class))
                .collect(Collectors.toList());
    }
    
    private List<PeriodModel> getPeriodsModel() {
        return periodService.getAll()
                .stream()
                .map(period -> modelMapper.map(period, PeriodModel.class))
                .collect(Collectors.toList());
    }
    
    private List<ClassroomModel> getClassroomsModel() {
        return classroomService.getAll()
                .stream()
                .map(classroom -> modelMapper.map(classroom, ClassroomModel.class))
                .collect(Collectors.toList());
    }

    @Autowired
    public void setLessonService(LessonService lessonService) {
        this.lessonService = lessonService;
    }
    
    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @Autowired
    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }

    @Autowired
    public void setClassroomService(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @Autowired
    public void setTeacherService(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
