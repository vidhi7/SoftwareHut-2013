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
 * First attempt at a load balancer
 * Pret basic since my understanding of Node.js is 
 * next to nothing
 * @ Brasoveanu Andrei Alexandru
 */

// Sets the modules used
var http = require('http'),
httpProxy = require('http-proxy');

//List of potential servers to farward data to
 var servers =  [{host :'ip_server_1', port :9991}, {host : 'ip_server_1',port :9992}];

//Create a stand alone proxy server 
  httpProxy.createServer(function (req, res, proxy) {

//Sends data to potential servers in a round robin manner
  var target = servers.shift();
  proxy.proxyRequest(req, res, target);
  servers.push(target);

//The port it listens for data
   }).listen(9999);

