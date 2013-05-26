/* This is the table that contains all the microfeeds. You can just create it
 * in an existing database (eg of your cms site), or in its own.
 *
 *	Have fun! 
 * Alex Hughes <ahughes@ahughes.org>, London 2013
 */
 
CREATE TABLE IF NOT EXISTS `microfeed` (
  `microID` bigint(20) NOT NULL auto_increment,
  `Author` varchar(32) NOT NULL,
  `Title` text NOT NULL,
  `Alias` varchar(200) NOT NULL,
  `Content` text NOT NULL,
  `DatePosted` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Status` tinyint NOT NULL,
  PRIMARY KEY  (`microID`),
  UNIQUE INDEX `Alias_UNIQUE` (`Alias` ASC) 
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


