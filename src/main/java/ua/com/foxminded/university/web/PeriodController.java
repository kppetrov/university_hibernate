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

import ua.com.foxminded.university.model.Period;
import ua.com.foxminded.university.service.PeriodService;
import ua.com.foxminded.university.web.model.PeriodModel;

@RequestMapping("/periods")
@Controller
public class PeriodController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodController.class);

    private PeriodService periodService;
    private ModelMapper modelMapper;

    @GetMapping
    public String list(Model model) {
        LOGGER.debug("Listing periods");
        List<PeriodModel> periods = periodService.getAll()
                .stream()
                .map(period -> modelMapper.map(period, PeriodModel.class))
                .collect(Collectors.toList());
        model.addAttribute("periods", periods);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No. of periods: {}", periods.size());
        }
        return "periods/list";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Showing period. id = {}", id);
        }
        PeriodModel period = modelMapper.map(periodService.getById(id), PeriodModel.class);
        model.addAttribute("period", period);
        return "periods/show";
    }  
    
    @GetMapping(value = "/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Editing period. id = {}", id);
        }
        PeriodModel period = modelMapper.map(periodService.getById(id), PeriodModel.class);
        model.addAttribute("period", period);
        return "periods/form";
    }
    
    @GetMapping(value = "/new")
    public String createForm(Model model) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Createing period");
        }
        PeriodModel period = new PeriodModel();
        model.addAttribute("period", period);
        return "periods/form";
    }
    
    @PostMapping(value = "/update")
    public String edit(@ModelAttribute("period") @Valid PeriodModel periodModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "periods/form";
        }
        Period period = modelMapper.map(periodModel, Period.class);
        periodService.update(period);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Period has been updated: {}", period);
        }
        return "redirect:/periods/" + period.getId();
    }
    
    @PostMapping(value = "/add")
    public String create(@ModelAttribute("period") @Valid PeriodModel periodModel, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "periods/form";
        }
        Period newPeriod = periodService.insert(modelMapper.map(periodModel, Period.class));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Period has been created: {}", newPeriod);
        }
        return "redirect:/periods";
    }
    
    @GetMapping(value = "/remove/{id}")
    public String remove(@PathVariable("id") Long id) {
        periodService.delete(id);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Period has been deleted. id = {}", id);
        }
        return "redirect:/periods";
    }
    
    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    
    @Autowired
    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }
}
