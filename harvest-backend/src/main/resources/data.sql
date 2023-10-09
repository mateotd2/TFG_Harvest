insert into Rol(name) values ('ROLE_ADMIN')
insert into Rol(name) values ('ROLE_CAPATAZ')
insert into Rol(name) values ('ROLE_TRACTORISTA')
--
----

insert into Empleado ( name,lastname, dni, nss, phone,birthdate, email, username, password) values ('miguel','garcia','12345678A','123456789012','666666666','1990-06-15', 'miguelon@gmail.com','miguelAdmin','$2a$10$9FEGLP52a3v6kPZzSjPwiekP/6H15IAAHAEaguMoeIzREdXh4iEiW')

insert into USER_ROLES (role_id,user_id) values (1,1)
insert into USER_ROLES (role_id,user_id) values (2,1)
insert into USER_ROLES (role_id,user_id) values (3,1)