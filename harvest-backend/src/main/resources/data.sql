insert into Rol(name) values ('ROLE_ADMIN')
insert into Rol(name) values ('ROLE_CAPATAZ')
insert into Rol(name) values ('ROLE_TRACTORISTA')
--
----

insert into Empleado ( name,lastname, dni, nss, phone,birthdate, direccion, email, username, password) values ('miguel','garcia','12345678A','123456789012','666666666','1990-06-15','Direccion ', 'miguelon@gmail.com','miguelAdmin','$2a$10$9FEGLP52a3v6kPZzSjPwiekP/6H15IAAHAEaguMoeIzREdXh4iEiW')

insert into USER_ROLES (role_id,user_id) values (1,1)
insert into USER_ROLES (role_id,user_id) values (2,1)
insert into USER_ROLES (role_id,user_id) values (3,1)

insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador1','garcia1','12145678A','123456789012','111111111','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador2','garcia2','98265432A','210987654321','222222222','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador3','garcia3','19383847A','211029384756','333333333','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador4','garcia4','12845678A','123456784012','111111111','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador5','garcia5','98565432A','210987652321','222222222','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador6','garcia6','19783847A','211029385756','333333333','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador7','garcia7','12845671A','123456786012','111111111','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador8','garcia8','9865432A','210987657321','222222222','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador9','garcia9','10283847A','211029388756','333333333','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador10','garcia10','19343679A','123456989012','111111111','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador11','garcia11','92765432A','210987054321','222222222','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador12','garcia12','11283847A','211029184756','333333333','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador13','garcia10','19341679A','223456989012','111111111','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador14','garcia11','92762462A','310987054321','222222222','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador15','garcia12','17283887A','411029184756','333333333','1990-06-15','Direccion',true)


insert into Disponibilidad (daywork, checkin, checkout,trabajador_id,attendance) values (CURRENT_DATE,'08:30:00','14:30:00',1,false)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id,attendance) values (CURRENT_DATE+1,'08:30:00','14:30:00',1,false)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id,attendance) values ('2023-10-25','08:30:00','14:30:00',1,true)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id,attendance) values (CURRENT_DATE,'08:30:00','14:30:00',2,true)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id,attendance) values (CURRENT_DATE+1,'08:30:00','14:30:00',2,true)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id,attendance) values ('2023-10-25','08:30:00','14:30:00',2,false)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id,attendance) values (CURRENT_DATE,'08:30:00','14:30:00',3,false)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id,attendance) values (CURRENT_DATE+1,'08:30:00','14:30:00',3,false)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id,attendance) values ('2023-10-25','08:30:00','14:30:00',3,false)