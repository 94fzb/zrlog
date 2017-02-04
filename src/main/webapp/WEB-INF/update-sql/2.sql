alter table `user` add secretKey varchar(1024) DEFAULT NULL COMMENT '密钥';
update `user` set secretKey=(select UUID())