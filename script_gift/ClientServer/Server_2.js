var Http = require('http');
var Util = require('util');

var Server = function() {
	
	var launchTime = 0;
	var requestCount = 0;
	this.httpServer_ = Http.createServer(function(req, res) {
		var responseData = 'Server_2,' + requestCount + '\n';
		requestCount+=1;
		res.write(responseData);
		res.end();
	});
	this.httpServer_.listen(8080, function() {
		console.log("Server Listening on port: " + 8080);
		launchTime = Date.now() / 1000;
	});
	
	var lastLogged = 0;
	
	var perSecond = function() {
		if(requestCount != 0) {
			console.log('Serving ' + requestCount + ' requests/s');
		}
		setTimeout(perSecond, 1000);
		requestCount = 0;
	};
	
	setTimeout(perSecond(), 1000);
};

new Server();

exports.Server = Server;