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
    host :'ip_server_1', 
    port :9991
}, {
    host : 'ip_server_1',
    port :9992
}, {
    host: 'ip_server_3',
    port: 9991
}];

//Shuffles data between the servers in the list
httpProxy.createServer(function (req, res, proxy) {
    var target;
    var srvFound = Boolean(0);
    while(srvFound == 0){
        target = servers.shift();
        try{            
            proxy.proxyRequest(req, res, target);
            req.on('connect', function(connect){
                srvFound = 1;
            });
            
        }finally{
            servers.push(target);
        }
       


    }
}).listen(9999, 'ip_addres_of_kannel');//Listens to ip and port ...

