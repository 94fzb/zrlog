alter table `log` add COLUMN `last_update_date` longtext DEFAULT NULL;
update `log` set `last_update_date` = `releaseTime`;