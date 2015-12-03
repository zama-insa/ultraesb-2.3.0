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

function stackedHorizontalBar(divClass, stream, events, w, h, xAxisName) {

    var margin = {top: 120, right: 20, bottom: 35, left: 30};
    d3.json(getCallURLBase() + "metrics/retrieveAllTimeEvents/" + stream, function(error, raw) {
        var data = [];
        var i = 0;
        $.each(raw.events, function(t) {
            data[i++] = raw.events[t];
        });

        data.sort(function(a, b) { return b.relativeAverage - a.relativeAverage; });

        var min = d3.min(data, function(d) { return d.relativeAverage; });
        data.forEach(function (d, i) {
            d.relativeAverage = (d.relativeAverage - min)/1000000;
        });

        var x = d3.scale.linear()
            .domain([0, d3.max(data, function(d, i) {return d.relativeAverage})])
            .range([0, w]);
        var xAxis = d3.svg.axis()
            .scale(x)
            .orient("bottom");

        var svg = d3.select("div ." + divClass).append("svg")
            .attr("class", "chart")
            .attr("width", w + margin.right + margin.left)
            .attr("height", h + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        var color = d3.scale
            .category20b();
//        .ordinal()
//        .range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);
        svg.selectAll("rect")
            .data(data)
            .enter().append("rect")
            .attr("y", 20)
            .attr("fill", function(d, i) {return color(i);})
            .attr("width", function(d, i) {return x(d.relativeAverage);})
            .attr("height", 30);

        svg.selectAll("text")
            .data(data)
            .enter().append("text")
//            .attr("transform", "rotate(-90)")
            .attr("y", function(d, i) {return i*(-10);})
            .attr("x", function(d, i) {return x(d.relativeAverage);})
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text(function(d, i) {return d.event; });

        var timeAxis = svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + h + ")")
            .call(xAxis);
        timeAxis.append("text")
            .attr("y", 20)
            .attr("x", w)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text(xAxisName);

    });
}