use qic_db;

ALTER TABLE qic_db.function_info
  ADD COLUMN product_id BIGINT NULL  COMMENT '产品id, 关联到sys_product_info表, 也就是属于指定产品的功能' AFTER `pid`;

update qic_db.function_info set product_id = 1;

CREATE TABLE `sys_product_info` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  utime TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=INNODB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='系统产品信息表';


INSERT INTO sys_product_info(id, NAME) VALUES( 1, 'qic');
INSERT INTO sys_product_info(id, NAME) VALUES( 2, 'iquant');



