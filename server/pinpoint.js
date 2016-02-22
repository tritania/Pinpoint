var express = require('express'),
    app = express(),
    server = require('http').createServer(app),
    io = require('socket.io')(server),
    port = process.env.PORT || 4400,
    rooms = [];

server.listen(port, function () {
  console.log('Starting server on port: ', port);
  rooms.push(room());
});

io.on('connection', function (socket) {
console.log("Connection " + socket.id);
    socket.on('findmatch', function (client) {
        if (rooms.length == 0) {
            rooms.push(room());
        }
        if (rooms[rooms.length -1].players.length < 2) {
            console.log("Adding to room: " + (rooms.length - 1));
            rooms[rooms.length -1].players.push(socket.id);
            if (rooms[rooms.length -1].players.length == 2) {
                io.to(rooms[rooms.length -1].players[0]).emit('mr', 'TP'); //take photo
                io.to(rooms[rooms.length -1].players[1]).emit('mr', 'WFI'); //waiting for imagery
            } else {
                io.to(socket.id).emit('mr', 'LFG'); //Looking for game
            }
        } else {
            console.log("Adding to new room: " + rooms.length);
            rooms.push(room());
            rooms[rooms.length -1].players.push(socket.id);
            io.to(socket.id).emit('mr', 'LFG'); //Looking for game
        }

    });
    
    socket.on('imgd', function (data) {
        for (var i = 0; i < rooms.length; i++) {
            if (rooms[i].players[0] === socket.id || rooms[i].players[1] === socket.id) { //find room with player in it
                if (rooms[i].players[0] === socket.id) {
                    io.to(rooms[i].players[1]).emit('SIMG', data); //send image to other player
                } else {
                    io.to(rooms[i].players[0]).emit('SIMG', data);
                }
            }
        }
    });
    
    socket.on('disconnect', function (socket) {
        console.log('lost connection');
        for (var i = 0; i < rooms.length; i++) {
            if (rooms[i].players[0] === socket.id || rooms[i].players[1] === socket.id) { //find room with player in it
                for (var j = 0; j < 2; j++) {
                    io.to(rooms[i].players[j]).emit('mr', 'LC'); //send disconnect to both players
                }
                rooms.splice(i, 1); //remove room
            }
        }
    });
});

function room() {
    return { players: [], state: null, full: false};
}

