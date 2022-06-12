package ua.com.foxminded.university.web.formatter;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import ua.com.foxminded.university.web.model.GroupModel;

public class GroupModelFormatter implements Formatter<GroupModel>{

    @Override
    public String print(GroupModel groupModel, Locale locale) {
        return String.valueOf(groupModel.getId());
    }

    @Override
    public GroupModel parse(String id, Locale locale) throws ParseException {
        GroupModel groupModel = new GroupModel();
        groupModel.setId(Long.parseLong(id));
        return groupModel;
    }
}
