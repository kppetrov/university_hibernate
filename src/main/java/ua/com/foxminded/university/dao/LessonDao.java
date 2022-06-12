package ua.com.foxminded.university.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ua.com.foxminded.university.model.Lesson;

public interface LessonDao extends GenericDao<Lesson> {
    public List<Lesson> getByCourseId(Long curseId);
    public List<Lesson> getByGroupId(Long groupId);
    public List<Lesson> getByTeacherId(Long teacherId);
    public Optional<Lesson> getByDatePeriodIdTeacherId(LocalDate date, Long periodId, Long teacherId);
    public Optional<Lesson> getByDatePeriodIdClassroomId(LocalDate date, Long periodId, Long classroomId);
}
