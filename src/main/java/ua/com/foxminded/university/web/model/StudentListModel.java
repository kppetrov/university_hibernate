package ua.com.foxminded.university.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentListModel {
    private Long id;
    private String firstName;
    private String lastName;
    private String groupName; 
}
