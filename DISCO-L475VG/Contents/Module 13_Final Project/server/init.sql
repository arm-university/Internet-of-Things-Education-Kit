create database if not exists Motion;
use Motion;

drop table if exists history_records;
create table if not exists history_records(
id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
activity varchar(255),
start_time varchar(255),
end_time varchar(255),
confidence float(5, 4),
count INT(11)
);

# insert test data
insert into history_records (activity, start_time, end_time, confidence, count)
	values ('walk', '2020-03-02 21:00:00', '2020-03-02 21:05:00', 0.9921, 15);
insert into history_records (activity, start_time, end_time, confidence, count)
	values ('walk', '2020-03-02 21:05:15', '2020-03-02 21:05:34', 0.8821, 45);

create user 'activity_manager'@'localhost' identified by 'password';
grant select, update, insert, delete on Motion.history_records to 'activity_manager'@'localhost';