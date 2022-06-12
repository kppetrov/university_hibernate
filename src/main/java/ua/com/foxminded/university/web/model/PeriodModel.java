package ua.com.foxminded.university.web.model;

import java.time.LocalTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodModel {
    private Long id;
    @NotBlank(message="{validation.name.NotBlank.message}")
    @Size(min=3, max=25, message="{validation.name.Size.message}")
    private String name;
    @DateTimeFormat(iso = ISO.TIME)
    @NotNull(message = "{validation.period.start.NotNull.message}")
    private LocalTime start;
    @DateTimeFormat(iso = ISO.TIME)
    @NotNull(message = "{validation.period.end.NotNull.message}")
    private LocalTime end; 
}
