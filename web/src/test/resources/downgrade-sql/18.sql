alter table `type` drop COLUMN `arrange_plugin`;
alter table `log` drop COLUMN `arrange_plugin`;
update website  set value = 17 where name = 'zrlogSqlVersion';