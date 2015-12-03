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

function startProxyService(id,state) {

    if (state == 'Starting' ||state == 'Started') {
        showWarning('Cannot start proxy service: ' + id + ', is either started or starting.');
    } else {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'proxyservices/startProxyService/' +id ,
            success: function(msg) {
                showInfo('Successfully started the Proxy Service: ' + id + '.' , window.location.href);
            },
            error: function(msg) {
                showInfo('Failed to start the Proxy Service: ' + id + '.' , window.location.href);
            }
        });
    }
}

function stopProxyService(id,state) {

    if (state == 'Stopping' || state == 'Stopped' || state == 'Paused') {
        showWarning('Cannot stop the proxy service: ' + id + ', is either stopped or stopping.');
    }
    else if (state == 'Failed') {
        showWarning('Cannot stop the proxy service: ' + id + '. It is not currently running.');
    } else {
        showConfirmation('Do you want to stop the Proxy Service ' + id + '?' , function() {
            $.ajax({
                type: 'POST',
                url: getCallURLBase() + 'proxyservices/stopProxyService/' +id ,
                success: function(msg) {
                    showInfo('Successfully stopped the Proxy Service: ' + id + '.' , window.location.href);
                }
            });
        });
    }
}

function enableDebugProxyService(id,debug) {

    if (!debug) {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'proxyservices/enableProxyServiceDebug/' +id ,
            success: function(msg) {
                showInfo('Debug successfully enabled for: ' + id + '.' , window.location.href);
            }
        });
    } else {
        showWarning('Debug enabled already!');
    }
}

function disableDebugProxyService(id,debug) {
    if (debug) {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'proxyservices/disableProxyServiceDebug/' +id ,
            success: function(msg) {
                showInfo('Debug successfully disabled for: ' + id + '.' ,  window.location.href);
            }
        });
    } else {
        showWarning('Debug disabled already!');
    }
}

function displayDebugIcon(id, debug, state) {
    if (state == 'Failed' || state == 'Warning' ) {
        $('img#' + id + '-disDebug').hide();
        $('img#' + id + '-enDebug').hide();
    } else {
        if(debug) {
            $('img#' + id + '-enDebug').hide();
            $('img#' + id + '-disDebug').show();
        } else {
            $('img#' + id + '-disDebug').hide();
            $('img#' + id + '-enDebug').show();
        }
    }
}

function displayIcon(id, state) {
    if (state=='Started') {
        $('img#' + id + '-start').hide();
        $('img#' + id + '-stop').show();
    }
    if (state == 'Stopped') {
        $('img#' + id + '-start').show();
        $('img#' + id + '-stop').hide();
    }
    if (state == 'Failed' || state == 'Warning') {
        $('img#' + id + '-start').show();
        $('img#' + id + '-stop').hide();
    }
}