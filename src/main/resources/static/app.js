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
    }
    if (text.startsWith('::')) {
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
    let isPublicMsg = message.recipient == null;
    let isPrivateMessage = !isPublicMsg;
    let weAreInterestedInPublicConversation = directMessagesRecipient == null;
    let weAreInterestedInThisPrivateConversation = !isPublicMsg && ((message.sender.name === directMessagesRecipient)
        || (message.recipient.name === directMessagesRecipient));
    let isActiveChannelMessage = (isPublicMsg && weAreInterestedInPublicConversation) || (isPrivateMessage && weAreInterestedInThisPrivateConversation);

    if (!isActiveChannelMessage) {
        let img = findAsideImgFromUsername(isPublicMsg ? null : message.sender.name);
        img.classList.add('notification');
        console.log('Skip message ', message);
        return;
    }

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
    } else if (message.type === 'LINK') {
        textSpan = document.createElement('span');
        let a = document.createElement('a');
        a.href = message.text;
        let fileName = message.text.substring(message.text.lastIndexOf('/') + 1);
        fileName = decodeURI(fileName);
        a.innerText = fileName;
        a.target = '_blank';
        textSpan.appendChild(a);
    } else if (message.type === "GIF") {
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

function findAsideImgFromUsername(searchedAltText) {
    if (searchedAltText == null) {
        searchedAltText = 'Public';
    }
    console.log("Called findAsideImgFromUsername for user ", searchedAltText);
    let allMessagesDiv = $("aside");
    let allChildren = allMessagesDiv[0].children;
    let avatarClicked = null;
    for (let i = 0; i < allChildren.length; i++) {
        let child = allChildren[i];
        if (child.alt === searchedAltText) {
            avatarClicked = child;
        }
    }
    return avatarClicked;
}

function toggleDirectMessageUser(username) {
    let oldClicked = findAsideImgFromUsername(directMessagesRecipient);
    let newClicked = findAsideImgFromUsername(username);

    oldClicked.classList.remove('selected');
    newClicked.classList.add('selected');
    directMessagesRecipient = username;
    displayMessageWindow();
}

function handleGifSearch(searchText) {
    $('#gifModalBody').empty();
    let img = findAsideImgFromUsername(directMessagesRecipient);
    img.classList.remove('notification');

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

function displayMessageWindow() {
    let allMessagesDiv = $("#allMessagesDiv");
    allMessagesDiv.empty();
    fetch('/fetchChatHistory' +
        (directMessagesRecipient == null ? '' :
            ('?user=' + directMessagesRecipient)))
        .then(response => {
            response.json().then(msgs => msgs.forEach(function (msg) {
                showNewMessage(msg);
            }));
        })
}

function uploadMultipleFiles(files) {
    var formData = new FormData();
    for(var index = 0; index < files.length; index++) {
        formData.append("files", files[index]);
    }

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/uploadMultipleFiles");

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status === 200) {
            for(var i = 0; i < response.length; i++) {
                let fileDownloadUri = response[i].fileDownloadUri;
                sendMessage(fileDownloadUri, 'LINK');
            }

            let multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
            multipleFileUploadInput.value = "";
        } else {
            console.log((response && response.message) || "Some Error Occurred");
        }
    }

    xhr.send(formData);
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    connect();
    $( "#multipleFileUploadInput" ).change(function() {
        sendMessage();
    });


    let publicImg = findAsideImgFromUsername(directMessagesRecipient);
    publicImg.classList.add('selected');
});