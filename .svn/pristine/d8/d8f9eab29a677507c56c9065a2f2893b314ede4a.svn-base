ALTER TABLE `qic_db`.`stockpool_ext` CHANGE `stockPoolCode` `stockPoolCode` BIGINT NOT NULL  COMMENT '股票stockPoolCode';

ALTER TABLE `qic_db`.`stockpool_ext` ADD INDEX k_spid(stockPoolCode);

ALTER TABLE qic_db.stock_pool_ext ADD INDEX k_spid(spid);

alter table qic_db.strategy_daily_yield add index kstid (strategyID, updateDate);

alter table qic_db.strategy_baseinfo add index k_stuuid (st_uuid);

alter table qic_db.operation_log_info add index k_uid (uid);

alter table qic_db.`strategy_high_low` add index k_stid (StrategyID);

alter table qic_db.`user_message` add index k_uid (uid, status);