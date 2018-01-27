alter table `comment` add COLUMN `have_read` bit(1) DEFAULT false COMMENT '评论是否已读';
update `comment` set `have_read` = true where 1=1;