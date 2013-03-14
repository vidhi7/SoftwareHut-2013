var Http = require('http');
var Util = require('util');

var Client = function() {
	var requests = 100000;
	
	var t1 = Date.now() / 1000;
	for(var i = 0; i < requests; i++) {
		var options = {
			host: 'localhost',
			port: 8060,
			path: '/',
			method: 'GET'
		};
	
		var req = Http.request(options, function(res) {
		
			var dataBuffer = '';
			res.on('data', function(data) {
				dataBuffer += data;
			});
		
			res.on('end', function() {
				console.log('C2S<-' + dataBuffer);
			});
		
			res.on('error', function(error) {
				console.log('Error on Response: ' + res);
			})
		});
	
		req.end();
	
		req.on('error', function(error) {
			console.log('Error in Request: ' + error);
		});
	}
};

new Client();

exports.Client = Client;