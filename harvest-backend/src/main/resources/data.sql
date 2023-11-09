insert into Rol(name) values ('ROLE_ADMIN')
insert into Rol(name) values ('ROLE_CAPATAZ')
insert into Rol(name) values ('ROLE_TRACTORISTA')
--
----

insert into Empleado ( name,lastname, dni, nss, phone,birthdate, direccion, email, username, password) values ('miguel','garcia','12345678A','123456789012','666666666','1990-06-15','Direccion ', 'miguelon@gmail.com','miguelAdmin','$2a$10$9FEGLP52a3v6kPZzSjPwiekP/6H15IAAHAEaguMoeIzREdXh4iEiW')

insert into USER_ROLES (role_id,user_id) values (1,1)
insert into USER_ROLES (role_id,user_id) values (2,1)
insert into USER_ROLES (role_id,user_id) values (3,1)

insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador1','garcia1','12345678A','123456789012','111111111','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador2','garcia2','98765432A','210987654321','222222222','1990-06-15','Direccion',true)
insert into Trabajador( name,lastname, dni, nss, phone,birthdate,address,available) values ('trabajador3','garcia3','19283847A','211029384756','333333333','1990-06-15','Direccion',true)

insert into Disponibilidad (daywork, checkin, checkout,trabajador_id) values ('2023-10-25','08:30:00','14:30:00',1)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id) values ('2023-10-25','08:30:00','14:30:00',1)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id) values ('2023-10-25','08:30:00','14:30:00',2)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id) values ('2023-10-25','08:30:00','14:30:00',2)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id) values ('2023-10-25','08:30:00','14:30:00',3)
insert into Disponibilidad (daywork, checkin, checkout,trabajador_id) values ('2023-10-25','08:30:00','14:30:00',3)