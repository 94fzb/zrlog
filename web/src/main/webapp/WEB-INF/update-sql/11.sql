alter table `comment` add COLUMN `reply_id` int(11) DEFAULT NULL COMMENT '回复评论的ID';
alter table `comment` add COLUMN `user_agent` varchar(1024) DEFAULT NULL COMMENT '浏览器信息';
