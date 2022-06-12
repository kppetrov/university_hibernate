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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ua.com.foxminded.university.config.WebConfig;
import ua.com.foxminded.university.model.Group;
import ua.com.foxminded.university.service.GroupService;
import ua.com.foxminded.university.web.model.GroupModel;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
class GroupControllerTest {
    private static final String GROUP_NAME = "group";
    
    private MockMvc mockMvc;
    @Mock
    private GroupService groupService;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    private GroupController controller;

    private Group group = new Group(1L, GROUP_NAME);
    private GroupModel groupModel = new GroupModel(1L, GROUP_NAME);

    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAll() throws Exception {
        List<Group> groups = Arrays.asList(group);
        List<GroupModel> expected = Arrays.asList(groupModel);
        when(groupService.getAll()).thenReturn(groups);
        when(modelMapper.map(group, GroupModel.class)).thenReturn(groupModel);
        mockMvc.perform(get("/groups"))
                .andExpect(status().isOk())
                .andExpect(view().name("groups/list"))
                .andExpect(model().attributeExists("groups"))
                .andExpect(model().attribute("groups", expected));
        verify(groupService, times(1)).getAll();
        verifyNoMoreInteractions(groupService);
    }   

    
    @Test
    void testShow() throws Exception {
        when(groupService.getById(1L)).thenReturn(group);
        when(modelMapper.map(group, GroupModel.class)).thenReturn(groupModel);
        mockMvc.perform(get("/groups/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("groups/show"))
                .andExpect(model().attributeExists("group"))
                .andExpect(model().attribute("group", groupModel));
        verify(groupService, times(1)).getById(1L);
        verifyNoMoreInteractions(groupService);
    }
    
    @Test
    void testEditForm() throws Exception {
        when(groupService.getById(1L)).thenReturn(group);
        when(modelMapper.map(group, GroupModel.class)).thenReturn(groupModel);
        mockMvc.perform(get("/groups/edit/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("groups/form"))
                .andExpect(model().attributeExists("group"))
                .andExpect(model().attribute("group", groupModel));
        verify(groupService, times(1)).getById(1L);
        verifyNoMoreInteractions(groupService);
    }
    
    @Test
    void testCreateForm() throws Exception {
        mockMvc.perform(get("/groups/new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("groups/form"))
                .andExpect(model().attributeExists("group"))
                .andExpect(model().attribute("group", new GroupModel()));
    }
    
    @Test
    void testEdit() throws Exception {      
        when(modelMapper.map(groupModel, Group.class)).thenReturn(group);   
        mockMvc.perform(post("/groups/update")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("name", GROUP_NAME)
                .sessionAttr("group", new GroupModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/groups/1"));
        
        ArgumentCaptor<GroupModel> formObjectArgument = ArgumentCaptor.forClass(GroupModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Group.class));
        verifyNoMoreInteractions(modelMapper); 
        GroupModel formObject = formObjectArgument.getValue();
      
        assertAll(
                () -> assertEquals(GROUP_NAME, formObject.getName()), 
                () -> assertEquals(1, formObject.getId())
                );
                
        verify(groupService, times(1)).update(group);
        verifyNoMoreInteractions(groupService);
    }
    
    @Test
    void testCreate() throws Exception {
        Group newGroup = new Group(null, GROUP_NAME);
        GroupModel newGroupModel = new GroupModel(null, GROUP_NAME);
      
        when(modelMapper.map(newGroupModel, Group.class)).thenReturn(newGroup);
        when(groupService.insert(newGroup)).thenReturn(newGroup);
        
        mockMvc.perform(post("/groups/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", GROUP_NAME)
                .sessionAttr("group", new GroupModel()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/groups"));
        
        ArgumentCaptor<GroupModel> formObjectArgument = ArgumentCaptor.forClass(GroupModel.class);
        verify(modelMapper, times(1)).map(formObjectArgument.capture(), eq(Group.class));
        verifyNoMoreInteractions(modelMapper);
        GroupModel formObject = formObjectArgument.getValue();
        
        assertAll(
                () -> assertEquals(GROUP_NAME, formObject.getName()), 
                () -> assertEquals(null, formObject.getId())
                ); 
        
        verify(groupService, times(1)).insert(newGroup);
        verifyNoMoreInteractions(groupService);
    }
    
    @Test
    void testRemove() throws Exception {
        mockMvc.perform(get("/groups/remove/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/groups"));
        verify(groupService, times(1)).delete(1L);
        verifyNoMoreInteractions(groupService);
    }
}
