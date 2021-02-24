var stompClient = null;

function connect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/allMessages', function (msg) {
            showNewMessage(JSON.parse(msg.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendMessage() {
    stompClient.send("/app/publishMessage", {}, JSON.stringify({'text': $("#newMessageInput").val()}));
}

function showNewMessage(message) {
    $("#allMessagesDiv")
        .append(message.humanReadableTimestamp + " " + message.user.name + " " + message.text);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    connect();
    // $( "#connect" ).click(function() { connect(); });
    // $( "#disconnect" ).click(function() { disconnect(); });
    $( "#sendMessage" ).click(function() { sendMessage(); });
});