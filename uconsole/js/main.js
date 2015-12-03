/*
 * AdroitLogic UltraESB Enterprise Service Bus
 *
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * GNU Affero General Public License Usage
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program (See LICENSE-AGPL.TXT).
 * If not, see http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Commercial Usage
 *
 * Licensees holding valid UltraESB Commercial licenses may use this file in accordance with the UltraESB Commercial
 * License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written
 * agreement between you and AdroitLogic.
 *
 * If you are unsure which license is appropriate for your use, or have questions regarding the use of this file,
 * please contact AdroitLogic at info@adroitlogic.com
 */

function getCallURLBase() {
    var callURLBase = $.cookie("callURLBase");
    if (callURLBase == null) {
        callURLBase = window.location.protocol + "//" + window.location.host + window.location.pathname.substr(0, window.location.pathname.indexOf("/", 1) + 1) + "services/";
        if ($.cookie("secure_mode") == "true") {
            $.cookie("callURLBase", callURLBase, { path: '/', secure : true});
        } else {
            $.cookie("callURLBase", callURLBase, { path: '/'});
        }
    }
    return callURLBase;
}

function encodeURLs() {
    $("a").each(function (i) {
        // hack to make it work on IE
        if ($(this).attr('href') != undefined && $(this).attr('href') != null && $(this).attr('href').indexOf('about:') === 0) {
            $(this).attr('href', getCallURLBase().split('services/')[0] + $(this).attr('href').split('about:')[1]);
        }
        var myUrl = $(this).attr('href');
        $(this).attr('href', encodeURI(myUrl));
    });
}

function onLoadContentPane() {
    var breadcrumb = $.parseJSON($.cookie("breadcrumb"));
    if (breadcrumb == null) {
        breadcrumb = [];
    }
    var currentTitle = $("#container").attr("title");
    var count = breadcrumb.length;

    if (count > 0) {
        if (breadcrumb[count - 1].title != currentTitle) {
            var bcurl = window.location.href;
            if (breadcrumb.length > 1) {
                bcurl += '&bc=' + breadcrumb.length;
            }
            breadcrumb[breadcrumb.length] = {"title": currentTitle, "link":bcurl};
            if ($.cookie("secure_mode") == "true") {
                $.cookie("breadcrumb", JSON.stringify(breadcrumb), { path: '/', secure: true });
            } else {
                $.cookie("breadcrumb", JSON.stringify(breadcrumb), { path: '/'});
            }
        } else {
            count--;
        }
    }

    var breadcrumbHTML = "";
    for (var i = 0; i < count; i++) {
        if (i > 0) {
            breadcrumbHTML += '&nbsp;&gt;&nbsp;';
        }
        breadcrumbHTML += '<a href="' + breadcrumb[i].link + '">' + breadcrumb[i].title + '</a>';
    }

    breadcrumbHTML += '&nbsp;&gt;&nbsp;' + currentTitle;
    $("#breadcrumbs").html(breadcrumbHTML);

    $(".ultraHelp").each(function(helpIndex) {
        var helpTitle = $(this).attr("title");
        var helpHeader = "";
        if (helpTitle != null || helpTitle != "") {
            helpHeader = "<header class='ultraHelpHeader fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix'>" + helpTitle + "</header>"
        }
        var h_contextHtml = helpHeader + "<div class='generatedHelp'>" + $(this).html() + "</div>";
        var helpClass = "ultrHelpIcon" + helpIndex;
        $(this).html("<img src='icons/help.png' class='clickable " + helpClass + "' title='Click here to see the context sensitive help'/>");
        $("." + helpClass).jscontext({
            html: h_contextHtml,
            bind: "click",
            fade: true
        });
    });
    encodeURLs();
}

function loadContentPane(pageName) {

    if (window.top !== window.self) {
        document.documentElement.style.display = 'none';
        window.top.location.replace(window.self.location);
    } else {
        document.documentElement.style.display = 'block';
    }
    $("input").attr("autocomplete","off");
    setTimeout("logoutTOConsole()", 900000);

    if ($.browser.msie && $.browser.version < 10 && pageName != "dashboard.html") {
        $('#main').loadShiv(pageName, function(response, status, xhr) {
            onLoadContentPane();
        });
    } else {
        $('#main').load(pageName, function(response, status, xhr) {
            onLoadContentPane();
        });
    }
}

function getURLParameter(key) {
    var url = window.location.href;
    if (url.indexOf('?') == -1) {
        return null;
    }
    var hashes = url.slice(url.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        if (key == hash[0]) {
            return hash[1];
        }
    }

    return null;
}


function getURLParams() {
    if (window.location.href.indexOf("?") == -1) {
        return null;
    }
    var urlParamString = window.location.href.substring(window.location.href.indexOf("?") + 1);
    var retStr = "{";
    var urlParams = urlParamString.split("&");
    for (var i = 0; i < urlParams.length; i++) {
        var keyValue = urlParams[i].split("=");
        if (i > 0) {
            retStr += ",";
        }
        retStr += keyValue[0];
        if (keyValue.length > 1) {
            retStr += ":";
            retStr += ("'" + keyValue[1] + "'");
        }
    }
    retStr += "}";
    return eval("(" + retStr + ")");
}

function ajaxErrorHandler(e, jqxhr, settings, exception) {
    if (jqxhr.status == 412) {
        if ($.cookie("secure_mode") == "true") {
            $.cookie("logged_in", null, {path:'/', secure: true });
            $.cookie("callURLBase", null, {path:'/', secure: true });
        } else {
            $.cookie("logged_in", null, {path:'/', secure: true });
            $.cookie("callURLBase", null, {path:'/', secure: true });
        }
        showError("Your session is invalid, please re-login", "login.html");
    } else if (jqxhr.status == 401) {
        $("#container").html("<h1 style='color:red'>Authorization Failure!!</h1><br/><a href='login.html'>Re-login</a> | <a href='#' onclick='javascript:history.back()'>Go-Back</a>");
        showError("You are not authorized, please check with the administrator for permissions or re-login with different account");
    } else if (jqxhr.status == 500) {
        if (jqxhr.responseText.indexOf("Lost") === 0) {
            showError(jqxhr.responseText, "login.html");
        } else {
            showError(jqxhr.responseText);
        }

    } else {
        showError("Cannot connect to the console : " + jqxhr.status, "login.html");
    }
}

function navigate(url) {
    loadPage(url);
}

function logoutTOConsole() {
    if ($.cookie("secure_mode") == "true") {
        $.cookie("logged_in", null, {path:'/', secure: true });
        $.cookie("password", null, {path:'/', secure: true });
        $.cookie("callURLBase", null, {path:'/', secure: true });
    } else {
        $.cookie("logged_in", null, {path:'/', secure: true });
        $.cookie("password", null, {path:'/', secure: true });
        $.cookie("callURLBase", null, {path:'/', secure: true });
    }
    clearBreadcrumb();
    $.ajax({
        type: 'POST',
        url: getCallURLBase() + 'usermgt/logout',
        success:function() {
            window.location.href = "login.html";
        },
        error:function() {
            window.location.href = "login.html";
        }
    });
}

function getInstances() {

    // reset the callURLBase cookie in-order to make sure a different path web-app is picked up
    if ($.cookie("secure_mode") == "true") {
        $.cookie("callURLBase", null, {path:'/', secure: true });
    } else {
        $.cookie("callURLBase", null, {path:'/'});
    }

    //if ($.browser.webkit || $.browser.mozilla) {
    //    $("#compatibility").css("display", "none");
    //}

    var jqxhr = $.getJSON(getCallURLBase() + "instances/all", function(dataset) {
        var data = {"instances":[]};
        var dataDefined = {"instances":[]};
        for (var key in dataset) {
            if (dataset[key].indexOf("(") != -1) {
                data.instances[data.instances.length] = {"val":key, "name":dataset[key]}
            } else {
                dataDefined.instances[dataDefined.instances.length] = {"val":key, "name":dataset[key]}
            }
        }

        if (data.instances.length == 0) {
            data.instances[0] = {"val":"NONE", "name":"- NONE -"};
            $('#localInstances').attr('disabled', 'true');
            $('#instanceLocal').attr('disabled', 'true');
            if (dataDefined.instances.length != 0) {
                $('#instanceDefined').attr('checked', 'checked');
            } else {
                $('#instanceRemote').attr('checked', 'checked');
            }
            $('.local').removeClass("shown");
            $('.local').addClass("hidden");
        } else {
            $('#instanceLocal').removeAttr('disabled');
            $('.local').removeClass("hidden");
            $('.local').addClass("shown");
            $('#instanceLocal').attr('checked', 'checked');
        }

        if (dataDefined.instances.length == 0) {
            $(".defined").removeClass("shown");
            $(".defined").addClass("hidden");
        } else {
            $(".defined").removeClass("hidden");
            $(".defined").addClass("shown");
        }

//        if($.cookie("remote_jmx_remind")) {
//            $('#instanceRemote').attr('checked', 'checked');
//            $('#remoteJMXUrl').val($.cookie("remote_jmx_remind"));
//            $('#remember').attr('checked', 'checked');
//        }

        var dir = {
            'option':{
                "instance <- instances":{
                    ".":"#{instance.name}",
                    "@value":"instance.val"
                },
                sort: function(a, b) {
                    return a.name > b.name ? 1 : -1;
                }
            }
        };

        $('select#localInstances').render(data, dir);
        $('select#definedInstances').render(dataDefined, dir);
    });
}

function hideJMXCredentialInput() {
    if ($('#jmxSecurity').attr("class") == "shown") {
        $('#jmxSecurity').removeClass("shown");
        $('#jmxSecurity').addClass("hidden");
        $('.secure_jmx').removeClass("secure_jmx_visible");
        $('#secure_jmx_icon').removeClass("ui-icon-triangle-1-n");
        $('#secure_jmx_icon').addClass("ui-icon-triangle-1-s");
    }
}

function showHideJMXCredentialInput() {
    if ($('#jmxSecurity').attr("class") == "hidden") {
        if ($('#instanceRemote').attr('checked') || $('#instanceDefined').attr('checked')) {
            $('.secure_jmx').addClass("secure_jmx_visible");
            $('#secure_jmx_icon').addClass("ui-icon-triangle-1-n");
            $('#secure_jmx_icon').removeClass("ui-icon-triangle-1-s");
            $('#jmxSecurity').removeClass("hidden");
            $('#jmxSecurity').addClass("shown");
        } else {
            showError("Secure JMX access only applies to remote JMX connections", null);
        }
    } else {
        $('#jmxSecurity').removeClass("shown");
        $('#jmxSecurity').addClass("hidden");
        $('#secure_jmx_icon').removeClass("ui-icon-triangle-1-n");
        $('#secure_jmx_icon').addClass("ui-icon-triangle-1-s");
        $('.secure_jmx').removeClass("secure_jmx_visible");
    }
}

function switchJMXURL() {
    $('#switchServer').html('<label class="form-field local hidden"><input type="radio" name="instanceType" id="instanceLocal" onclick="hideJMXCredentialInput()"/> Local instance </label><select class="local hidden" id="localInstances" onfocus="$(\'#instanceLocal\').attr(\'checked\', \'checked\'); hideJMXCredentialInput();"><option value="local">Local</option></select><br class="local "/><label class="form-field defined hidden"><input type="radio" name="instanceType" id="instanceDefined"/> Defined instance </label><select id="definedInstances" class="defined hidden" onfocus="$(\'#instanceDefined\').attr(\'checked\', \'checked\')"><option value="local">Defined</option></select><br class="defined hidden"/> <label class="form-field"><input type="radio" name="instanceType" id="instanceRemote"/> Remote JMX URL </label><input type="text" class="jmxURL" id="remoteJMXUrl" onfocus="$(\'#instanceRemote\').attr(\'checked\', \'checked\');"/> <br/><div class="secure_jmx"><a onclick="showHideJMXCredentialInput()" href="#">Secure JMX access</a><span id="secure_jmx_icon" title="ui-icon-triangle-1-s" class="ui-icon ui-icon-triangle-1-s secure_jmx_icon">&nbsp;</span><div id="jmxSecurity" class="hidden"><br /><label class="form-field">JMX Username </label><input type="text" class="jmxUsername" id="jmxUsername" value=""/><br/><label class="form-field">JMX Password </label><input type="password" class="jmxPassword" id="jmxPassword" value=""/></div></div><br/><div><label class="form-field cluster hidden"><input type="radio" name="instanceType" id="instanceCluster"/>Detected Cluster Nodes</label><select id="clusterJMXURLs" class="hidden"><option></option></select></div>');
    getInstances();

    if ($.cookie("clustering") == "true") {
        $('.cluster').removeClass("hidden");
        $('.cluster').addClass("shown");
        $('#clusterJMXURLs').removeClass("hidden");
        $('#clusterJMXURLs').addClass("shown");

        var jqxhr = $.getJSON(getCallURLBase() + "cluster/getJMXServiceURLs", function(dataset) {
            if (dataset != null) {
                var urlArray = [{name:"",url:""}];
                var currentServerName = $.cookie("server_name");
                for (var uIndex in dataset) {
                    if (uIndex != currentServerName) {
                        urlArray[urlArray.length] = {name:uIndex,url:dataset[uIndex]};
                    }
                }
                var data = {urls:urlArray};
                var dir = {
                    'option':{
                        "url <- urls":{
                            ".":function(url) {if (url.item.name=="") {return url.item.url;} else {return url.item.name + ' (' + url.item.url + ')';}},
                            "@value":"url.url"
                        },
                        sort: function (a, b) {
                            return a.name > b.name ? 1 : -1;
                        }
                    }
                };
                $('#clusterJMXURLs').render(data, dir);
            }
            $('#clusterJMXURLs').combobox();
        });
    }

    $("#switchServer").dialog({
        modal: true,
        buttons: {
            Ok: function() {
                $(this).dialog("close");

                if ($('#instanceRemote').attr('checked')) {
                    if ($.cookie("secure_mode") == "true") {
                        $.cookie("remote_jmx", $("#remoteJMXUrl").attr("value"), { path: '/', secure: true } );
                    } else {
                        $.cookie("remote_jmx", $("#remoteJMXUrl").attr("value"), { path: '/'} );
                    }
                }

                var params = '{';
                if ($('#instanceLocal').attr('checked')) {
                    params += ( '"jmxURL": "' + $('#localInstances').attr('value') + '",');
                    params += ('"jmxUsername": "local"');
                }
                if ($('#instanceRemote').attr('checked')) {
                    params += ('"jmxURL": "' + $("#remoteJMXUrl").attr("value") + '"');
                    if ($('#jmxSecurity').attr("class") == "shown") {
                        params += (',"jmxUsername": "' + $("#jmxUsername").attr("value") + '",');
                        params += ('"jmxPassword": "' + $("#jmxPassword").attr("value") + '"');
                    }
                }
                if ($('#instanceDefined').attr('checked')) {
                    params += ('"jmxURL": "' + $("#definedInstances").attr("value") + '"');
                }
                if ($('#instanceCluster').attr('checked')) {
                    var serviceURL = $('#clusterJMXURLs').val();
                    var nameIndex = serviceURL.indexOf('(');
                    if (nameIndex != -1) {
                        serviceURL = serviceURL.substring(nameIndex + 1, serviceURL.length - 1);
                    }

                    params += ('"jmxURL": "' + serviceURL + '",');
                    params += ('"jmxUsername": "default"');
                }
                params += '}';

                $.ajax({
                    type: 'POST',
                    url: getCallURLBase() +  'usermgt/switchServer/',
                    data: params,
                    dataType: 'text',
                    contentType: 'application/json',
                    success: function(msg) {
                        $.cookie("server_name", msg, { path: '/' });
                        showInfo('Successfully switched the server to ' + msg + '.', window.location.href.substr(0, window.location.href.length-1));
                    }
                }).error(function() {
                    showError("Failed to switch the server of the url: " + $("#remoteJMXUrl").attr("value")  + ", please check the JMX URL, console credentials, and JMX credentials if you are connecting to a secure JMX service", "login.html");
                });
            },
            Cancel: function() {
                $(this).dialog("close");
            }
        }
    });
}

function clearBreadcrumb() {
    $.cookie("breadcrumb", null, {path:'/'});
}

function loadPage(page) {
    window.location.href = page;
    if (page.indexOf('#') != -1) {
        window.location.reload(true);
    }
}

function showWarning(warning, page) {
    $("#msgWarning").text(warning);
    $("#warning").dialog({
        modal: true,
	    buttons: {
		    Ok: function close() {
			    $(this).dialog("close");
			    if (page != null) {
	                loadPage(page);
			    }
		    }
	    }
    });
}

function showInfo(info, page) {
    $("#info").dialog({
        closeOnEscape: false,
        open: function (event, ui) {
            $(".ui-dialog-titlebar-close").hide();
        }
    });
    $("#msgInfo").text(info);
    $("#info").dialog({
        modal: true,
	    buttons: {
		    Ok: function close() {
			    $(this).dialog("close");
			    if (page != null) {
	                loadPage(page);
			    }
		    }
	    }
    });
}

function showInfoWithCallback(info, onOK) {
    $("#info").dialog({
        closeOnEscape: false,
        open: function (event, ui) {
            $(".ui-dialog-titlebar-close").hide();
        }
    });
    $("#msgInfo").text(info);
    $("#info").dialog({
        modal: true,
	    buttons: {
		    Ok: function close() {
			    $(this).dialog("close");
			    onOK();
		    }
	    }
    });
}

function showError(msg, page) {

    if (!msg) {
        msg = "Unknown error - Try to login again";
    }
    $("#msgError").text(msg);
    $("#error").dialog({
        modal: true,
	    buttons: {
		    Ok: function close() {
			    $(this).dialog("close");
			    if (page != null) {
	                loadPage(page);
			    }
		    }
	    }
    });
}

function showConfirmation(msg, onConfirm) {
    $("#msgConfirm").text(msg);	
    $("#confirm").dialog({
	    modal: true,
	    buttons: {
		    Ok: function() {
	            $(this).dialog("close");
                onConfirm(); 
            }, 
		    Cancel: function() {
			    $(this).dialog("close");
		    }
	    }
    });
}

function showConfirmationWithCancelCallback(msg, onConfirm, onCancel) {
    $("#msgConfirm").text(msg);
    $("#confirm").dialog({
	    modal: true,
	    buttons: {
		    Ok: function() {
	            $(this).dialog("close");
                onConfirm();
            },
		    Cancel: function() {
			    $(this).dialog("close");
                onCancel();
		    }
	    }
    });
}

function formatNumber(val) {
    if (val == 0) {
        return 0;
    }
    return jQuery.formatNumber(val, {format:"#,###", locale:"us"});
}

function formatDecimalNumber(val) {
    return jQuery.formatNumber(val, {format:"#,##0.##", locale:"us"});
}
var minifiers = ['', 'K', 'M', 'G', 'T']
function formatDecimalNumberWithMinify(val) {
    var minifier = 0;
    while (val > 1000) {
        val = val/1000;
        minifier++;
    }
    return jQuery.formatNumber(val, {format:"#,##0.##", locale:"us"}) + minifiers[minifier];
}
function formatNumberWithMinify(val) {
    if (val == 0) {
        return 0;
    }
    var minifier = 0;
    while (val > 1000) {
        val = val/1000;
        minifier++;
    }
    return jQuery.formatNumber(val, {format:"#,###", locale:"us"}) + minifiers[minifier];
}
function formatDecimalNumber4(val) {
    return jQuery.formatNumber(val, {format:"#,##0.####", locale:"us"});
}
function formatDecimalNumber4WithMinify(val) {
    var minifier = 0;
    while (val > 1000) {
        val = val/1000;
        minifier++;
    }
    return jQuery.formatNumber(val, {format:"#,##0.####", locale:"us"}) + minifiers[minifier];
}

function escapeSpecialChars(str) {
    return str.replace(/\"/g, "\\\"").replace(/\n/g, "\\n").replace(/\r/g, "\\r");
    /*return str.replace(/\\/g, "\\\\")
        .replace(/\n/g, "\\n")
        .replace(/\t/g, "\\t")
        .replace(/\f/g, "\\f")
        .replace(/"/g, "\\\"")
        .replace(/'/g, "\\\'")
        .replace(/\&/g, "\\&");*/
}

function formattedLocaleDateTime(millyTime) {
    var dat = new Date(millyTime);
    return dat.toLocaleDateString() + " - " + dat.toLocaleTimeString();
}

function formattedTimeDif(milliseconds) {
    var milSecondsRem = milliseconds % 1000;
    var seconds = (milliseconds - milSecondsRem)/1000;
    var secondsRem = seconds % 60;
    var minutes = (seconds - secondsRem)/60;
    if (minutes == 0) {
        return "" + secondsRem + " sec"
    }
    var minutesRem = minutes % 60;
    var hours = (minutes - minutesRem)/60;
    if (hours == 0) {
        return "" + minutesRem + " min " + secondsRem + " sec";
    }
    var hoursRem = hours % 24;
    var days = (hours - hoursRem)/24;
    if (days == 0) {
        return "" + hoursRem + " hrs " + minutesRem + " min " + secondsRem + " sec";
    } else {
        return "" + days + " days " + hoursRem + " hrs " + minutesRem + " min " + secondsRem + " sec";
    }
}

function listToCommaSeparatedString(contentList) {
    var retStr = "";
    if (contentList == null) {
        return retStr;
    }
    for (var i = 0; i < contentList.length; i++) {
        if(i > 0) {
            retStr += ", ";
        }
        retStr += contentList[i].nodeName;
    }
    return retStr;
}

function getFormattedDate(date, format) {
  var months = ["January","February","March","April","May","June","July","August","September","October","November","December"],
  date = new Date(Date.parse(date));
  return months[date.getMonth()] + ' ' + date.getDate();
}

function getFormattedMilli(millis) {
    var result = "";
    if (millis > 60000) {
        result += " " + Math.floor(millis/60000) + " min";
        millis = millis%60000;
    }
    if (millis > 1000) {
        result += " " + Math.floor(millis/1000) + " sec";
        millis = millis%1000;
    }
    if (millis > 0) {
        result += " " + millis + " ms"
    }
    return result;
}
