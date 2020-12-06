use easymeal;

create table Account (
	userAccount varchar(20),
    userPassword varchar(20),
    userName varchar(20),
    userEmail varchar(30),
    primary key (userAccount)
);

create table Food (
	foodID BIGINT,
    userAccount varchar(20),
    foodName varchar(40),
    foodDescription varchar(200),
    foodPic BLOB,
    primary key (foodID, userAccount),
    foreign key (userAccount) references Account(userAccount) on update Cascade on delete cascade
    );

create table Favorite (
	foodID BIGINT,
    userAccount varchar(20),
    primary key (foodID, userAccount),
    foreign key (foodID) references Food(foodID) on update cascade on delete cascade,
    foreign key (userAccount) references Account(userAccount) on update cascade on delete cascade
);

create table Ingredient (
	ingredientName varchar(40),
    primary key (ingredientName)
);

create table FoodIngredient (
	foodID BIGINT,
    ingredientName varchar(40),
    primary key (foodID, ingredientName),
    foreign key (foodID) references Food(foodID) on update cascade on delete cascade,
    foreign key (ingredientName) references Ingredient(ingredientName) on update cascade on delete cascade
);

create table Tool (
	toolName varchar(40),
    primary key (toolName)
);

create table FoodTool (
	toolName varchar(40),
    foodID BIGINT,
    primary key (toolName, foodID),
    foreign key (toolName) references Tool(toolName) on update cascade on delete cascade,
    foreign key (foodID) references Food(foodID) on update cascade on delete cascade
);

create table Recipe (
	foodID BIGINT,
    step BIGINT,
    instruction varchar(200),
    primary key (foodID, step),
    foreign key (foodID) references Food(foodID) on update cascade on delete cascade
);

drop table Account;
show tables;


delimiter $$
drop procedure if exists loginAccount;
create procedure loginAccount (in uaccount varchar(20), in password varchar(20), 
								out name varchar (20), out email varchar(30))
la: begin
	
    declare searchPassword varchar(20);
    
	set searchPassword = (
		select a.userPassword
        from Account a
        where a.userAccount = uaccount);
	
    set name = "NULL";
	set email = "NULL";
    
    if searchPassword is NULL then
		leave la;
	end if;
    
    if searchPassword != password then
		leave la;
	end if;
    
	set name = (
		select a.userName
        from Account a
        where a.userAccount = uaccount);
		
	set email = (
		select a.userEmail
        from Account a
        where a.userAccount = uaccount);
end $$
delimiter ;

delimiter $$
drop procedure if exists createAccount;
create procedure createAccount (in uaccount varchar(20), in password varchar(20), in username varchar(20), in email varchar(30), out status varchar(10))
ca: begin
	
    declare insertUsername varchar(20);
        
    set insertUsername = (
		select a.userAccount
        from Account a
        where a.userAccount = uaccount
	);
	
    if insertUsername is NOT NULL then
		set status = "Exist";
        leave ca;
	end if;
        
    if insertUsername is NULL then
		insert into Account values (uaccount, password, username, email);
        set status = "Success";
	end if;
end $$
delimiter ;
select * from Account;
delete from Account where userAccount = "TestAccount";

delimiter $$
drop procedure if exists updateAccount;
create procedure updateAccount (in uaccount varchar(20), in password varchar(20), in username varchar(20), in email varchar(30), out status varchar(10))
ua: begin

	declare getUser varchar(20);
        
    set getUser = (
		select a.userAccount
        from Account a
        where a.userAccount = uaccount
	);
    
    if getUser is NULL then
		set status = "Fail";
        leave ua;
	end if;
    
    update Account a
    set a.userPassword = password,
        a.userEmail = email,
        a.userName = username
	where a.userAccount = uaccount;
    
    set status = "Success";
    	
end $$
delimiter ;

delimiter $$
drop procedure if exists setFavorite;
create procedure setFavorite (in uaccount varchar(20), in ufood BIGINT, out status varchar(10))
sf: begin

	declare getUser varchar(20);
    declare getFood BIGINT;
    declare find varchar(20);
        
    set getUser = (
		select a.userAccount
        from Account a
        where a.userAccount = uaccount
	);
    
    if getUser is NULL then
		set status = "Fail";
        leave sf;
	end if;
    
    set getFood = (
		select f.foodID
        from Food f
        where f.foodID = ufood
	);
	
    if getFood is NULL then
		set status = "Fail";
        leave sf;
	end if;
    
    set find = (
		select userAccount
        from Favorite
        where userAccount = uaccount
			and foodID = ufood
	);
    
    if find is NULL then
		insert into Favorite values (ufood, uaccount);
        set status = "Success";
        leave sf;
	end if;
    
    set status = "Fail";
    	
end $$
delimiter ;

delimiter $$
drop procedure if exists getFoodID;
create procedure getFoodID (out id BIGINT)
begin
	set id =(
		select f1.foodID
        from Food f1
        where f1.foodID > all(
			select f2.foodID
            from Food f2
            where f1.foodID <> f2.foodID)
		);
	
end $$
delimiter ;

delimiter $$
drop procedure if exists createFood;
create procedure createFood (in id BIGINT, in uaccount VARCHAR(20), in fname VARCHAR(40), in fDescription VARCHAR(200), in fPic BLOB, out status VARCHAR(10))
cf: begin
	declare getID BIGINT;
    
    set getID = (
		select f.foodID
        from Food f
        where f.userAccount = uaccount
			and f.foodName = fname);
	
    if getID is NOT NULL then
		set status = "Fail";
        leave cf;
	end if;
    
    insert into Food values (id, uaccount, fname, fdescription, fpic);
    set status = "Success";
    
end $$
delimiter ;

delimiter $$
drop procedure if exists updateFood;
create procedure updateFood (in fid BIGINT, in uaccount varchar(20), in fname varchar(40), in fdescription varchar(200), in fpic BLOB, out status varchar(10))
uf: begin
	declare getID BIGINT;
    set getID = (
		select foodID
        from Food
        where foodID = fid
			and userAccount = uaccount);
	
    if getID is NULL then
		set status = "Fail";
        leave uf;
	end if;
    
    set getID = (
		select foodID
        from Food
        where foodName = fname
			and userAccount = uaccount);
	if getID is NOT NULL then
		set status = "Fail";
        leave uf;
	end if;
    
    update Food
    set foodName = fname,
		foodDescription = fdescription,
        foodPic = fpic
	where foodID = fid;
    
    set status = "Success";
end $$
delimiter ;

delimiter $$
drop procedure if exists createRecipe;
create procedure createRecipe (in fid BIGINT, in ustep BIGINT, in uinstruction VARCHAR(200), in uaccount varchar(20), out status VARCHAR(10))
cr: begin
	declare findID BIGINT;
    declare findAccount varchar(20);
    declare find BIGINT;
    
    set findAccount =(
		select userAccount
        from Account
        where userAccount = uaccount);
	
    if findAccount is NULL then
		set status = "Fail1";
        leave cr;
	end if;
    
    set findID = (
		select foodID
        from Food
        where foodID = fid);
        
	if findID is NULL then
		set status = "Fail2";
        leave cr;
	end if;
    
    set find = (
		select step
        from Recipe
        where foodID = fid);

	if find is NOT NULL then
		set status = "Fail3";
        leave cr;
	end if;
    
    insert into Recipe values (fid, ustep, uinstruction);
    set status = "Success";
    
end $$
delimiter ;

delimiter $$
drop procedure if exists deleteRecipe;
create procedure deleteRecipe(in fid BIGINT, in uaccount varchar(20))
dr: begin
	declare findID BIGINT;
    declare findAccount varchar(20);
    
    set findID = (
		select foodID
        from Recipe
        where foodID = fid
        group by foodID);
	
    if findID is NULL then
		leave dr;
	end if;
    
    set findAccount =(
		select userAccount
        from Food
        where foodID = fid
			and userAccount = uaccount);
	
    if findAccount is NULL then
		leave dr;
	end if;
    
    delete from Recipe where foodID = fid;
		
end $$
delimiter ;

delimiter $$
drop procedure if exists updateRecipe;
create procedure updateRecipe(in fid BIGINT, in ustep BIGINT, in instruction varchar(200), in uaccount varchar(20))
ur: begin
	declare findID BIGINT;
    declare findAccount varchar(20);
    declare findStep BIGINT;
    
    set findID = (
		select foodID
        from Recipe
        where foodID = fid
        group by foodID);
        
	if findID is NULL then
		leave ur;
	end if;
    
    set findAccount = (
		select userAccount
        from Food
        where foodID = fid
			and userAccount = uaccount);
	
    if findAccount is NULL then
		leave ur;
	end if;
	
    set findStep = (
		select r.step
        from Recipe r
        where r.step = ustep
			and r.foodID = fid);
	
    if findStep is NULL then
		insert into Recipe values (fid, ustep, instruction);
        leave ur;
	end if;
    
	update Recipe r set r.instruction = instruction where r.foodID = fid and r.step = ustep;
    
end $$
delimiter ;

delimiter $$
drop procedure if exists createTool;
create procedure createTool (in tname varchar(40))
ct: begin
	declare tool varchar(20);
    
    set tool = (
		select *
        from Tool
        where toolName = tname);
	
    if tool is NOT NULL then
		leave ct;
	end if;
    
    insert into Tool values (tname);
end $$
delimiter ;

delimiter $$
drop procedure if exists deleteTool;
create procedure deleteTool ()
dt: begin
	delete 
    from Tool t
    where t.toolName <> all(select ft.toolName from FoodTool ft group by ft.toolName);
end $$
delimiter ;

delimiter $$
drop procedure if exists createToolFood;
create procedure createToolFood(in toolName varchar(40), in foodID BIGINT)
ctf: begin
	declare find BIGINT;
    declare findFood BIGINT;
    declare findTool varchar(20);
    
    set find = (
		select ft.foodID
        from FoodTool ft
        where ft.toolName = toolName
			and ft.foodID = foodID);
            
	if find is NOT NULL then
		leave ctf;
	end if;
    
    set findFood = (
		select f.foodID
        from Food f
        where f.foodID = foodID);
	
    if findFood is NULL then
		leave ctf;
	end if;
    
    set findTool =(
		select t.toolName
        from Tool t
        where t.toolName = toolName);
	
    if findTool is NULL then
		leave ctf;
	end if;
    
    insert into FoodTool values (toolName, foodID);
end $$
delimiter ;

delimiter $$
drop procedure if exists createIngredient;
create procedure createIngredient (in newIngredient varchar(40), out status varchar(10))
ci: begin
	declare find varchar(20);
    
    set find = (
		select *
        from Ingredient ig
        where ig.ingredientName = newIngredient);
	if find is NOT NULL then
		set status = "Fail";
        leave ci;
	end if;
    
    insert into Ingredient values (newIngredient);
    set status = "Success";
end $$
delimiter ;

delimiter $$
drop procedure if exists createIngredientFood;
create procedure createIngredientFood (in fid BIGINT, in newIngredient varchar(40))
cif: begin
	declare find BIGINT;
    
    set find = (
		select foodID
        from FoodIngredient
        where foodID = fid
			and ingredientName = newIngredient);
	
    if find is NOT NULL then
		leave cif;
	end if;
    
    insert into FoodIngredient values (fid, newIngredient);
end $$
delimiter ;

insert into Ingredient values ("Test Ingredient");
select * from Favorite;
select * from Food;
select * from Account;
select * from Recipe;
select * from Tool;
select * from FoodTool;
select * from Favorite;
select * from Ingredient;
select * from FoodIngredient;
delete from FoodTool where foodID = 1;
delete from Tool where toolName = "Test1";
delete from Tool;
insert into Food values (4, "Admin", "new4", "new4", null);

select * from Food where foodName like '%new%';
delete from Food where foodName like '%new%';

insert into Recipe values (1, 3, "Test instruction3");

insert into Favorite values (3,"tsewang");
delete from Account where userAccount = "test";
