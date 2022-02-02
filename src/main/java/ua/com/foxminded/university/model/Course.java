package ua.com.foxminded.university.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private Set<Lesson> lessons = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "course_group",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new HashSet<>();

    public Course() {
        
    }
    
    public Course(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Course(int id, String name, Set<Lesson> lessons, Set<Group> groups) {
        this.id = id;
        this.name = name;
        this.lessons = lessons;
        this.groups = groups;
    }

    public boolean addGroup(Group group) {
        return groups.add(group);
    }

    public boolean removeGroup(Group group) {
         return groups.remove(group);
    }
    
    public boolean addLesson(Lesson lesson) {
        return lessons.add(lesson);
    }

    public boolean removeGroup(Lesson lesson) {
         return lessons.remove(lesson);
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

    public Set<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
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
