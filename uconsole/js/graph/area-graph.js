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
 * Draws a line graph using d3js with the specified parameters
 *
 * @param divClass the class name of the div on which the graph will be drawn
 * @param stream the metrics stream name to plot
 * @param w width of the graph
 * @param h height of the graph
 * @param yAxisName text to be printed in the y axis
 * @param xAxisName text to be printed on the x axis
 * @param dataTransform a function of the following form -
 *              function(d) {
 *                  d.mean = d.mean/1000000
 *              }
 * @param xAxisData function to retrieve x axis data points of the following form
 *              function(d) {
 *                  return d.timestamp
 *              }
 * @param yAxisData function to retrieve y axis data points of the following form
 *              function(d) {
 *                  return d.mean
 *              }
 * @param refreshInterval the graph refresh interval
 * @param defaultMax the minimum y axis range if it doesn't exceed this value
 * @param xTicks number of time axis ticks
 */
function dynamicAreaGraph(divClass, stream, w, h, yAxisName, xAxisName, dataTransform, xAxisData, yAxisData, refreshInterval, defaultMax, xTicks) {

    var steps = Number($.cookie("metricsSteps"));

    var margin = {top: 20, right: 20, bottom: 30, left: 50};
    var x = d3.time.scale()
        .range([0, w]);
    var y = d3.scale.linear()
        .range([h, 0]);

    var xAxis = d3.svg.axis()
        .scale(x)
        .orient("bottom").ticks(xTicks != null ? xTicks : 5);
    var xAxisGrid = d3.svg.axis()
        .scale(x)
        .orient("bottom");
    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left").ticks(5);
    var yAxisGrid = d3.svg.axis()
        .scale(y)
        .orient("left");

    var area = d3.svg.area()
//        .interpolate("linear")
        .x(function(d) { return x(d.timestamp); })
        .y0(h)
        .y1(function(d) { return y(yAxisData != null ? yAxisData(d) : d.mean); });

    var svg = d3.select("div ." + divClass).append("svg")
        .attr("width", w + margin.left + margin.right)
        .attr("height", h + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    d3.select("svg").append("linearGradient")
        .attr("id", "area-gradient")
        .attr("gradientUnits", "userSpaceOnUse")
        .attr("x1", 0).attr("y1", y(1))
        .attr("x2", 0).attr("y2", y(-1))
        .selectAll("stop")
        .data([
            {offset: "0%", color: "steelblue"},
//            {offset: "50%", color: "white"},
            {offset: "65%", color: "white"}
        ])
        .enter().append("stop")
        .attr("offset", function(d) { return d.offset; })
        .attr("stop-color", function(d) { return d.color; });

    var nextCallTime = 0;

    d3.json(getCallURLBase() + "metrics/retrieveGauge/" + stream, function(error, data) {
        data.reverse();
        data.forEach(function(d) {
            if (dataTransform != null) {
                dataTransform(d);
            }
            if (d.timestamp > nextCallTime) {
                nextCallTime = d.timestamp;
            }
        });

        x.domain(d3.extent(data, xAxisData != null ? xAxisData : function(d) { return d.timestamp; }));
        var ymax = d3.max(data, yAxisData != null ? yAxisData : function(d) { return d.mean; });
        y.domain([0, ymax > defaultMax ? ymax : defaultMax]);

        svg.append("defs").append("clipPath")
            .attr("id", "clip")
            .append("rect")
            .attr("width", w)
            .attr("height", h);

        var timeAxis = svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + h + ")")
            .call(xAxis);

        var timeGrid = svg.append("g")
            .attr("class", "grid")
            .attr("transform", "translate(0," + h + ")")
            .call(xAxisGrid
                .tickSize(-h, 0, 0)
                .tickFormat("")
            );

        var memAxis = svg.append("g")
            .attr("class", "y axis")
            .call(yAxis);
        var memGrid = svg.append("g")
            .attr("class", "grid")
            .call(yAxisGrid
                .tickSize(-w, 0, 0)
                .tickFormat("")
            );
        memAxis.append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 5)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text(yAxisName);

        var path = svg.append("g")
            .attr("clip-path", "url(#clip)")
            .append("path")
            .data([data])
//            .attr("stroke", "steelblue")
            .attr("fill", "url(#area-gradient)")
            .attr("class", "area")
            .attr("d", area);

        path.on("mouseover", function(d, i, t) {alert("Draw a tooltip with data-set " + t + " :: " + xAxisData(data[i]) + ", "+ yAxisData(data[i]));});

        setInterval(function() {
            d3.json(getCallURLBase() + "metrics/retrieveGauge/" + stream + "/after/" + nextCallTime, function(error, data2) {
                data2.reverse();
                data2.forEach(function(d) {
                    if (dataTransform != null) {
                        dataTransform(d);
                    }
                    if (d.timestamp > nextCallTime) {
                        nextCallTime = d.timestamp;
                    }
                    data.push(d);
                    x.domain(d3.extent(data, xAxisData != null ? xAxisData : function(d) { return d.timestamp; }));
                    ymax = d3.max(data, yAxisData != null ? yAxisData : function(d) { return d.mean; });
                    y.domain([0, ymax > defaultMax ? ymax : defaultMax]);
//                    y.domain([0, d3.max(data, yAxisData != null ? yAxisData : function(d) { return d.mean; })]);
                    path.transition()
                        .duration(750)
                        .ease("linear")
                        .attr("d", area);
                    timeAxis.transition()
                        .duration(750)
                        .ease("linear")
                        .call(xAxis);
                    timeGrid.transition()
                        .duration(750)
                        .ease("linear")
                        .call(xAxisGrid);
                    memAxis.transition().ease("linear").call(yAxis);
                    memGrid.transition().ease("linear").call(yAxisGrid);
                    if (data.length > steps) {
                        for (var k = 0; k < data.length - steps; k++) {
                            data.shift();
                        }
                    }
                });

            });
        }, refreshInterval);
    });
}

/**
 * Draws a line graph using d3js with the specified parameters
 *
 * @param divClass the class name of the div on which the graph will be drawn
 * @param stream the metrics stream name to plot
 * @param w width of the graph
 * @param h height of the graph
 * @param yAxisName text to be printed in the y axis
 * @param xAxisName text to be printed on the x axis
 * @param dataTransform a function of the following form -
 *              function(d) {
 *                  d.average = d.mean/1000000
 *              }
 * @param xAxisData function to retrieve x axis data points of the following form
 *              function(d) {
 *                  return d.timestamp
 *              }
 * @param yAxisData function to retrieve y axis data points of the following form
 *              function(d) {
 *                  return d.mean
 *              }
 * @param refreshInterval the graph refresh interval
 * @param defaultMax the minimum y axis range if it doesn't exceed this value
 * @param xTicks number of time axis ticks
 */
function dynamicAreaGraphCluster(divClass, stream, w, h, yAxisName, xAxisName, dataTransform, xAxisData, yAxisData, refreshInterval, defaultMax, xTicks) {

    var steps = Number($.cookie("metricsSteps"));

    var margin = {top: 20, right: 20, bottom: 30, left: 50};
    var x = d3.time.scale()
        .range([0, w]);
    var y = d3.scale.linear()
        .range([h, 0]);

    var xAxis = d3.svg.axis()
        .scale(x)
        .orient("bottom");
    var xAxisGrid = d3.svg.axis()
        .scale(x)
        .orient("bottom").ticks(xTicks != null ? xTicks : 5);;
    var yAxis = d3.svg.axis()
        .scale(y)
        .orient("left").ticks(5);
    var yAxisGrid = d3.svg.axis()
        .scale(y)
        .orient("left");

    var area = d3.svg.area()
        .interpolate("linear")
        .x(function(d) { return x(d.timestamp); })
        .y0(h)
        .y1(function(d) { return y(yAxisData != null ? yAxisData(d) : d.mean); });

    var svg = d3.select("div ." + divClass).append("svg")
        .attr("width", w + margin.left + margin.right)
        .attr("height", h + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var nextCallTime = 0;

    d3.json(getCallURLBase() + "metrics/retrieveClusterGauge/" + stream, function(error, allData) {
        var completeData = [];
        for (var node in allData) {
            allData[node].reverse();
            allData[node].forEach(function(d) {
                if (dataTransform != null) {
                    dataTransform(d);
                }
                if (d.timestamp > nextCallTime) {
                    nextCallTime = d.timestamp;
                }
            });
            $.merge(completeData, allData[node]);
        }

        x.domain(d3.extent(completeData, xAxisData != null ? xAxisData : function(d) { return d.timestamp; }));
        var ymax = d3.max(completeData, yAxisData != null ? yAxisData : function(d) { return d.mean; });
        y.domain([0, ymax > defaultMax ? ymax : defaultMax ]);

        svg.append("defs").append("clipPath")
            .attr("id", "clip")
            .append("rect")
            .attr("width", w)
            .attr("height", h);

        var timeAxis = svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + h + ")")
            .call(xAxis);

        var timeGrid = svg.append("g")
            .attr("class", "grid")
            .attr("transform", "translate(0," + h + ")")
            .call(xAxisGrid
                .tickSize(-h, 0, 0)
                .tickFormat("")
            );

        var memAxis = svg.append("g")
            .attr("class", "y axis")
            .call(yAxis);
        var memGrid = svg.append("g")
            .attr("class", "grid")
            .call(yAxisGrid
                .tickSize(-w, 0, 0)
                .tickFormat("")
            );
        memAxis.append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 5)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text(yAxisName);

        var color = d3.scale
            .category10();
        var paths = [];
        var i = 0;
        for (node in allData) {
            paths[node] = svg.append("g")
                .attr("clip-path", "url(#clip)")
                .append("path")
                .data([allData[node]])
                .attr("stroke", color(i++))
                .attr("fill", color(i++))
                .attr("class", "area")
                .attr("d", area);

            paths[node].on("mouseover", function(d, i, t) {alert("Draw a tooltip with data-set " + t + " :: " + xAxisData(data[i]) + ", "+ yAxisData(data[i]));});

        }

        setInterval(function() {
            d3.json(getCallURLBase() + "metrics/retrieveClusterGauge/" + stream + "/after/" + nextCallTime, function(error, allData2) {
                for (node in allData2) {
                    allData2[node].reverse();
                    allData2[node].forEach(function(d) {
                        if (dataTransform != null) {
                            dataTransform(d);
                        }
                        if (d.timestamp > nextCallTime) {
                            nextCallTime = d.timestamp;
                        }
                        allData[node].push(d);
                        completeData.push(d);
                        x.domain(d3.extent(completeData, xAxisData != null ? xAxisData : function(d) { return d.timestamp; }));
                        ymax = d3.max(completeData, yAxisData != null ? yAxisData : function(d) { return d.mean; });
                        y.domain([0, ymax > defaultMax ? ymax : defaultMax]);
//                    y.domain([0, d3.max(data, yAxisData != null ? yAxisData : function(d) { return d.mean; })]);
                        paths[node].transition()
                            .duration(750)
                            .ease("linear")
                            .attr("d", area);
                        timeAxis.transition()
                            .duration(750)
                            .ease("linear")
                            .call(xAxis);
                        timeGrid.transition()
                            .duration(750)
                            .ease("linear")
                            .call(xAxisGrid);
                        memAxis.transition().ease("linear").call(yAxis);
                        memGrid.transition().ease("linear").call(yAxisGrid);
                        if (allData[node].length > steps) {
                            for (var k = 0; k < allData[node].length - steps; k++) {
                                allData[node].shift();
                            }
                        }
                    });
                }

            });
        }, refreshInterval);
    });
}