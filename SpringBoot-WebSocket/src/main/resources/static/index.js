var stompClient = null;

function setConnected(connected, prefix) {
    $("#" + prefix + "Connect").prop("disabled", connected);
    $("#" + prefix + "Disconnect").prop("disabled", !connected);

    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function connect(destination, prefix) {
    var socket = new SockJS('/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true, prefix);
        console.log('Connected:' + frame);
        console.log("Destination is :" + destination);
        stompClient.subscribe(destination, function (message) {
            console.log("Receive message from server:" + message);
            showMessage(JSON.parse(message.body));
        });
    });
}

function disconnect(prefix) {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false, prefix);
    console.log("Disconnected");
}

function sendMessage(destination, prefix) {
    stompClient.send(destination, {}, $("#" + prefix + "Name").val());
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message.content + "</td></tr>")
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $("#broadcastConnect").click(function () {
        connect('/response/message', 'broadcast');
    });
    $("#specifyConnect").click(function () {
        connect('/user/response/message', 'specify');
    });

    $("#broadcastDisconnect").click(function () {
        disconnect('broadcast');
    });

    $("#specifyDisconnect").click(function () {
        disconnect('specify');
    });

    $("#broadcastSend").click(function () {
        sendMessage("/request/message/broadcast", 'broadcast');
    });
    $("#specifySend").click(function () {
        sendMessage("/request/message/specify", 'specify');
    });
});