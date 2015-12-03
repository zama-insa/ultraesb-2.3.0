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
 * @author Ruwan
 */
function donutGraph(divClass, w, h, stream) {
    var radius = Math.min(w, h) / 2;

    var color = d3.scale
        .category20b();
//        .ordinal()
//        .range(["#E6E2AF", "#A7A37E", "#EFECCA", "#046380", "#002F2F"]);

    var arc = d3.svg.arc()
        .outerRadius(radius - 10)
        .innerRadius(radius - 40);

    var pie = d3.layout.pie()
        .sort(null)
        .value(function(d) { return d.relativeAverage; });

    var svg = d3.select("div ." + divClass).append("svg")
        .attr("width", w)
        .attr("height", h)
        .append("g")
        .attr("transform", "translate(" + w / 2 + "," + h / 2 + ")");

    d3.json(getCallURLBase() + "metrics/retrieveAllTimeEvents/" + stream, function(error, raw) {
        var data = [];
        var i = 0;
        $.each(raw.events, function(t) {
            raw.events[t].i = i;
            data[i++] = raw.events[t];
        });

        data.sort(function(a, b) { return b.relativeAverage - a.relativeAverage; });

//        var min = d3.min(data, function(d) { return d.relativeAverage; });
//        data.forEach(function (d, i) {
//            d.relativeAverage = (d.relativeAverage - min)/1000000;
//        });

//        data.forEach(function(d) {
//            d.population = +d.population;
//        });

        var g = svg.selectAll(".arc")
            .data(pie(data))
            .enter().append("g")
            .attr("class", "arc");

        g.append("path")
            .attr("d", arc)
            .style("fill", function(d) { return color(d.data.i); });

        g.append("text")
            .attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
            .attr("dy", ".35em")
            .style("text-anchor", "middle")
            .text(function(d) { return d.data.event; });

    });
}
