package ua.com.foxminded.university.web.model;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonEditModel {
    private int id;
    private int courseId; 
    @DateTimeFormat(iso = ISO.DATE)
    @NotNull(message = "{validation.lesson.date.NotNull.message}")
    private LocalDate date;
    private int periodId;
    private int classroomId;
    private int teacherId;
}
