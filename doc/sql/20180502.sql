CREATE TABLE `user_channel_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) NOT NULL,
  `account_id` bigint(20) not null,
  `channel_provider` varchar(10) not null,
  `channel_code` varchar(10) not null default '',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `lock_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `version` int(5) NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `paid_amount` decimal(32,2) NOT NULL DEFAULT '0.00',
  `total_amount` decimal(32,2) not null DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_account_channel_id` (`account_id`,`channel_provider`),
  UNIQUE KEY `unq_user_channel_id` (`group_id`,`channel_provider`)
) ENGINE=InnoDB AUTO_INCREMENT=8651 DEFAULT CHARSET=utf8;

alter table channel_provider add agent_pay int default 0;
update channel_provider set agent_pay = 1 where provider_code = 'ww';

alter table account_opr_log add service varchar(10) not null default '';
alter table account_opr_log add provider_code varchar(10) not null default '';

alter table settle_task add paid_amount decimal(20,2) not null default 0;
alter table settle_task add lock_amount decimal(20,2) not null default 0;
alter table settle_task add id_no varchar(32) not null default '';
alter table settle_task add bank_code varchar(32) not null default '';
alter table settle_task add tel varchar(32) not null default '';

CREATE TABLE `agent_pay_log` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `trade_no` varchar(32) not null default '',
  `with_draw_task_id` bigint(32) not null,
  `group_id` bigint(32) NOT NULL,
  `user_channel_account_id` bigint(32) NOT NULL,
  `provider_code` varchar(10) NOT NULL DEFAULT '',
  `amount` decimal(32,2) NOT NULL DEFAULT '0.00',
  `status` int(2) NOT NULL DEFAULT '0',
  `type` int not null default '0',
  `remark` varchar(100) NOT NULL default '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  index `idx_group` (`group_id`),
  index `idx_user_channel_account_id` (`user_channel_account_id`),
  index `idx_provider_code` (`provider_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1530 DEFAULT CHARSET=utf8;