!function (e) {
    var t, n, o, i, d, a,
        c = '<svg><symbol id="icon-docker" viewBox="0 0 1024 1024"><path d="M1004.544 466.08a161.504 161.504 0 0 0-119.52-10.816 158.88 158.88 0 0 0-64.608-101.216l-12.8-10.08-10.848 12.16a133.888 133.888 0 0 0-25.28 96.32 123.552 123.552 0 0 0 24.128 64.704 187.648 187.648 0 0 1-34.752 15.296A232.416 232.416 0 0 1 689.216 544H13.568l-1.44 15.136a282.656 282.656 0 0 0 23.776 147.2l9.248 18.336 1.056 1.728c63.52 104.896 190.24 159.232 311.808 159.232 235.392 0 414.368-112.352 503.552-328.224 59.584 3.04 120.544-14.112 149.696-69.408l7.424-14.112-14.144-7.936zM210.464 739.2a52.832 52.832 0 1 1 54.4-52.832 53.664 53.664 0 0 1-54.4 52.832z"  ></path><path d="M210.464 658.432a27.808 27.808 0 1 0 28.608 27.84 28.224 28.224 0 0 0-28.608-27.84M64 416h96v96H64z m128 0h96v96h-96z m0-128h96v96h-96z m128 0h96v96h-96z m0 128h96v96h-96z m128 0h96v96h-96z m128 0h96v96h-96zM448 288h96v96h-96z m0-128h96v96h-96z"  ></path></symbol></svg>',
        l = (l = document.getElementsByTagName("script"))[l.length - 1].getAttribute("data-injectcss");
    if (l && !e.__iconfont__svg__cssinject__) {
        e.__iconfont__svg__cssinject__ = !0;
        try {
            document.write("<style>.svgfont {display: inline-block;width: 1em;height: 1em;fill: currentColor;vertical-align: -0.1em;font-size:16px;}</style>")
        } catch (e) {
            console && console.log(e)
        }
    }

    function s() {
        d || (d = !0, o())
    }

    t = function () {
        var e, t, n;
        (n = document.createElement("div")).innerHTML = c, c = null, (t = n.getElementsByTagName("svg")[0]) && (t.setAttribute("aria-hidden", "true"), t.style.position = "absolute", t.style.width = 0, t.style.height = 0, t.style.overflow = "hidden", e = t, (n = document.body).firstChild ? (t = n.firstChild).parentNode.insertBefore(e, t) : n.appendChild(e))
    }, document.addEventListener ? ~["complete", "loaded", "interactive"].indexOf(document.readyState) ? setTimeout(t, 0) : (n = function () {
        document.removeEventListener("DOMContentLoaded", n, !1), t()
    }, document.addEventListener("DOMContentLoaded", n, !1)) : document.attachEvent && (o = t, i = e.document, d = !1, (a = function () {
        try {
            i.documentElement.doScroll("left")
        } catch (e) {
            return void setTimeout(a, 50)
        }
        s()
    })(), i.onreadystatechange = function () {
        "complete" == i.readyState && (i.onreadystatechange = null, s())
    })
}(window);
