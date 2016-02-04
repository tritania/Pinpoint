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
console.log("Connection " + socket.id)
    socket.on('findmatch', function (client) {
        if (rooms[rooms.length -1].full == false ) {
            console.log("Adding to room: " + (rooms.length - 1))
            rooms[rooms.length -1].players.push(socket.id);
            if (rooms[rooms.length -1].players.length == 2) {
                rooms[rooms.length -1].full = true; //send game start
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

});

function room() {
    return { players: [], state: null, full: false};
}

