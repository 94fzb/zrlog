alter table `log` add COLUMN `version` int(11) DEFAULT '0';
update `log` set `version` = 0;