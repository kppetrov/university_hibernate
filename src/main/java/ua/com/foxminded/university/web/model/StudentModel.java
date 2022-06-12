package ua.com.foxminded.university.web.model;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.foxminded.university.model.Gender;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentModel {
    private Long id;
    @NotBlank(message = "{validation.person.firstName.NotBlank.message}")
    private String firstName;
    @NotBlank(message = "{validation.person.lastName.NotBlank.message}")
    private String lastName;
    private Gender gender;
    @DateTimeFormat(iso = ISO.DATE)
    @NotNull(message = "{validation.person.birthdate.NotNull.message}")
    private LocalDate birthdate;
    private Long groupId;
    private String groupName; 
}
