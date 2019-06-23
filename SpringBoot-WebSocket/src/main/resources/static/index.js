var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);

    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function connect() {
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected:' + frame);
        stompClient.subscribe('/response/message', function (message) {
            console.log("Receive message from server:" + message);
            showMessage(JSON.parse(message.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    stompClient.send("/request/message", {}, $("#name").val());
}

function showMessage(message) {
    Notification.requestPermission(function (status) {
        new Notification(message.title, {
            body: message.content,
            icon: "https://helloworlde.github.io/css/images/logo.png"
        });
    });
    $("#messages").append("<tr><td>" + message.title + ":" + message.content + "</td></tr>")
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $("#connect").click(function () {
        connect();
    });

    $("#disconnect").click(function () {
        disconnect();
    });

    $("#send").click(function () {
        sendMessage();
    });

    if (window.Notification && Notification.permission !== "granted") {
        Notification.requestPermission(function (status) {
            if (Notification.permission !== status) {
                Notification.permission = status;
            }
        });
    }
});