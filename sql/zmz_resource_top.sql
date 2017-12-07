/*
Navicat MySQL Data Transfer

Source Server         : cloud_db - s1.fanrong.vip
Source Server Version : 50720
Source Host           : s1.fanrong.vip:3306
Source Database       : cloud_db

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2017-12-07 16:39:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for zmz_resource_top
-- ----------------------------
DROP TABLE IF EXISTS `zmz_resource_top`;
CREATE TABLE `zmz_resource_top` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `get_time` datetime NOT NULL,
  `count` int(11) DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `src` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `img_data_src` varchar(500) CHARACTER SET utf8 DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `name_en` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `processed` tinyint(4) NOT NULL DEFAULT '0',
  `process_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`get_time`),
  KEY `get_time` (`get_time`)
) ENGINE=InnoDB AUTO_INCREMENT=965 DEFAULT CHARSET=latin1;
