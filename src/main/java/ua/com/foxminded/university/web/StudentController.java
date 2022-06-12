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

import ua.com.foxminded.university.model.Student;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.service.StudentService;
import ua.com.foxminded.university.web.model.GroupModel;
import ua.com.foxminded.university.web.model.StudentListModel;
import ua.com.foxminded.university.web.model.StudentModel;

@RequestMapping("/students")
@Controller
public class StudentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

    private StudentService studentService;
    private GroupService groupService;
    private ModelMapper modelMapper;

    @GetMapping
    public String list(Model model) {
        LOGGER.debug("Listing students");
        List<StudentListModel> students = studentService.getAll()
                .stream()
                .map(student -> modelMapper.map(student, StudentListModel.class))
                .collect(Collectors.toList());
        model.addAttribute("students", students);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No. of students: {}", students.size());
        }
        return "students/list";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Showing student. id = {}", id);
        }
        StudentModel studentModel = modelMapper.map(studentService.getById(id), StudentModel.class);
        model.addAttribute("student", studentModel);
        return "students/show";
    }

    @GetMapping(value = "/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing student. id = {}", id);
        }
        StudentModel studentModel = modelMapper.map(studentService.getById(id), StudentModel.class);
        model.addAttribute("student", studentModel);
        model.addAttribute("groups", getGroupsModel());
        return "students/form";
    }

    @GetMapping(value = "/new")
    public String createForm(Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Createing student");
        }
        StudentModel studentModel = new StudentModel();
        model.addAttribute("student", studentModel);
        model.addAttribute("groups", getGroupsModel());
        return "students/form";
    }

    @PostMapping(value = "/update")
    public String edit(@ModelAttribute("student") @Valid StudentModel studentModel, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("groups", getGroupsModel());
            return "students/form";
        }
        Student student = modelMapper.map(studentModel, Student.class);
        studentService.update(student);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Student has been updated: {}", student);
        }
        return "redirect:/students/" + student.getId();
    }

    @PostMapping(value = "/add")
    public String create(@ModelAttribute("student") @Valid StudentModel studentModel, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("groups", getGroupsModel());
            return "students/form";
        }
        Student newStudent = studentService.insert(modelMapper.map(studentModel, Student.class));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Student has been created: {}", newStudent);
        }
        return "redirect:/students";
    }

    @GetMapping(value = "/remove/{id}")
    public String remove(@PathVariable("id") Long id) {
        studentService.delete(id);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Student has been deleted. id = {}", id);
        }
        return "redirect:/students";
    }
    
    private List<GroupModel> getGroupsModel() {
        return groupService.getAll()
                .stream()
                .map(group -> modelMapper.map(group, GroupModel.class))
                .collect(Collectors.toList());
    }

    @Autowired
    public void setStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    @Autowired
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
