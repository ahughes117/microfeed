CREATE TABLE IF NOT EXISTS `miniblog` (
  `miniID` bigint(20) NOT NULL auto_increment,
  `Author` varchar(32) NOT NULL,
  `Title` varchar(64) NOT NULL,
  `Subtitle` varchar(100),
  `Content` text NOT NULL,
  `DatePosted` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY  (`miniID`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

insert into miniblog (Author, Title, Content)
values ('Alex Hughes', 'Test Post', 'This is just a test post to see whether that cool simple minifeed is working');

insert into miniblog (Author, Title, Content)
values ('Alex Hughes', 'Success!', 'Well, the whole thing seems to be working, I will keep posting my small chit-chat news here');
