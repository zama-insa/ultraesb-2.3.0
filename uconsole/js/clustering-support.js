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

function reTryCommand(commandId, nodeName) {
    showConfirmation("Do you want to retry the command " + commandId + " on node " + nodeName + "?", function() {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'cluster/retryCommand/' + commandId + "/" + nodeName,
            dataType: 'text',
            success: function(msg) {
                if (msg=="true") {
                    showInfo("Retry of the command " + commandId + " on node " + nodeName + " was successful.", window.location.href);
                } else {
                    showError("Retry of the command " + commandId + " on node " + nodeName + " failed!");
                }
            }
        }).error(function() {
            showError("Retry of the command " + commandId + " on node " + nodeName + " failed!");
        });
    });
}

function archiveCommand(commandId) {
    showConfirmation("Do you want to archive the command with id : " + commandId + "?", function() {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'cluster/archiveCommand/' + commandId,
            success: function(msg) {
                showInfo('Successfully archived the command with id : ' + commandId + '.', window.location.href);
            }
        });
    });
}

function rePublishCommand(commandId) {
    showConfirmation("Do you want to republish the command with id : " + commandId + "?", function() {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'cluster/republishCommand/' + commandId,
            dataType: 'text',
            success: function(msg) {
                showInfo('Successfully republished the command with id : ' + commandId + ' into a new command : ' + msg + '.', 'index.html?pageName=cluster-mgt/command-details.html&commandId=' + msg);
            }
        });
    });
}