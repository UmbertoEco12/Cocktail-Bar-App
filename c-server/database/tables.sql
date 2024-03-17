/* saves the users info */
create table if not exists users_table(
  user_id int primary key auto_increment,
  user_name varchar(255) unique not null,
  user_password varchar(255) not null
);
/* saves info about the products */
create table if not exists drinks_table(
  	drink_id int primary key auto_increment,
  	drink_name varchar(255) not null,
  	drink_description mediumtext,
    drink_icon_path varchar(255) not null,
    drink_price float not null,
  	is_smoothie bool not null default false
  );
  
/* saves the shopping cart of the user */
create table if not exists user_cart(
  user_id int not null,
  drink_id int not null,
  foreign key (user_id) references users_table(user_id),
  foreign key (drink_id) references drinks_table(drink_id)
);
/* saves the orders of the user */
create table if not exists user_order(
  user_id int not null,
  drink_id int not null,
  foreign key (user_id) references users_table(user_id),
  foreign key (drink_id) references drinks_table(drink_id)
);

/* table that stores card information */
create table if not exists payment_methods(
  card_id int primary key auto_increment,
  card_number varchar(16) not null,
  card_date varchar(5) not null,
  card_cvc varchar(3) not null
);

/* user payment options */
create table if not exists user_payment_method(
	user_id int not null,
  card_id int not null,
  foreign key (user_id) references users_table(user_id),
  foreign key (card_id) references payment_methods(card_id)
);

/* tags */

create table if not exists tags(
	tag_id int primary key auto_increment,
  tag_name varchar(255) not null unique  
);

/* drink tags */
create table if not exists drink_tags(
	tag_id int not null,
  drink_id int not null,
  foreign key (tag_id) references tags(tag_id),
  foreign key (drink_id) references drinks_table(drink_id)
);

/* ingredients */
create table if not exists ingredients(
  ingredient_id int primary key auto_increment,
  ingredient_name  varchar(255) not null unique  
);
/* drink ingredients */
create table if not exists drink_ingredients(
	ingredient_id int not null,
  drink_id int not null,
  foreign key (ingredient_id) references ingredients(ingredient_id),
  foreign key (drink_id) references drinks_table(drink_id)
);

/* biometric auth */
create table if not exists user_biometric(
	user_id int not null,
  biometric_hash varchar(255) not null primary key,
  foreign key(user_id) references users_table(user_id)
);

/* function that adds a payment method to the user */ 
drop function if exists insert_payment_method;

create function if not exists insert_payment_method(us_id int , c_n varchar(255) , c_d varchar(255), c_cvc varchar(255)) returns int
deterministic
begin
	declare id int;
  declare u_id int;
  declare x int;
  select card_id into id from payment_methods where card_number = c_n and card_date = c_d and card_cvc = c_cvc;
  if(id is null) then
  	insert into payment_methods(card_number, card_date, card_cvc) values(c_n, c_d, c_cvc);
  	select card_id into id from payment_methods where card_number = c_n and card_date = c_d and card_cvc = c_cvc;
  end if;  
  select card_id into x from user_payment_method where user_id = us_id and card_id = id;
  if(x is null) then
  	insert into user_payment_method values(us_id, id);
  else
  	return null;
  end if;
  /* returns card id */
  return id;
 end;


