delete from course_group;
delete from lessons;
delete from periods;
delete from classrooms;
delete from courses;
delete from students;
delete from teachers;
delete from groups;

insert into groups (id, name) values (1, 'group1');
insert into groups (id, name) values (2, 'group2');
insert into groups (id, name) values (3, 'group3');

insert into students (id, group_id, first_name, last_name, gender, birthdate) values (1, 1, 'first_name1', 'last_name1', 'MAIL', '2001-01-01');
insert into students (id, group_id, first_name, last_name, gender, birthdate) values (2, 1, 'first_name2', 'last_name2', 'FEMAIL', '2002-02-02');
insert into students (id, group_id, first_name, last_name, gender, birthdate) values (3, 2, 'first_name3', 'last_name3', 'FEMAIL', '2003-03-03');
insert into students (id, group_id, first_name, last_name, gender, birthdate) values (4, 2, 'first_name4', 'last_name4', 'FEMAIL', '2004-04-04');

insert into periods (id, name, start_time, end_time) values(1, 'period1', '08:00:00', '09:30:00');
insert into periods (id, name, start_time, end_time) values(2, 'period2', '09:50:00', '11:20:00');
insert into periods (id, name, start_time, end_time) values(3, 'period3', '11:50:00', '13:20:00');

insert into classrooms (id, name) values(1, 'classroom1');
insert into classrooms (id, name) values(2, 'classroom2');
insert into classrooms (id, name) values(3, 'classroom3');

insert into teachers (id, first_name, last_name, gender, birthdate) values (1, 'first_name1', 'last_name1', 'MAIL', '1971-01-01');
insert into teachers (id, first_name, last_name, gender, birthdate) values (2, 'first_name2', 'last_name2', 'FEMAIL', '1972-02-02');
insert into teachers (id, first_name, last_name, gender, birthdate) values (3, 'first_name3', 'last_name3', 'FEMAIL', '1973-03-03');

insert into courses (id, name) values(1, 'course1');
insert into courses (id, name) values(2, 'course2');
insert into courses (id, name) values(3, 'course3');

insert into course_group (course_id, group_id) values(1, 1);
insert into course_group (course_id, group_id) values(2, 2);

insert into lessons (id, course_id, teacher_id, classroom_id, period_id, date) values(1, 1, 1, 1, 1, '2021-01-01');
insert into lessons (id, course_id, teacher_id, classroom_id, period_id, date) values(2, 1, 1, 1, 2, '2021-01-01');
insert into lessons (id, course_id, teacher_id, classroom_id, period_id, date) values(3, 2, 2, 2, 1, '2021-01-01');
insert into lessons (id, course_id, teacher_id, classroom_id, period_id, date) values(4, 2, 2, 2, 2, '2021-01-01');