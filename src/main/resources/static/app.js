var stompClient = null;

function fetchAllActiveUsersBlocking() {
    var request = new XMLHttpRequest();
    request.open('GET', '/allActiveUsers', false);
    request.send(null);

    if (request.status === 200) {
        let users = JSON.parse(request.responseText);
        users.forEach(function (user) {
            handleUsersActivity({
                'type': 'USER_LOGGED_IN',
                'user': user
            });
        });
    }
}

function connect() {
    fetchAllActiveUsersBlocking();

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

function sendGif(gifUrl) {
    sendMessage(gifUrl, 'GIF');
    $('#gifModal').modal('hide');
}

function sendMessage(text, type) {
    let newMessageInput = $("#newMessageInput");
    if (!text) {
        text = newMessageInput.val();
        newMessageInput.val('');
    }
    if (!type) {
        type = 'TEXT'
    }    if (text.startsWith('::')) {
        handleGifSearch(text.substr(2));
    } else {
        if (directMessagesRecipient == null) {
            stompClient.send("/app/publishPublicMessage", {}, JSON.stringify(
                {
                    'text': text,
                    'type': type
                }
            ));
        } else {
            stompClient.send("/app/publishPrivateMessage", {}, JSON.stringify(
                {
                    'text': text,
                    'type': type,
                    'recipient': directMessagesRecipient
                }
            ));
        }

    }

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

    let textSpan = null;
    let video = null;
    if (message.type === "TEXT") {  //text messages front
        textSpan = document.createElement('span');
        textSpan.textContent = message.text;
    } else if (message.type === "GIF") {  //gifs front
        video = document.createElement('video');
        video.autoplay = true;
        video.loop = true;
        video.muted = true;
        let source = document.createElement('source');
        source.src = message.text;
        source.type = "video/mp4";
        video.appendChild(source);
    }

    div.appendChild(timestampSpan);
    div.appendChild(senderSpan);
    if (recipientSpan != null) {
        div.appendChild(arrowSpan);
        div.appendChild(recipientSpan);
    }

    if (textSpan != null) {
        div.appendChild(textSpan);
    }
    if (video != null) {
        div.appendChild(video);
    }

    let allMessagesDiv = $("#allMessagesDiv");
    allMessagesDiv.append(div);

    allMessagesDiv.stop().animate({
        scrollTop: allMessagesDiv[0].scrollHeight
    }, 500);
}

function handleUsersActivity(message) {
    let aside = $('aside');
    if (message.type === "USER_LOGGED_IN") {
        let img = document.createElement('img');
        img.alt = message.user.name;
        img.src = message.user.avatarUrl;
        img.setAttribute("onclick", 'toggleDirectMessageUser(\'' + message.user.name + '\');');

        aside.append(img);
    } else if (message.type === "USER_LOGGED_OUT") {
        let allChildren = aside[0].children;
        for (let i = 0; i < allChildren.length; i++) {
            let child = allChildren[i];
            if (child.alt === message.user.name) {
                aside[0].removeChild(child);
                if (directMessagesRecipient.trim() === message.user.name) {
                    directMessagesRecipient = null;
                }
            }
        }
    }
}

let directMessagesRecipient = null;

function toggleDirectMessageUser(username) {
    console.log("Clicked toggleDirectMessageUser for user ", username);
    let allMessagesDiv = $("aside");
    let allChildren = allMessagesDiv[0].children;
    let avatarClicked = null;
    for (let i = 0; i < allChildren.length; i++) {
        let child = allChildren[i];
        if (child.alt === username) {
            avatarClicked = child;
        }
    }

    if (directMessagesRecipient == null) {
        directMessagesRecipient = username;
        avatarClicked.classList.add('selected');
        fetch('/fetchChatHistory?user=' + username)
            .then(response => response.json())
            .then(obj => console.log('Received obj: ', obj));
    } else if (directMessagesRecipient === username) {
        directMessagesRecipient = null;
        avatarClicked.classList.remove('selected');
    } else {
        toggleDirectMessageUser(directMessagesRecipient);
        toggleDirectMessageUser(username);
    }
}

function handleGifSearch(searchText) {
    $('#gifModalBody').empty();

    fetch('/searchGifs?search=' + searchText)
        .then(response => response.json())
        .then(gifUrls => gifUrls.forEach(function (gifUrl) {
            let body = document.getElementById('gifModalBody');
            let video = document.createElement('video');
            video.autoplay = true;
            video.loop = true;
            video.muted = true;
            let source = document.createElement('source');
            source.src = gifUrl;
            source.type = "video/mp4";
            video.appendChild(source);
            video.setAttribute("onclick", 'sendGif(\'' + gifUrl + '\');');

            body.appendChild(video);
        })).then(function () {
        $('#gifModal').modal('show');
    });
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    connect();
// $( "#connect" ).click(function() { connect(); });
// $( "#disconnect" ).click(function() { disconnect(); });
    $("#sendMessage").click(function () {
        sendMessage();
    });
});