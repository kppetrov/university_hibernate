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
import ua.com.foxminded.university.model.Period;
import ua.com.foxminded.university.service.PeriodService;
import ua.com.foxminded.university.web.model.PeriodModel;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
class PeriodControllerTest {
    private static final String PERIOD_NAME = "period";
    
    private MockMvc mockMvc;
    @Mock
    private PeriodService periodService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PeriodController controller;

    private Period period = new Period(1L, PERIOD_NAME, LocalTime.of(8, 0), LocalTime.of(9, 30));
    private PeriodModel periodModel = new PeriodModel(1L, PERIOD_NAME, LocalTime.of(8, 0), LocalTime.of(9, 30));

    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Period> periods = Arrays.asList(period);
        List<PeriodModel> expected = Arrays.asList(periodModel);
        when(periodService.getAll()).thenReturn(periods);
        when(modelMapper.map(period, PeriodModel.class)).thenReturn(periodModel);
        mockMvc.perform(get("/periods"))
                .andExpect(status().isOk())
                .andExpect(view().name("periods/list"))
                .andExpect(model().attributeExists("periods"))
                .andExpect(model().attribute("periods", expected));
        verify(periodService, times(1)).getAll();
        verifyNoMoreInteractions(periodService);
    }
    
    @Test
    void testShow() throws Exception {
        when(periodService.getById(1L)).thenReturn(period);
        when(modelMapper.map(period, PeriodModel.class)).thenReturn(periodModel);
        mockMvc.perform(get("/periods/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("periods/show"))
                .andExpect(model().attributeExists("period"))
                .andExpect(model().attribute("period", periodModel));
        verify(periodService, times(1)).getById(1L);
        verifyNoMoreInteractions(periodService);
    }
    
    @Test
    void testEditForm() throws Exception {
        when(periodService.getById(1L)).thenReturn(period);
        when(modelMapper.map(period, PeriodModel.class)).thenReturn(periodModel);
        mockMvc.perform(get("/periods/edit/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("periods/form"))
                .andExpect(model().attributeExists("period"))
                .andExpect(model().attribute("period", periodModel));
        verify(periodService, times(1)).getById(1L);
        verifyNoMoreInteractions(periodService);
    }
    
    @Test
    void testCreateForm() throws Exception {
        mockMvc.perform(get("/periods/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("periods/form"))
                .andExpect(model().attributeExists("period"))
                .andExpect(model().attribute("period", new PeriodModel()));
    }
    
    @Test
    void testEdit() throws Exception {      
        when(modelMapper.map(periodModel, Period.class)).thenReturn(period);   
        mockMvc.perform(post("/periods/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("name", PERIOD_NAME)
                        .param("start", "08:00")
                        .param("end", "09:30")
                .sessionAttr("period", new PeriodModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/periods/1"));
        
        ArgumentCaptor<PeriodModel> formObjectArgument = ArgumentCaptor.forClass(PeriodModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Period.class));
        verifyNoMoreInteractions(modelMapper); 
        PeriodModel formObject = formObjectArgument.getValue();
      
        assertAll(
                () -> assertEquals(PERIOD_NAME, formObject.getName()), 
                () -> assertEquals(1, formObject.getId())
                );
                
        verify(periodService, times(1)).update(period);
        verifyNoMoreInteractions(periodService);
    }
    
    @Test
    void testCreate() throws Exception {
        Period newPeriod = new Period(null, PERIOD_NAME, LocalTime.of(8, 0), LocalTime.of(9, 30));
        PeriodModel newPeriodModel = new PeriodModel(null, PERIOD_NAME, LocalTime.of(8, 0), LocalTime.of(9, 30));
      
        when(modelMapper.map(newPeriodModel, Period.class)).thenReturn(newPeriod);
        when(periodService.insert(newPeriod)).thenReturn(newPeriod);
        
        mockMvc.perform(post("/periods/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", PERIOD_NAME)
                        .param("start", "08:00")
                        .param("end", "09:30")
                .sessionAttr("period", new PeriodModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/periods"));
        
        ArgumentCaptor<PeriodModel> formObjectArgument = ArgumentCaptor.forClass(PeriodModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Period.class));
        verifyNoMoreInteractions(modelMapper);
        PeriodModel formObject = formObjectArgument.getValue();
        
        assertAll(
                () -> assertEquals(PERIOD_NAME, formObject.getName()), 
                () -> assertEquals(null, formObject.getId())
                ); 
        
        verify(periodService, times(1)).insert(newPeriod);
        verifyNoMoreInteractions(periodService);
    }
    
    @Test
    void testRemove() throws Exception {
        mockMvc.perform(get("/periods/remove/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/periods"));
        verify(periodService, times(1)).delete(1L);
        verifyNoMoreInteractions(periodService);
    }
}
