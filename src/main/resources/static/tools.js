/**
 * Created by luoyuliuyin on 16-6-15.
 * tools.js
 */

var logSocket = null;

function commandRun() {
    var command = $.trim($("#command").val());
    var div = $("#jsonShow");

    if ('WebSocket' in window) {
        var host = window.location.host;
        if ("127.0.0.1" !== window.location.host) {
            host = "104.194.85.123";
        }

        logSocket = new WebSocket("ws://" + host + "/websocket?command=" + encodeURIComponent(command));

        //接收消息函数
        var begin = true;
        logSocket.onmessage = function (event) {
            if (begin) {
                div.html("");
                begin = false;
            }
            div.append(event.data + "<br>");
        };
        div.html("...ing" + "<br>");
    } else {
        alert('Not support websocket')
    }
}

function download() {
    var file = $("#files").find("option:selected").val();
    window.location.href = "download?file=" + encodeURIComponent(file);
}