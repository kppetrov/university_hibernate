package ua.com.foxminded.university.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "courses")
@NamedQuery(name = Course.FIND_COURSE_BY_ID_WITH_GROUPS, 
            query = "select distinct c from Course c "
                    + "left join fetch c.groups g " 
                    + "where c.id = :id")
public class Course {    
    public static final String FIND_COURSE_BY_ID_WITH_GROUPS = "Course.findByIdWithGroups";  
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "name")
    private String name;
    
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Lesson> lessons = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "course_group",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private List<Group> groups = new ArrayList<>();

    public Course() {
        
    }
    
    public Course(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Course(int id, String name, List<Lesson> lessons, List<Group> groups) {
        this.id = id;
        this.name = name;
        this.lessons = lessons;
        this.groups = groups;
    }

    public boolean addGroup(Group group) {
        if (groups == null) {
            groups = new ArrayList<>();
        } else {
            if (groups.contains(group)) {
                return false;
            }
        }
        groups.add(group);
        return true;
    }

    public boolean removeGroup(Group group) {
        if (groups == null) {
            return false;
        } else {
            return groups.remove(group);
        }
    }
    
    public boolean addLesson(Lesson lesson) {
        if (lessons == null) {
            lessons = new ArrayList<>();
        } else {
            if (lessons.contains(lesson)) {
                return false;
            }
        }
        lessons.add(lesson);
        return true;
    }

    public boolean removeGroup(Lesson lesson) {
        if (lessons == null) {
            return false;
        } else {
            return lessons.remove(lesson);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Course other = (Course) obj;
        return id == other.id && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "Course [id=" + id + ", name=" + name + "]";
    }
}
