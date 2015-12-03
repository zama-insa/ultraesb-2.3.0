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

function startEndpoint(state,id) {

    if (state == 'Starting' ||state == 'Started') {
        showWarning('Cannot start the Endpoint: ' + id + ', is either started or starting.');
    } else {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'endpoints/startEndpoint/' +id ,
            success: function(msg) {
                showInfo('Successfully started the Endpoint: ' + id + '.', window.location.href);
            }
        });
    }
}

function stopEndpoint(state,id) {

    if (state == 'Stopping' || state == 'Stopped') {
        showWarning('Cannot stop the Endpoint: ' + id + ', is either stopped or stopping.');
    } else {
        showConfirmation('Do you want to stop the Endpoint?', function() {
            $.ajax({
                type: 'POST',
                url: getCallURLBase() + 'endpoints/stopEndpoint/' +id ,
                success: function(msg) {
                    showInfo('Successfully stopped the Endpoint: ' + id + '.', window.location.href);
                }
            });
        });
    }
}

function enableDebug(id,debug) {

    if (!debug) {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'endpoints/enableEndpointDebug/' +id ,
            success: function(msg) {
                showInfo('Debug enabled successfully for the Endpoint : ' + id + '.', window.location.href);
            }
        });
    } else {
        showWarning('Debug already enabled for endpoint ' + id + '!');
    }
}

function disableDebug(id,debug) {

    if (debug) {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'endpoints/disableEndpointDebug/' +id ,
            success: function(msg) {
                showInfo('Debug disabled successfully for the Endpoint : ' + id + '.', window.location.href);
            }
        });
    } else {
        showWarning('Debug already disabled for endpoint ' + id + '!');
    }
}

function editEndpoint(id) {
    loadPage('index.html?pageName=node-mgt/endpoint-details.html&mode=edit&epId=' + id);
}

function startAddress(epId, id, state){
    if (state == 'active') {
        showWarning('Address ' + id + ' already started!');
    }
    else {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'endpoints/startAddress/' + epId + '/' + id,
            success: function(msg) {
                showInfo('Successfully started the Address: ' + id + ' of the endpoint ' + epId, window.location.href);
            }
        });
    }
}

function stopAddress(epId, id, state){
    if (state == 'off') {
        showWarning('Address: ' + id + ' already stopped!');
    }
    else {
        showConfirmation('Do you want to stop the address? ',function() {
            $.ajax({
                type: 'POST',
                url: getCallURLBase() + 'endpoints/stopAddress/' + epId + '/' + id ,
                success: function(msg) {
                    showInfo('Successfully stopped the Address: ' + id + ' of the endpoint ' + epId, window.location.href);
                }
            });
        });
    }
}

function editAddress(id1, id2) {
    loadPage('index.html?pageName=node-mgt/address.html&mode=edit&epId=' + id1 + '&type=' + type + '&addrId=' + id2);
}

function addAddress(id) {
    if (type == 'single') {
        showWarning('Cannot add a new address, address type - single.');
    }else{
        loadPage('index.html?pageName=node-mgt/address.html&mode=add&epId=' + id + '&type=' + type);
    }
}

function deleteAddress(epID, addrID, type, addrCount) {

    if (addrCount > 1) {
        if (type == 'single') {
            showWarning('Cannot Delete the Address: ' + addrID + ', address type-single.');

        } else {
            showConfirmation('Do you want to delete the address ' + addrID + ' of endpoint ' + epID + '?' , function() {
                $.ajax({
                    type: 'DELETE',
                    url: getCallURLBase() + 'endpoints/deleteAddress/' + epID + '/' + addrID ,
                    success: function(msg) {
                        showInfo('Successfully deleted the Address: ' + addrID + ' of endpoint ' + epID + '.', 'index.html?pageName=node-mgt/endpoint-details.html&mode=view&epId=' + epID);
                    }
                });
            });
        }
    } else {
         showWarning('Cannot Delete the Address: ' + addrID + ', the only existing address for ' + epID);
    }
}

function displayDebugIcon(id, debug) {
    if(debug) {
        $('img#' + id + '-enDebug').hide();
        $('img#' + id + '-disDebug').show();
    } else {
        $('img#' + id + '-disDebug').hide();
        $('img#' + id + '-enDebug').show();
    }
}

function displayIcon(id, state) {
    if(state=='Started') {
        $('img#' + id + '-start').hide();
        $('img#' + id + '-stop').show();
    }
    if(state == 'Stopped') {
        $('img#' + id + '-start').show();
        $('img#' + id + '-stop').hide();
    }
}