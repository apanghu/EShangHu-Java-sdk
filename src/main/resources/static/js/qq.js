function is_weixn_qq() {
    var ua = navigator.userAgent.toLowerCase();
    if (ua.match(/MicroMessenger/i) == "micromessenger") {
        return "weixin";
    } else if (ua.match(/MQQBrowser/i) == "mqqbrowser" && ua.match(/TBS/i) == "tbs") {
        return "qq";
    }
    return "none";
}

function check() {
    var result_check = is_weixn_qq();
    console.log("userAgent:" + result_check);
    var url = window.location.href;
    console.log("url:" + url);
    if (result_check == "qq") {
        if (url.indexOf("?") == -1) {
            mqq.ui.openUrl({target: 2, url: url + "?spm=" + new Date().getTime()});
        } else {
            mqq.ui.openUrl({target: 2, url: url + "&spm=" + new Date().getTime()});
        }
    } else if (result_check == "weixin") {
        alert("检测到您当前在微信中打开本站,为了保证浏览效果,请点击右上角,选择'用浏览器打开'本站,谢谢!");
    }
}

window.onload = check();


