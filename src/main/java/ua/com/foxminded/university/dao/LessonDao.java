package ua.com.foxminded.university.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ua.com.foxminded.university.model.Lesson;

public interface LessonDao extends GenericDao<Lesson> {
    public List<Lesson> getByCourseId(int curseId);
    public List<Lesson> getByGroupId(int groupId);
    public List<Lesson> getByTeacherId(int teacherId);
    public Optional<Lesson> getByDatePeriodIdTeacherId(LocalDate date, int periodId, int teacherId);
    public Optional<Lesson> getByDatePeriodIdClassroomId(LocalDate date, int periodId, int classroomId);
}
