alter table `log`
    add COLUMN `arrange_plugin` varchar(64) DEFAULT null COMMENT '文章统筹重排插件名称';
alter table `type`
    add COLUMN `arrange_plugin` varchar(64) DEFAULT null COMMENT '文章分类统筹重排插件名称';
