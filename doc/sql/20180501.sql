alter table channel add min_price decimal(32,2) not null default 0;
alter table channel add max_price decimal(32,2) not null default 0;
alter table channel add start_hour int(2) not null default 0;
alter table channel add stop_hour int(2) not null default 0;