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

import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.web.model.GroupModel;

@RequestMapping("/groups")
@Controller
public class GroupController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

    private GroupService groupService;
    private ModelMapper modelMapper;

    @GetMapping
    public String list(Model model) {
        LOGGER.debug("Listing groups");
        List<GroupModel> groups = groupService.getAll()
                .stream()
                .map(group -> modelMapper.map(group, GroupModel.class))
                .collect(Collectors.toList());
        model.addAttribute("groups", groups);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No. of groups: {}", groups.size());
        }
        return "groups/list";
    }
    
    @GetMapping(value = "/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Showing group. id = {}", id);
        }
        GroupModel group = modelMapper.map(groupService.getById(id), GroupModel.class);
        model.addAttribute("group", group);
        return "groups/show";
    }  
    
    @GetMapping(value = "/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing group. id = {}", id);
        }
        GroupModel group = modelMapper.map(groupService.getById(id), GroupModel.class);
        model.addAttribute("group", group);
        return "groups/form";
    }
    
    @GetMapping(value = "/new")
    public String createForm(Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Createing group");
        }
        GroupModel group = new GroupModel();
        model.addAttribute("group", group);
        return "groups/form";
    }
    
    @PostMapping(value = "/update")
    public String edit(@ModelAttribute("group") @Valid GroupModel groupModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "groups/form";
        }
        Group group = modelMapper.map(groupModel, Group.class);
        groupService.update(group);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Group has been updated: {}", group);
        }
        return "redirect:/groups/" + group.getId();
    }
    
    @PostMapping(value = "/add")
    public String create(@ModelAttribute("group") @Valid GroupModel groupModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "groups/form";
        }
        Group newGroup = groupService.insert(modelMapper.map(groupModel, Group.class));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Group has been created: {}", newGroup);
        }
        return "redirect:/groups";
    }
    
    @GetMapping(value = "/remove/{id}")
    public String remove(@PathVariable("id") Long id) {
        groupService.delete(id);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Group has been deleted. id = {}", id);
        }
        return "redirect:/groups";
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
