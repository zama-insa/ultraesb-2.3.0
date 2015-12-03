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

function startSequence(id, state) {
    if (state == 'Starting' || state=='Started') {
        showWarning('Cannot start the Sequence: ' + id + ', is either started or starting.');
    } else {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'sequences/startSequence/' + id ,
            success: function(msg) {
                showInfo('Successfully started the Sequence: ' + id + '.' , window.location.href);
            }
        });
    }
}

function endSequence(id, state) {
    if (state == 'Stopped' || state=='Stopping') {
        showWarning('Cannot stop the Sequence: ' + id + ', is either stopped or stopping.');
    } else {
        showConfirmation('Do you want to stop the Sequence: ' + id + '?' , function() {
            $.ajax({
                type: 'POST',
                url: getCallURLBase() + 'sequences/stopSequence/' + id ,
                success: function(msg) {
                    showInfo('Successfully stopped the Sequence: ' + id + '.', window.location.href);
                }
            });
        });
    }
}

function enableDebugSequence(id, debug) {
    if (!debug) {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'sequences/enableDebugSequence/' + id ,
            success: function(msg) {
                showInfo('Debugging enabled for Sequence: ' + id + '.', window.location.href);
            }
        });
    } else {
        showWarning('Debugging is already enabled for Sequence: ' + id);
    }
}

function disableDebugSequence(id, debug) {
    if (debug) {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'sequences/disableDebugSequence/' + id ,
            success: function(msg) {
                showInfo('Debugging disabled for Sequence: ' + id + '.', window.location.href);
            }
        });
    } else {
        showWarning('Debugging is already disabled for Sequence: ' + id);
    }
}

function resetStatisticsSequence(id) {
    $.ajax({
        type: 'POST',
        url: getCallURLBase() + 'sequences/resetStatisticsSequence/' + id ,
        success: function(msg) {
            showInfo('Statistics of Sequence: ' + id + ' successfully reset.', window.location.href);
        }
    });
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