/*
* jquery.jscontext.js
* jQuery Context Popup Plugin
* http://duncanmcintyre.net/plugins/jscontext/
*
* Copyright (c) 2011 Duncan McIntyre http://duncanmcintyre.net
* Version 1.0
* Dual licensed under the MIT and GPL licenses.
*/

(function ($) {
    $.fn.jscontext = function (options) {
        var settings = {
            html: 'default',
            bind: 'right-click',
            closeOnMouseLeave: false,
            fade: false,
            fadeSpeed: "normal",
            show: null,
            open: null,
            close: null,
            mouseover: null,
            mouseleave: null,
            sticky: false
        };

        var methods = {
            initMenu: function (elm, settings) {
                $("html").mousemove(function (e) {
                    mouse.x = e.pageX;
                    mouse.y = e.pageY;

                    if (element.current != undefined && element.moveit) {
                        element.current.css("top", (mouse.y + 10) + 'px');
                        element.current.css("left", (mouse.x + 10) + 'px');

                        if (settings.mouseover != null)
                            settings.mouseover.call();
                    }
                });

                $("html").click(function () {
                    if (element.close())
                        methods.closeMenu(null, null);
                });

                $("html").mouseleave(function () {
                    methods.closeMenu(null, null);
                });

                switch (settings.bind) {
                    case "right-click":
                        $(elm).bind("contextmenu", function (e) {
                            var elementcurrent = $("#" + elm.id + "jscontext");
                            if (elementcurrent.length == 0) {
                                methods.openMenu(elm, settings);
                                jscontextElm(elm);
                                if (settings.show != null)
                                    settings.show.call();
                                element.moveit = false;
                                return false;
                            }
                        });
                        break;
                    case "click":
                        $(elm).click(function (e) {
                            var elementcurrent = $("#" + elm.id + "jscontext");
                            if (elementcurrent.length == 0) {
                                methods.openMenu(elm, settings);
                                jscontextElm(elm);
                                if (settings.show != null)
                                    settings.show.call();
                                element.moveit = false;
                                return false;
                            }
                        });
                        break;
                    case "hover":
                        $(elm).hover(function (e) {
                            var elementcurrent = $("#" + elm.id + "jscontext");
                            if (elementcurrent.length == 0) {
                                methods.openMenu(elm, settings);
                                jscontextElm(elm);
                                if (settings.show != null)
                                    settings.show.call();
                                if (settings.sticky)
                                    element.moveit = true;
                                return false;
                            }

                        },
                        function (e) {
                            if (settings.sticky) {
                                if (element.close())
                                    methods.closeMenu($(element.current).attr("id"), null);
                            }
                            element.moveit = false;
                        }
                        );
                        break;
                }

            },
            openMenu: function (elm, settings) {
                methods.createMenu(elm, settings);
                element.current = $("#" + elm.id + "jscontext");

                element.current.html(settings.html);
                var jscontextBtn = $(".jscontextBtn");
                var contextWidth = 0;
                if (jscontextBtn.length > 0) {
                    jscontextBtn.each(function () {
                        if (contextWidth < $(this)[0].offsetWidth)
                            contextWidth = $(this)[0].offsetWidth;
                    });
                    element.current.width(contextWidth);
                }

                if (settings.open != null)
                    settings.open.call();

                if (settings.closeOnMouseLeave && !settings.sticky) {
                    element.current.mouseleave(function () {
                        if (element.close())
                            methods.closeMenu($(element.current).attr("id"), null);
                    });
                }

                try {
                    var winWidth = window.innerWidth || document.documentElement.clientWidth || document.body.offsetWidth;
                    if (mouse.x >= winWidth - element.current.width())
                        element.current.css("left", mouse.x - (element.current.width() - 15) + "px");

                }
                catch (e) { }

            },
            createMenu: function (elm, settings) {
                var jscontextID = elm.id + "jscontext";
                var afterClose = function () {
                    var jscontextElement = document.createElement('div');
                    jscontextElement.id = jscontextID;
                    jscontextElement.style.position = 'absolute';
                    jscontextElement.style.width = 'auto';
                    jscontextElement.style.height = 'auto';
                    jscontextElement.style.wordWrap = 'break-word';
                    jscontextElement.style.display = 'none';

                    if (mouse.y == undefined)
                        mouse.y = 0;
                    if (mouse.x == undefined)
                        mouse.x = 0;
                    jscontextElement.style.top = (mouse.y - (settings.sticky == true ? 0 : 15)) + 'px';
                    jscontextElement.style.left = (mouse.x - (settings.sticky == true ? 0 : 15)) + 'px';
                    jscontextElement.style.zIndex = '9999';
//                    jscontextElement.style.background = '#e4e4e4';
//                    jscontextElement.style.border = '1px solid silver';
                    document.body.appendChild(jscontextElement);
                    var jscontext = $("#" + jscontextID);
                    jscontext.addClass("jscontextContainer");

                    if (settings.fade) {
                        jscontext.fadeIn(settings.fadeSpeed);
                        element.fade = true;
                    }
                    else {
                        jscontext.show();
                        element.fade = false;
                    }
                }

                methods.closeMenu(null, afterClose);

            },
            closeMenu: function (elmID, afterClose) {
                var remove = function () {
                    if (elmID) {
                        if ($("#" + elmID).length > 0)
                            document.body.removeChild(document.getElementById(elmID));
                    }
                    else {
                        $(".jscontextContainer").remove();
                    }
                }
                if (element.fade) {
                    if (elmID) {
                        if ($("#" + elmID).length > 0)
                            $("#" + elmID).fadeOut(settings.fadeSpeed, remove);
                    }
                    else {
                        $(".jscontextContainer").fadeOut(settings.fadeSpeed, remove);
                    }
                }
                else {
                    remove.call();
                }
                if (settings.close != null)
                    settings.close.call();

                if (afterClose != null)
                    afterClose.call();
            }
        };

        return this.each(function () {
            if (options && typeof options === 'object') {
                $.extend(settings, options);
                methods.initMenu(this, settings);
            }
            else if (typeof options === 'string') {
                switch (options) {
                    case "":
                        break;
                }
            }

        });

    };
})(jQuery);

jscontext = function(option) {
    switch (option) {
        case "close":
            $(".jscontextContainer").remove();
            break;
    }
}

jscontextElm = function(elm) {
    jscontextObj = $(elm);
}

var element = {
    current: null,
    moveit: false,
    fade: false,
    close: function () {
        if (element.current == null)
            return true;
        if ((mouse.x < element.current.position().left + 5) || (mouse.x > (element.current.position().left + element.current.width() - 5)) || (mouse.y < element.current.position().top + 5) || (mouse.y > (element.current.position().top + element.current.height() - 5)))
            return true;
        else
            return false;
    }
};

var mouse = {
    x: null,
    y: null
};

var jscontextObj = $();