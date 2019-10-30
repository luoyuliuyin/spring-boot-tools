/**
 * Created by luoyuliuyin on 16-6-15.
 * tools.js
 */

var websocket = null;

if ('WebSocket' in window) {
    var host = window.location.host;
    if ("dump.mogu-inc.com" === window.location.host) {
        host = "10.80.171.61";
    }
    var nickName = $("#spanNickName").text();
    websocket = new WebSocket("ws://" + host + "/websocket/" + nickName);
} else {
    alert('Not support websocket')
}

websocket.onerror = function () {
    //alert("WebSocket error");
};

websocket.onopen = function (event) {
    //alert("open");
};

/*websocket.onmessage = function (event) {
    alert(event.data);
};*/

websocket.onclose = function () {
    //alert("close");
};

//listening window close event. when this window closed,to force close websocket,avoid the window not closing before the websocket closed.
window.onbeforeunload = function () {
    websocket.close();
};

function commandRun() {
    var command = $.trim($("#command").val());
    websocket.send(command);
    var div = $("#jsonShow");
    var begin = true;
    websocket.onmessage = function (event) {
        if (begin) {
            div.html("");
            begin = false;
        }
        div.append(event.data + "<br>");
    };
    div.html("...ing" + "<br>");
}

function download() {
    var file = $("#files").find("option:selected").val();
    window.location.href = "download?file=" + encodeURIComponent(file);
}