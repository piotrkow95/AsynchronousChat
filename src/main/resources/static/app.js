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
        stompClient.subscribe('/user/topic/privateMessages', function (msg) {
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
    let newMessageInput = $("#newMessageInput");
    if (directMessagesRecipient == null) {
        stompClient.send("/app/publishPublicMessage", {}, JSON.stringify(
            {
                'text': newMessageInput.val(),
            }
        ));
    } else {
        stompClient.send("/app/publishPrivateMessage", {}, JSON.stringify(
            {
                'text': newMessageInput.val(),
                'recipient': directMessagesRecipient
            }
        ));
    }
    newMessageInput.val('');
}

function showNewMessage(message) {

    let div = document.createElement('div');
    let timestampSpan = document.createElement('span');
    timestampSpan.textContent = message.humanReadableTimestamp + ' ';

    let senderSpan = document.createElement('span');
    senderSpan.textContent = message.sender.name + ' ';
    senderSpan.style = 'color: ' + message.sender.colorCode + ';';

    let arrowSpan = null;
    let recipientSpan = null;
    if (message.recipient != null) {
        arrowSpan = document.createElement('span');
        arrowSpan.textContent = '➡';

        recipientSpan = document.createElement('span');
        recipientSpan.textContent = ' ' + message.recipient.name + ' ';
        recipientSpan.style = 'color: ' + message.recipient.colorCode + ';';
    }

    let textSpan = document.createElement('span');
    textSpan.textContent = message.text;

    div.appendChild(timestampSpan);
    div.appendChild(senderSpan);
    if (recipientSpan != null) {
        div.appendChild(arrowSpan);
        div.appendChild(recipientSpan);
    }
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
            let userButton = document.createElement('button');
            userButton.textContent = message.username + ' ';
            userButton.style = 'color: ' + message.colorCode + ';';
            userButton.setAttribute("onclick", 'toggleDirectMessageUser(\'' + message.username + '\');');
            // th:onclick="'toggleDirectMessageUser(\'' + ${user.name} + '\')'"
            // toggleDirectMessageUser(this.getAttribute('data1'));

            allMessagesDiv.append(userButton);
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

    let directMessagesRecipient = null;

    function toggleDirectMessageUser(username) {
        console.log("Clicked toggleDirectMessageUser for user ", username);
        let allMessagesDiv = $("#allActiveUsersDiv");
        let allChildren = allMessagesDiv[0].children;
        let buttonClicked = null;
        for (let i = 0; i < allChildren.length; i++) {
            let child = allChildren[i];
            if (child.textContent.trim() === username) {
                buttonClicked = child;
            }
        }

        if (directMessagesRecipient == null) {
            directMessagesRecipient = username;
            buttonClicked.style.backgroundColor = 'red';
            buttonClicked.style.textDecoration = 'underline';
        } else if (directMessagesRecipient === username) {
            directMessagesRecipient = null;
            buttonClicked.style.backgroundColor = '';
            buttonClicked.style.textDecoration = '';
        }
    }

    connect();
    // $( "#connect" ).click(function() { connect(); });
    // $( "#disconnect" ).click(function() { disconnect(); });
    $( "#sendMessage" ).click(function() { sendMessage(); });
});