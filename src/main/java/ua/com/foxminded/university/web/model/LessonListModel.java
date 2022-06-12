package ua.com.foxminded.university.web.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonListModel {
    private Long id;
    private String courseName;
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate date;
    private String periodName;
}
