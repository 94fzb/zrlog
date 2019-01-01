# 这个文件仅用于程序初始化数据库表结构使用, 程序初始化使用提供的安装向导进行初始化, 及不要手动导入这个文件到数据库
# Date: 2014-07-22 13:23:19
# Generator: MySQL-Front 5.3  (Build 4.43)

/*!40101 SET NAMES utf8 */;

DROP TABLE IF EXISTS `comment`, `link`, `log`, `lognav`, `plugin`, `tag`, `type`, `user`, `website`;

#
# Structure for table "link"
#

DROP TABLE IF EXISTS `link`;
CREATE TABLE `link` (
  `linkId` int(11) NOT NULL AUTO_INCREMENT,
  `alt` varchar(255) DEFAULT NULL,
  `linkName` varchar(255) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`linkId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "lognav"
#

DROP TABLE IF EXISTS `lognav`;
CREATE TABLE `lognav` (
  `navId` int(11) NOT NULL AUTO_INCREMENT,
  `navName` varchar(32) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`navId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "plugin"
#

DROP TABLE IF EXISTS `plugin`;
CREATE TABLE `plugin` (
  `pluginId` int(11) NOT NULL AUTO_INCREMENT,
  `content` text,
  `isSystem` bit(1) DEFAULT NULL,
  `pTitle` varchar(255) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `pluginName` varchar(255) DEFAULT NULL,
  `level` int(11) DEFAULT NULL,
  PRIMARY KEY (`pluginId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "tag"
#

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `tagId` int(11) NOT NULL AUTO_INCREMENT,
  `count` int(11) NOT NULL DEFAULT '0',
  `text` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`tagId`),
  UNIQUE KEY `text` (`text`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "type"
#

CREATE TABLE `type` (
  `typeId` int(11) NOT NULL AUTO_INCREMENT,
  `alias` varchar(32) DEFAULT NULL,
  `remark` varchar(2000) DEFAULT NULL,
  `typeName` varchar(128) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  PRIMARY KEY (`typeId`),
  KEY `pid` (`pid`),
  CONSTRAINT `type_ibfk_1` FOREIGN KEY (`pid`) REFERENCES `type` (`typeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#
# Structure for table "user"
#

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(64) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  `userName` varchar(16) DEFAULT NULL,
  `header` varchar(255) DEFAULT NULL,
  `secretKey` varchar(1024) DEFAULT NULL COMMENT '密钥',
  PRIMARY KEY (`userId`),
  UNIQUE KEY `userName` (`userName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "log"
#

DROP TABLE IF EXISTS `log`;
CREATE TABLE `log` (
  `logId` int(11) NOT NULL AUTO_INCREMENT,
  `alias` varchar(255) DEFAULT NULL,
  `canComment` bit(1) DEFAULT b'1',
  `click` int(11) DEFAULT '0',
  `content` longtext,
  `plain_content` longtext,
  `markdown` longtext,
  `digest` text,
  `keywords` varchar(255) DEFAULT NULL,
  `thumbnail` varchar(255) DEFAULT NULL,
  `recommended` bit(1) DEFAULT b'0',
  `releaseTime` datetime DEFAULT NULL,
  `last_update_date` datetime DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `typeId` int(11) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  `hot` bit(1) DEFAULT NULL,
  `rubbish` bit(1) DEFAULT NULL,
  `privacy` bit(1) DEFAULT NULL,
  `editor_type` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`logId`),
  KEY `typeId` (`typeId`),
  KEY `userId` (`userId`),
  CONSTRAINT `log_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `user` (`userId`),
  CONSTRAINT `log_ibfk_1` FOREIGN KEY (`typeId`) REFERENCES `type` (`typeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_unicode_ci;


#
# Structure for table "comment"
#

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `commentId` int(11) NOT NULL AUTO_INCREMENT,
  `commTime` datetime DEFAULT NULL,
  `hide` bit(1) DEFAULT NULL,
  `have_read` bit(1) DEFAULT false COMMENT '评论是否已读',
  `td` datetime DEFAULT NULL,
  `userComment` varchar(2048) DEFAULT NULL,
  `userHome` varchar(64) DEFAULT NULL,
  `userIp` varchar(64) DEFAULT NULL,
  `userMail` varchar(64) DEFAULT NULL,
  `userName` varchar(64) DEFAULT NULL,
  `logId` int(11) DEFAULT NULL,
  `postId` varchar(128) DEFAULT NULL COMMENT '多说评论使用',
  `header` varchar(1024) DEFAULT NULL COMMENT '评论者头像',
  `user_agent` varchar(1024) DEFAULT NULL COMMENT '浏览器信息',
  `reply_id` int(11) DEFAULT NULL COMMENT '回复评论的ID',
  PRIMARY KEY (`commentId`),
  UNIQUE KEY `postId` (`postId`),
  KEY `logId` (`logId`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`logId`) REFERENCES `log` (`logId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


#
# Structure for table "website"
#

DROP TABLE IF EXISTS `website`;
CREATE TABLE `website` (
  `siteId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `value` longtext DEFAULT NULL,
  `remark` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`siteId`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;