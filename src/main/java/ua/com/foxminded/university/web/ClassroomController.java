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

import ua.com.foxminded.university.model.Classroom;
import ua.com.foxminded.university.service.ClassroomService;
import ua.com.foxminded.university.web.model.ClassroomModel;

@RequestMapping("/classrooms")
@Controller
public class ClassroomController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassroomController.class);
    
    private ClassroomService classroomService;
    private ModelMapper modelMapper;
    
    @GetMapping
    public String list(Model model) {
        LOGGER.debug("Listing classrooms");
        List<ClassroomModel> classrooms = classroomService.getAll()
                .stream()
                .map(classroom -> modelMapper.map(classroom, ClassroomModel.class))
                .collect(Collectors.toList());
        model.addAttribute("classrooms", classrooms);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No. of classrooms: {}", classrooms.size());
        }
        return "classrooms/list";
    }   
    
    @GetMapping(value = "/{id}")
    public String show(@PathVariable("id") Integer id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Showing classroom. id = {}", id);
        }
        ClassroomModel classroom = modelMapper.map(classroomService.getById(id), ClassroomModel.class);
        model.addAttribute("classroom", classroom);
        return "classrooms/show";
    }  
    
    @GetMapping(value = "/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing classroom. id = {}", id);
        }
        ClassroomModel classroom = modelMapper.map(classroomService.getById(id), ClassroomModel.class);
        model.addAttribute("classroom", classroom);
        return "classrooms/form";
    }
    
    @GetMapping(value = "/new")
    public String createForm(Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Createing classroom");
        }
        ClassroomModel classroom = new ClassroomModel();
        model.addAttribute("classroom", classroom);
        return "classrooms/form";
    }
    
    @PostMapping(value = "/update")
    public String edit(@ModelAttribute("classroom") @Valid ClassroomModel classroomModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "classrooms/form";
        }
        Classroom classroom = modelMapper.map(classroomModel, Classroom.class);
        classroomService.update(classroom);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Classroom has been updated: {}", classroom);
        }
        return "redirect:/classrooms/" + classroom.getId();
    }
    
    @PostMapping(value = "/add")
    public String create(@ModelAttribute("classroom") @Valid ClassroomModel classroomModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "classrooms/form";
        }
        Classroom newClassroom = classroomService.insert(modelMapper.map(classroomModel, Classroom.class));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Classroom has been created: {}", newClassroom);
        }
        return "redirect:/classrooms";
    }
    
    @GetMapping(value = "/remove/{id}")
    public String remove(@PathVariable("id") Integer id) {
        classroomService.delete(id);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Classroom has been deleted. id = {}", id);
        }
        return "redirect:/classrooms";
    }    
    
    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Autowired
    public void setClassroomService(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }
}
