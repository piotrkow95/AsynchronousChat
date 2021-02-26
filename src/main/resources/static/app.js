var stompClient = null;

function connect() {
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/allMessages', function (msg) {
            showNewMessage(JSON.parse(msg.body));
        });
        stompClient.subscribe('/topic/allLogins', function (msg) {
            handleUsersActivity(JSON.parse(msg.body));
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
    let newMessageInput = $("#newMessageInput");
    stompClient.send("/app/publishMessage", {}, JSON.stringify(
        {'text': newMessageInput.val(),
        }
    ));
    newMessageInput.val('');
}

function showNewMessage(message) {

    let div = document.createElement('div');
    let timestampSpan = document.createElement('span');
    timestampSpan.textContent = message.humanReadableTimestamp + ' ';

    let userSpan = document.createElement('span');
    userSpan.textContent = message.user.name + ' ';
    userSpan.style = 'color: ' + message.user.colorCode + ';';

    let textSpan = document.createElement('span');
    textSpan.textContent = message.text;

    div.appendChild(timestampSpan);
    div.appendChild(userSpan);
    div.appendChild(textSpan);

    let allMessagesDiv = $("#allMessagesDiv");
    allMessagesDiv.append(div);

    allMessagesDiv.stop().animate({
        scrollTop: allMessagesDiv[0].scrollHeight
    }, 500);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    function handleUsersActivity(message) {
        let allMessagesDiv = $("#allActiveUsersDiv");
        if (message.type === "USER_LOGGED_IN") {
            let userSpan = document.createElement('span');
            userSpan.textContent = message.username + ' ';
            userSpan.style = 'color: ' + message.colorCode + ';';
            allMessagesDiv.append(userSpan);
        } else if (message.type === "USER_LOGGED_OUT") {
            let allChildren = allMessagesDiv[0].children;
            for (let i = 0; i < allChildren.length; i++) {
                let child = allChildren[i];
                if (child.textContent.trim() === message.username) {
                    allMessagesDiv[0].removeChild(child);
                }
            }
        }
    }

    connect();
    // $( "#connect" ).click(function() { connect(); });
    // $( "#disconnect" ).click(function() { disconnect(); });
    $( "#sendMessage" ).click(function() { sendMessage(); });
});