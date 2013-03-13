/*
 * Copyright (C) 2013 Omada Programming(Brasoveanu Andrei Alexandru, Dominic Lee,Delvin Varghese, Konstantinos Akrivos)
 * Permission is hereby granted, free of charge, 
 * to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, 
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit 
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 * 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/**
 * A load balancer
 * Keeps a list of server and farwards data to and from them
 * in an round robin manner
 * @ Brasoveanu Andrei Alexandru
 */



// Gets modules used
var http = require('http'),
httpProxy = require('http-proxy');

//This is the list of servers to farward data to
var servers =  [{
    host : 'localhost', 
    port : 8070
}, {
    host : 'localhost',
    port : 8080
}, {
    host: 'localost',
    port: 8090
}];
var target;

var server = httpProxy.createServer(function (req, res, proxy) {
    
    target = servers.shift();
          
    proxy.proxyRequest(req, res, target);

    servers.push(target);
   
    server.proxy.on('end' , function() {
        console.log("Request solved");
    });  
    server.proxy.on('close', function() {
        console.log("Client disconected");
    });
      
    
}).listen(8060, 'localhost');




