/*
 * @fileoverview Description of this file.
 * @author: Christopher Burke <christopher.burke@simulity.com>
 */
var Winston = require('winston');
var MongoDB = require('winston-mongodb').MongoDB;

/**
 * @constructor
 * @param name of log file
 */
var Logger = function(name) {
	
	var path = './logs/';
	var custom = {
		levels: {
			quotes: 0,
	    	info: 1,
	    	warn: 2,
	    	error: 3,
	    	fatal: 4
	    },
	    colors: {
	    	info: 'blue',
	    	warn: 'green',
	    	error: 'yellow',
	    	fetal: 'red'
	    }
	};
	
	this.logger_ = new (Winston.Logger) ( {
	    transports: [
	    	new Winston.transports.File({ 
				filename: path + name + '-all.log',
				level: 'quotes'
			}),
			new Winston.transports.Console({ level: 'quotes'}),
			new Winston.transports.MongoDB({
				level: 'quotes',
				host: 'localhost',
				port: 27017,
				db: 'fortune-server',
				collection: 'log'
			})
	    ],
	    exceptionHandlers: [
	    	new Winston.transports.File({ filename: path + name + '-exceptions.log' }),
			new Winston.transports.Console()
		],
		levels: custom.levels,
		colors: custom.colors
	});
};

/**
 * Log to level QUOTE
 * @param {String} message Message to Log
 */
Logger.prototype.QUOTE = function(message) {
	console.log('\n----- Quote: -----');
	this.logger_.log('quotes', message);
	console.log('------------------\n')
};


/**
 * Log to level INFO
 * @param {String} message Message to Log
 */
Logger.prototype.INFO = function(message) {
	this.logger_.info(message);
};

/**
 * Log to level WARN
 * @param {String} message Message to Log
 */
Logger.prototype.WARN = function(message) {
	this.logger_.warn(message);
};

/**
 * Log to level ERROR
 * @param {String} message Message to Log
 */
Logger.prototype.ERROR = function(message) {
	this.logger_.error(message);
};

/**
 * Log to level FATAL
 * @param {String} message Message to Log
 */
Logger.prototype.FATAL = function(message) {
	this.logger_.fatal(message);
};

exports.Logger = Logger;
