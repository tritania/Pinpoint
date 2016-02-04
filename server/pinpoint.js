var express = require('express');
var app = express();
var server = require('http').createServer(app);
var io = require('socket.io')(server);
var port = process.env.PORT || 4400;

server.listen(port, function () {
  console.log('Starting server on port: ', port);
});

io.on('connection', function (socket) {

    socket.on('findmatch', function (data) {
        socket.emit('mr', 'testing');
    });

});

