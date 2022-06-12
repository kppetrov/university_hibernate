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

import ua.com.foxminded.university.model.Teacher;
import ua.com.foxminded.university.service.TeacherService;
import ua.com.foxminded.university.web.model.TeacherListModel;
import ua.com.foxminded.university.web.model.TeacherModel;

@RequestMapping("/teachers")
@Controller
public class TeacherController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeacherController.class);

    private TeacherService teacherService;
    private ModelMapper modelMapper;

    @GetMapping
    public String list(Model model) {
        LOGGER.debug("Listing teachers");
        List<TeacherListModel> teachers = teacherService.getAll()
                .stream()
                .map(teacher -> modelMapper.map(teacher, TeacherListModel.class))
                .collect(Collectors.toList());
        model.addAttribute("teachers", teachers);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No. of teachers: {}", teachers.size());
        }
        return "teachers/list";
    }
    
    @GetMapping(value = "/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Showing teacher. id = {}", id);
        }
        TeacherModel teacherModel = modelMapper.map(teacherService.getById(id), TeacherModel.class);
        model.addAttribute("teacher", teacherModel);
        return "teachers/show";
    }

    @GetMapping(value = "/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing teacher. id = {}", id);
        }
        TeacherModel teacherModel = modelMapper.map(teacherService.getById(id), TeacherModel.class);
        model.addAttribute("teacher", teacherModel);
        return "teachers/form";
    }

    @GetMapping(value = "/new")
    public String createForm(Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Createing teacher");
        }
        TeacherModel teacherModel = new TeacherModel();
        model.addAttribute("teacher", teacherModel);
        return "teachers/form";
    }

    @PostMapping(value = "/update")
    public String edit(@ModelAttribute("teacher") @Valid TeacherModel teacherModel, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "teachers/form";
        }
        Teacher teacher = modelMapper.map(teacherModel, Teacher.class);
        teacherService.update(teacher);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Teacher has been updated: {}", teacher);
        }
        return "redirect:/teachers/" + teacher.getId();
    }

    @PostMapping(value = "/add")
    public String create(@ModelAttribute("teacher") @Valid TeacherModel teacherModel, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "teachers/form";
        }
        Teacher newTeacher = teacherService.insert(modelMapper.map(teacherModel, Teacher.class));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Teacher has been created: {}", newTeacher);
        }
        return "redirect:/teachers";
    }

    @GetMapping(value = "/remove/{id}")
    public String remove(@PathVariable("id") Long id) {
        teacherService.delete(id);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Teacher has been deleted. id = {}", id);
        }
        return "redirect:/teachers";
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
