use qic_db;

ALTER TABLE `qic_db`.`strategy_baseinfo`
  ADD COLUMN `param` TEXT NULL  COMMENT '策略参数:从配制文件中提取出来的.xml格式' AFTER `run_server_id`;

ALTER TABLE `qic_db`.`strategy_baseinfo`
  ADD COLUMN `fundsProportion` DOUBLE NULL  COMMENT '资金使用比例,从配制文件中提取' AFTER `param`;