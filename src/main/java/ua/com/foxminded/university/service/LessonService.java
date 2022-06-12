package ua.com.foxminded.university.service;

import java.util.List;

import ua.com.foxminded.university.model.Lesson;

public interface LessonService extends GenericService<Lesson>{
    public List<Lesson> getByCourseId(Long curseId);
    public List<Lesson> getByGroupId(Long groupId);
    public List<Lesson> getByTeacherId(Long teacherId);
}
