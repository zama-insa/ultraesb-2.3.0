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

/**
 * Zabbix monitoring template management and registration related functions
 * @author Ruwan
 */

function editTemplate(templateKey) {
    window.location.href = 'index.html?pageName=monitoring/template-details.html&templateKey=' + templateKey + '&mode=edit&bc=3';
}

function removeTemplate(templateKey) {
    showConfirmation("Do you want to remove the template with key : " + templateKey, function() {
        $.ajax({
            type: 'DELETE',
            url: getCallURLBase() + 'uz/templates/' + templateKey,
            success: function(msg) {
                showInfo("Successfully removed the template with key : " + templateKey, 'index.html?pageName=monitoring/uz-template-registry.html&bc=2');
            }
        });
    });
}

function enableTemplate(templateKey) {
    showConfirmation("Do you want to enable the template with key : " + templateKey, function() {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'uz/templates/' + templateKey + '/enable',
            success: function(msg) {
                showInfo("Successfully enabled the template with key : " + templateKey, window.location.href);
            }
        });
    });
}

function disableTemplate(templateKey) {
    showConfirmation("Do you want to disable the template with key : " + templateKey, function() {
        $.ajax({
            type: 'POST',
            url: getCallURLBase() + 'uz/templates/' + templateKey + '/disable',
            success: function(msg) {
                showInfo("Successfully disabled the template with key : " + templateKey, window.location.href);
            }
        });
    });
}
