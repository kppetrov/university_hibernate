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

import ua.com.foxminded.university.model.Course;
import ua.com.foxminded.university.service.CourseService;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.web.model.CourseModel;
import ua.com.foxminded.university.web.model.CourseModelWithGroups;
import ua.com.foxminded.university.web.model.GroupModel;

@RequestMapping("/courses")
@Controller
public class CourseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    private CourseService courseService;
    private GroupService groupService;
    private ModelMapper modelMapper;

    @GetMapping
    public String list(Model model) {
        LOGGER.debug("Listing courses");
        List<CourseModel> courses = courseService.getAll()
                .stream()
                .map(course -> modelMapper.map(course, CourseModel.class))
                .collect(Collectors.toList());
        model.addAttribute("courses", courses);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No. of courses: {}", courses.size());
        }
        return "courses/list";
    }
    
    @GetMapping(value = "/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Showing course. id = {}", id);
        }
        CourseModelWithGroups course = modelMapper.map(courseService.getByIdWithDetail(id), CourseModelWithGroups.class);
        model.addAttribute("course", course);
        return "courses/show";
    }  
    
    @GetMapping(value = "/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing course. id = {}", id);
        }
        CourseModelWithGroups course = modelMapper.map(courseService.getByIdWithDetail(id), CourseModelWithGroups.class);
        model.addAttribute("course", course);
        model.addAttribute("allGroups", getGroupsModel());
        return "courses/updateForm";
    }
    
    @GetMapping(value = "/new")
    public String createForm(Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Createing course");
        }
        CourseModel course = new CourseModel();
        model.addAttribute("course", course);
        return "courses/newForm";
    }
    
    @PostMapping(value = "/update")
    public String edit(@ModelAttribute("course") @Valid CourseModelWithGroups courseModel, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("allGroups", getGroupsModel());
            return "courses/updateForm";
        }
        Course course = modelMapper.map(courseModel, Course.class);
        courseService.update(course);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Course has been updated: {}", course);
        }
        return "redirect:/courses/" + course.getId();
    }
    
    @PostMapping(value = "/add")
    public String create(@ModelAttribute("course") @Valid CourseModel courseModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "courses/newForm";
        }
        Course newCourse = courseService.insert(modelMapper.map(courseModel, Course.class));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Course has been created: {}", newCourse);
        }
        return "redirect:/courses";
    }
    
    @GetMapping(value = "/remove/{id}")
    public String remove(@PathVariable("id") Long id) {
        courseService.delete(id);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Course has been deleted. id = {}", id);
        }
        return "redirect:/courses";
    }
    
    private List<GroupModel> getGroupsModel() {
        return groupService.getAll()
                .stream()
                .map(group -> modelMapper.map(group, GroupModel.class))
                .collect(Collectors.toList());
    }
    
    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }    
}
