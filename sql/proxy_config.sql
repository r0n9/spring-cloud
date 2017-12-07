/*
Navicat MySQL Data Transfer

Source Server         : cloud_db - s1.fanrong.vip
Source Server Version : 50720
Source Host           : s1.fanrong.vip:3306
Source Database       : cloud_db

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2017-12-07 16:38:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for proxy_config
-- ----------------------------
DROP TABLE IF EXISTS `proxy_config`;
CREATE TABLE `proxy_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `host` varchar(20) CHARACTER SET utf8 NOT NULL,
  `port` int(11) NOT NULL,
  `location` varchar(100) CHARACTER SET utf8 NOT NULL,
  `type` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `statusUpdateTime` datetime DEFAULT NULL,
  `insertTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1726 DEFAULT CHARSET=latin1;
