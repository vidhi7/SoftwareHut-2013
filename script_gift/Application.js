/*
 * @fileoverview Description of this file.
 * @author: Christopher Burke <christopher.burke@simulity.com>
 */
var Sys = require('sys');
var Exec = require('child_process').exec;
var Util = require('util');
var Logger = require('./Logger.js').Logger;

/**
 * A Node.JS Database loader, for Fortune quotes. Will populate quotes
 * into the MongoDB specified in the Logger. Just thrown in as a quick
 * hack. Perhaps see if MySQL can be used to populate your quotes. ;-)
 */
 var Application = function() {
	 this.logger_ = new Logger('fortune_populator');
	 var hackyThis = this; // do not make this a habbit!
	 var runCount = process.argv[2] || 10;
	 
	 for(var i = 0; i < runCount; i++) { 
		 var child = Exec("fortune", function (error, stdout, stderr) {
			 hackyThis.logger_.QUOTE(stdout);
			 if (error !== null) {
				 this.logger_.ERROR(error);
			 }
		 });
	 }
 }
 
 console.log('INFO: Starting the Fortune Database Loader.');
 new Application();
 console.log('INFO: Databaseloader has finished all operations.');