-- !Ups
create table scala.users
(
    id         serial       not null constraint users_pk primary key,
    email       varchar(64)  not null,
    first_name   varchar(64)  not null,
    last_name   varchar(64)  not null,
    password   varchar(128) not null,
    role       varchar(16)  not null,
    birth_date       timestamp,
    address       varchar(16),
    phone_number       varchar(16)
);

create unique index users_id_index on scala.users (id);
create unique index users_email_index on scala.users (email);

insert into scala.users(id, email, first_name, last_name, password, role, birth_date, address, phone_number)
values (1, 'admin@gmail.com', 'Admin', 'Admin', '$2a$10$AEZeNnPEx4qGu47TpEQ.ueaTOYAboWoz/f5c3KDTVvQ.WlpSikQiS',
'Admin', current_timestamp, 'Home', '123456');

create table scala.products
(
	id              serial          not null constraint products_pk primary key,
	product_name           varchar(128)    not null,
	price     FLOAT not null,
	exp_date            timestamp       not null
);

create unique index products_id_index on scala.products (id);

create table scala.orders
(
	id              serial          not null constraint orders_pk primary key,
	user_id           INT    not null,
	order_date            timestamp       not null,
	total_price     FLOAT not null,
	constraint orders_user_id_fk foreign key (user_id) references scala.users (id)
);

create unique index orders_id_index on scala.orders (id);

create table scala.order_details
(
	id              serial          not null constraint order_details_pk primary key,
	order_id           INT    not null,
	product_id            INT       not null,
	price     FLOAT not null,
	quantity     INT not null,
	constraint order_details_order_id_fk foreign key (order_id) references scala.orders (id),
	constraint order_details_product_id_fk foreign key (product_id) references scala.products (id)
);

create unique index order_details_id_index on scala.order_details (id);

-- !Downs
DROP TABLE scala.order_details;
DROP TABLE scala.orders;
DROP TABLE scala.products;
DROP TABLE scala.users;
