use `ou-journal-db`;

-- INSERT ROLE NAME INTO ROLE TABLE
insert into `role`(role_name)
select 'ROLE_EDITOR'
where not exists(
	select *
    from role
    where role_name = 'ROLE_EDITOR'
);
insert into `role`(role_name)
select 'ROLE_SECRETARY'
where not exists(
	select *
    from role
    where role_name = 'ROLE_SECRETARY'
);
insert into `role`(role_name)
select 'ROLE_AUTHOR'
where not exists(
	select *
    from role
    where role_name = 'ROLE_AUTHOR'
);
insert into `role`(role_name)
select 'ROLE_REVIEWER'
where not exists(
	select *
    from role
    where role_name = 'ROLE_REVIEWER'
);
insert into `role`(role_name)
select 'ROLE_ADMIN'
where not exists(
	select *
    from role
    where role_name = 'ROLE_ADMIN'
);

-- INSERT TYPE OF DATE INTO DATE_TYPE TABLE
insert into `date_type`(type_name)
select 'SUBMITTED_DATE'
where not exists(
	select *
    from date_type
    where type_name = 'SUBMITTED_DATE'
);
insert into `date_type`(type_name)
select 'SECRETARY_VIEWED_DATE'
where not exists(
	select *
    from date_type
    where type_name = 'SECRETARY_VIEWED_DATE'
);
insert into `date_type`(type_name)
select 'IN_REVIEW_DATE'
where not exists(
	select *
    from date_type
    where type_name = 'IN_REVIEW_DATE'
);
insert into `date_type`(type_name)
select 'DECIDING_DATE'
where not exists(
	select *
    from date_type
    where type_name = 'DECIDING_DATE'
);
insert into `date_type`(type_name)
select 'DECIDED_DATE'
where not exists(
	select *
    from date_type
    where type_name = 'DECIDED_DATE'
);
insert into `date_type`(type_name)
select 'WITHDRAW_DATE'
where not exists(
	select *
    from date_type
    where type_name = 'WITHDRAW_DATE'
);
insert into `date_type`(type_name)
select 'PUBLIC_DATE'
where not exists(
	select *
    from date_type
    where type_name = 'PUBLIC_DATE'
);

-- INSERT TYPE OF AUTHOR INTO AUTHOR_TYPE TABLE
insert into `author_type`(type)
select 'FIRST_AUTHOR'
where not exists(
	select *
    from author_type
    where type = 'FIRST_AUTHOR'
);
insert into `author_type`(type)
select 'CORRESPONDING_AUTHOR'
where not exists(
	select *
    from author_type
    where type = 'CORRESPONDING_AUTHOR'
);
insert into `author_type`(type)
select 'AUTHOR'
where not exists(
	select *
    from author_type
    where type = 'AUTHOR'
);

-- INSERT DEFAULT ADMIN ACCOUNT INTO ACCOUNT AND RELATED TABLES
drop procedure if exists insert_default_admin;
delimiter //
create procedure insert_default_admin()
begin
	if not exists(select 1 from user where email = 'admin@gmail.com') then
		insert into `user`(created_at, dob, email, first_name, last_name)
			values (now(), '2002-05-18', 'admin@gmail.com', 'Mở TP.HCM', 'trường Đại Học');
            
		insert into `user_role`(role_id, user_id)
			select (select r.id from role r where r.role_name = 'ROLE_ADMIN') as role_id, u.id as user_id
            from user u
            where u.email = 'admin@gmail.com';
            
		insert into `account`(id, created_at, email, password, user_name)
			select (select u.id from user u where u.email = 'admin@gmail.com') as id,
				now() as created_at, 'admin@gmail.com' as email,
                '$2a$10$WBFsrPYuJz7aAEFpSJFo2OfFKaWiCH2oJW4Y34zr3W9dlr0laNad6' as password,
                'admin' as user_name;
    end if;

    if not exists(select 1 from user where email = 'secretary@gmail.com') then
        insert into `user`(created_at, dob, email, first_name, last_name)
			values (now(), '2005-05-16', 'secretary@gmail.com', 'Mở TP.HCM', 'Thư ký trường Đại Học');
            
		insert into `user_role`(role_id, user_id)
			select (select r.id from role r where r.role_name = 'ROLE_SECRETARY') as role_id, u.id as user_id
            from user u
            where u.email = 'secretary@gmail.com';
            
		insert into `account`(id, created_at, email, password, user_name)
			select (select u.id from user u where u.email = 'secretary@gmail.com') as id,
				now() as created_at, 'secretary@gmail.com' as email,
                '$2a$10$WBFsrPYuJz7aAEFpSJFo2OfFKaWiCH2oJW4Y34zr3W9dlr0laNad6' as password,
                'secretary' as user_name;
    end if;

    if not exists(select 1 from user where email = 'editor@gmail.com') then
        insert into `user`(created_at, dob, email, first_name, last_name)
			values (now(), '2005-05-16', 'editor@gmail.com', 'Mở TP.HCM', 'Biên tập viên trường Đại Học');
            
		insert into `user_role`(role_id, user_id)
			select (select r.id from role r where r.role_name = 'ROLE_EDITOR') as role_id, u.id as user_id
            from user u
            where u.email = 'editor@gmail.com';
            
		insert into `account`(id, created_at, email, password, user_name)
			select (select u.id from user u where u.email = 'editor@gmail.com') as id,
				now() as created_at, 'editor@gmail.com' as email,
                '$2a$10$WBFsrPYuJz7aAEFpSJFo2OfFKaWiCH2oJW4Y34zr3W9dlr0laNad6' as password,
                'editor' as user_name;
    end if;
end //
delimiter ;

CALL insert_default_admin();