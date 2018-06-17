insert into channel_provider(provider_code,provider_name)  values ('zfb','中付宝');
alter table pay_request add receive_time bigint  not null default 0;