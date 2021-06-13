CREATE TABLE `dummy-batch`.`products` (
  `product_id` INT NOT NULL AUTO_INCREMENT,
  `product_name` VARCHAR(150) NULL,
  `product_desc` VARCHAR(150) NULL,
  `unit` INT NULL,
  `price` FLOAT NULL,
  PRIMARY KEY (`product_id`));

INSERT INTO `dummy-batch`.`products`
(`product_name`,`product_desc`,`unit`,`price`)VALUES
('HP','HP description',10,100);

INSERT INTO `dummy-batch`.`products`
(`product_name`,`product_desc`,`unit`,`price`)VALUES
('DELL','DELL description',10,200);
