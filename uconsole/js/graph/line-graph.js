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
function dynamicClusterMultiGraph(graphs, refreshInterval) {

    $('div.stats div').hide();
    $('div.stats p span.right').hide();
    $('div.stats p span.stat').hide();

    var steps = Number($.cookie("metricsSteps"));
    var runtimeGraphs = {};
    var nextCallTime = 0;
    var gaugeStreamString = ",";
    var counterStreamString = ",";
    var timerStreamString = ",";
    var color = d3.scale.category10();
    var nodeColors = {};

    var margin = {top: 20, right: 20, bottom: 30, left: 50};

    for (var i in graphs) {

        runtimeGraphs[i] = {};
        runtimeGraphs[i].x = d3.time.scale().range([0, graphs[i].w]);
        runtimeGraphs[i].y = d3.scale.linear().range([graphs[i].h, 0]);

        runtimeGraphs[i].xAxis = d3.svg.axis().scale(runtimeGraphs[i].x).orient("bottom").ticks(graphs[i].xTicks != null ? graphs[i].xTicks : 5);
        runtimeGraphs[i].xAxisGrid = d3.svg.axis().scale(runtimeGraphs[i].x).orient("bottom").ticks(graphs[i].xTicks != null ? graphs[i].xTicks : 10);

        runtimeGraphs[i].yAxis = d3.svg.axis().scale(runtimeGraphs[i].y).orient("left").ticks(5);
        runtimeGraphs[i].yAxisGrid = d3.svg.axis().scale(runtimeGraphs[i].y).orient("left");

        runtimeGraphs[i].line = d3.svg.line().interpolate("linear")
            .x(function(d) { return runtimeGraphs[i].x(d.timestamp); })
            .y(function(d) { return runtimeGraphs[i].y(graphs[i].yAxisData != null ? graphs[i].yAxisData(d) : d.mean); });
        runtimeGraphs[i].svg = d3.select("div ." + graphs[i].divClass).append("svg")
            .attr("width", graphs[i].w + margin.left + margin.right)
            .attr("height", graphs[i].h + margin.top + margin.bottom)
            .attr("class", "graph")
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        if (graphs[i].type == "gauge") {
            gaugeStreamString += i + ",";
        } else if (graphs[i].type == "counter") {
            counterStreamString += i + ",";
        } else if (graphs[i].type == "timer") {
            timerStreamString += i + ",";
        }
    }

    blockUIActive = false;
    $.getJSON(getCallURLBase() + "metrics/retrieveClusterMultiGauges/" + gaugeStreamString + "/" + counterStreamString + "/" + timerStreamString, function(multiData) {
        for (var stream in multiData) {
            runtimeGraphs[stream].completeData = [];
            for (var node in multiData[stream]) {
//                multiData[stream][node].reverse();
                var dAll = multiData[stream][node].splice(0,1)[0];
                multiData[stream][node].forEach(function(d) {
                    if (graphs[stream].dataTransform != null && graphs[stream].dataTransform != undefined) {
                        graphs[stream].dataTransform(d);
                    }
                    if (d.timestamp > nextCallTime) {
                        nextCallTime = d.timestamp;
                    }
                });
                $.merge(runtimeGraphs[stream].completeData, multiData[stream][node]);
            }

            runtimeGraphs[stream].x.domain(d3.extent(runtimeGraphs[stream].completeData, graphs[stream].xAxisData != null ? graphs[stream].xAxisData : function(d) { return d.timestamp; }));
            runtimeGraphs[stream].ymax = d3.max(runtimeGraphs[stream].completeData, graphs[stream].yAxisData != null ? graphs[stream].yAxisData : function(d) { return d.mean; });
            runtimeGraphs[stream].y.domain([0, runtimeGraphs[stream].ymax > graphs[stream].defaultMax ? runtimeGraphs[stream].ymax : graphs[stream].defaultMax ]);
            runtimeGraphs[stream].svg.append("defs").append("clipPath")
                .attr("id", "clip")
                .append("rect")
                .attr("width", graphs[stream].w)
                .attr("height", graphs[stream].h);
            runtimeGraphs[stream].timeAxis = runtimeGraphs[stream].svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + graphs[stream].h + ")")
                .call(runtimeGraphs[stream].xAxis);
            runtimeGraphs[stream].timeGrid = runtimeGraphs[stream].svg.append("g")
                .attr("class", "grid")
                .attr("transform", "translate(0," + graphs[stream].h + ")")
                .call(runtimeGraphs[stream].xAxisGrid
                    .tickSize(-graphs[stream].h, 0, 0)
                    .tickFormat("")
                );
            runtimeGraphs[stream].memAxis = runtimeGraphs[stream].svg.append("g")
                .attr("class", "y axis")
                .call(runtimeGraphs[stream].yAxis);
            runtimeGraphs[stream].memGrid = runtimeGraphs[stream].svg.append("g")
                .attr("class", "grid")
                .call(runtimeGraphs[stream].yAxisGrid
                    .tickSize(-graphs[stream].w, 0, 0)
                    .tickFormat("")
                );
            runtimeGraphs[stream].memAxis.append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 5)
                .attr("dy", ".71em")
                .style("text-anchor", "end")
                .text(graphs[stream].yAxisName);
            runtimeGraphs[stream].paths = {};
        }

        for (var stream in multiData) {

            var colorIndex = 1;
            for (var node in multiData[stream]) {
                if (nodeColors[node] == null || nodeColors[node] == undefined) {
                    nodeColors[node] = color(colorIndex++);
                }
                runtimeGraphs[stream].paths[node] = runtimeGraphs[stream].svg.append("g")
                    .attr("clip-path", "url(#clip)")
                    .append("path")
                    .data([multiData[stream][node]])
                    .attr("stroke", nodeColors[node])
                    .attr("class", "line")
                    .attr("d", d3.svg.line().interpolate("linear")
                        .x(function(d) { return runtimeGraphs[stream].x(d.timestamp); })
                        .y(function(d) { return runtimeGraphs[stream].y(graphs[stream].yAxisData != null ? graphs[stream].yAxisData(d) : d.mean); }));

//                paths[node].on("mouseover", function(d, i, t) {alert("Draw a tooltip with data-set " + t + " :: " + xAxisData(data[i]) + ", "+ yAxisData(data[i]));});

            }
        }

        setInterval(function() {
            blockUIActive = false;
            $.getJSON(getCallURLBase() + "metrics/retrieveClusterMultiGauges/" + gaugeStreamString + "/" + counterStreamString + "/" + timerStreamString + "/after/" + nextCallTime, function(multiData2) {
                for (var stream in multiData2) {
                    runtimeGraphs[stream].completeData = [];
                    for (var node in multiData2[stream]) {
//                        multiData2[stream][node].reverse();
                        var dAll = multiData2[stream][node].splice(0,1)[0];
                        multiData2[stream][node].forEach(function(d) {
                            if (graphs[stream].dataTransform != null && graphs[stream].dataTransform != undefined) {
                                graphs[stream].dataTransform(d);
                            }
                            if (d.timestamp > nextCallTime) {
                                nextCallTime = d.timestamp;
                            }
                            multiData[stream][node].push(d);
                            if (multiData[stream][node].length > steps) {
                                var shifted = multiData[stream][node].shift();
                                var min = d3.min(multiData[stream][node], function(d) { return d.timestamp; });
                                if (shifted.timestamp > min) {
//                                    alert("problem found, shifted " + shifted.timestamp + " while min being " + min);
                                }
                            }
                        });
                        $.merge(runtimeGraphs[stream].completeData, multiData[stream][node]);
                    }

                    runtimeGraphs[stream].x.domain(d3.extent(runtimeGraphs[stream].completeData, graphs[stream].xAxisData != null ? graphs[stream].xAxisData : function(d) { return d.timestamp; }));
                    runtimeGraphs[stream].ymax = d3.max(runtimeGraphs[stream].completeData, graphs[stream].yAxisData != null ? graphs[stream].yAxisData : function(d) { return d.mean; });
                    runtimeGraphs[stream].y.domain([0, runtimeGraphs[stream].ymax > graphs[stream].defaultMax ? runtimeGraphs[stream].ymax : graphs[stream].defaultMax]);

                    for (node in multiData[stream]) {
                        runtimeGraphs[stream].paths[node]
                            .attr("d", d3.svg.line().interpolate("linear")
                                .x(function(d) { return runtimeGraphs[stream].x(d.timestamp); })
                                .y(function(d) { return runtimeGraphs[stream].y(graphs[stream].yAxisData != null ? graphs[stream].yAxisData(d) : d.mean); }))
                            .attr("transform", null)
                            .transition()
                            .duration(500)
                            .ease("linear");
                    }

                    runtimeGraphs[stream].timeAxis.transition()
                        .duration(500)
                        .ease("linear")
                        .call(runtimeGraphs[stream].xAxis);
                    runtimeGraphs[stream].timeGrid.transition()
                        .duration(500)
                        .ease("linear")
                        .call(runtimeGraphs[stream].xAxisGrid);
                    runtimeGraphs[stream].memAxis.transition()
                        .ease("linear")
                        .call(runtimeGraphs[stream].yAxis);
                    runtimeGraphs[stream].memGrid.transition()
                        .ease("linear")
                        .call(runtimeGraphs[stream].yAxisGrid);

                }
//                for (var stream in multiData) {
//                    for (node in multiData[stream]) {
//                        if (multiData[stream][node].length > steps) {
//                            for (var k = 0; k < multiData[stream][node].length - steps; k++) {
//                                multiData[stream][node].shift();
//                            }
//                        }
//                    }
//                }
            });
        }, refreshInterval);
    });

}

function dynamicMultiGraph(graphs, refreshInterval) {

    var steps = Number($.cookie("metricsSteps"));
    var runtimeGraphs = {};
    var nextCallTime = 0;
    var gaugeStreamString = ",";
    var counterStreamString = ",";
    var timerStreamString = ",";

    var margin = {top: 20, right: 20, bottom: 30, left: 50};

    for (var i in graphs) {

        runtimeGraphs[i] = {};
        runtimeGraphs[i].x = d3.time.scale().range([0, graphs[i].w]);
        runtimeGraphs[i].y = d3.scale.linear().range([graphs[i].h, 0]);

        runtimeGraphs[i].xAxis = d3.svg.axis().scale(runtimeGraphs[i].x).orient("bottom").ticks(5);
        runtimeGraphs[i].xAxisGrid = d3.svg.axis().scale(runtimeGraphs[i].x).orient("bottom").ticks(graphs[i].xTicks != null ? graphs[i].xTicks : 10);

        runtimeGraphs[i].yAxis = d3.svg.axis().scale(runtimeGraphs[i].y).orient("left").ticks(5);
        runtimeGraphs[i].yAxisGrid = d3.svg.axis().scale(runtimeGraphs[i].y).orient("left");

        runtimeGraphs[i].line = d3.svg.line().interpolate("linear")
            .x(function(d) { return runtimeGraphs[i].x(d.timestamp); })
            .y(function(d) { return runtimeGraphs[i].y(graphs[i].yAxisData != null ? graphs[i].yAxisData(d) : d.mean); });
        runtimeGraphs[i].svg = d3.select("div ." + graphs[i].divClass).append("svg")
            .attr("width", graphs[i].w + margin.left + margin.right)
            .attr("height", graphs[i].h + margin.top + margin.bottom)
            .attr("class", "graph")
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        if (graphs[i].type == "gauge") {
            gaugeStreamString += i + ",";
        } else if (graphs[i].type == "counter") {
            counterStreamString += i + ",";
        } else if (graphs[i].type == "timer") {
            timerStreamString += i + ",";
        }
    }

    blockUIActive = false;
    $.getJSON(getCallURLBase() + "metrics/retrieveMultiGauges/" + gaugeStreamString + "/" + counterStreamString + "/" + timerStreamString, function(multiData) {
        for (var stream in multiData) {
//            multiData[stream].reverse();
            var dAll = multiData[stream].splice(0, 1)[0];
            if (graphs[stream].drawCallback != null) {
                graphs[stream].drawCallback(dAll);
            }
            if (graphs[stream].stats != null) {
                if (graphs[stream].allDataTransform != null && graphs[stream].allDataTransform != undefined) {
                    graphs[stream].allDataTransform(dAll);
                }
                var average = graphs[stream].stats.average;
                if (average != null) {
                    $('#' + average).html(formatDecimalNumber(dAll.mean));
                }
                var min = graphs[stream].stats.min;
                if (min != null) {
                    $('#' + min).html(formatDecimalNumber(dAll.min));
                }
                var max = graphs[stream].stats.max;
                if (max != null) {
                    $('#' + max).html(formatDecimalNumber(dAll.max));
                }
                var count = graphs[stream].stats.count;
                if (count != null) {
                    $('#' + count).html(formatNumber(dAll.count));
                }
                var sum = graphs[stream].stats.sum;
                if (sum != null) {
                    $('#' + sum).html(formatNumber(dAll.sum));
                }
                var rate = graphs[stream].stats.rate;
                if (rate != null) {
                    $('#' + rate).html(formatDecimalNumber(dAll.averageRate));
                }
                var median = graphs[stream].stats.median;
                if (median != null) {
                    $('#' + median).html(formatDecimalNumber4(dAll.median));
                }
                var percentValues = [75, 95, 98, 99, 999, 9999];
                for (var percent in percentValues) {
                    var percentKey = "_" + percentValues[percent] + "Percentile";
                    var percentile = graphs[stream].stats[percentKey];
                    if (percentile != null) {
                        $('#' + percentile).html(formatDecimalNumber4(dAll[percentKey]));
                    }
                }
            }
            multiData[stream].forEach(function(d) {
                if (graphs[stream].dataTransform != null && graphs[stream].dataTransform != undefined) {
                    graphs[stream].dataTransform(d);
                }
                if (d.timestamp > nextCallTime) {
                    nextCallTime = d.timestamp;
                }
            });

            runtimeGraphs[stream].x.domain(d3.extent(multiData[stream], graphs[stream].xAxisData != null ? graphs[stream].xAxisData : function(d) { return d.timestamp; }));
            runtimeGraphs[stream].ymax = d3.max(multiData[stream], graphs[stream].yAxisData != null ? graphs[stream].yAxisData : function(d) { return d.mean; });
            runtimeGraphs[stream].y.domain([0, runtimeGraphs[stream].ymax > graphs[stream].defaultMax ? runtimeGraphs[stream].ymax : graphs[stream].defaultMax ]);
            runtimeGraphs[stream].svg.append("defs").append("clipPath")
                .attr("id", "clip")
                .append("rect")
                .attr("width", graphs[stream].w)
                .attr("height", graphs[stream].h);
            runtimeGraphs[stream].timeAxis = runtimeGraphs[stream].svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + graphs[stream].h + ")")
                .call(runtimeGraphs[stream].xAxis);
            runtimeGraphs[stream].timeGrid = runtimeGraphs[stream].svg.append("g")
                .attr("class", "grid")
                .attr("transform", "translate(0," + graphs[stream].h + ")")
                .call(runtimeGraphs[stream].xAxisGrid
                    .tickSize(-graphs[stream].h, 0, 0)
                    .tickFormat("")
                );
            runtimeGraphs[stream].memAxis = runtimeGraphs[stream].svg.append("g")
                .attr("class", "y axis")
                .call(runtimeGraphs[stream].yAxis);
            runtimeGraphs[stream].memGrid = runtimeGraphs[stream].svg.append("g")
                .attr("class", "grid")
                .call(runtimeGraphs[stream].yAxisGrid
                    .tickSize(-graphs[stream].w, 0, 0)
                    .tickFormat("")
                );
            runtimeGraphs[stream].memAxis.append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 5)
                .attr("dy", ".71em")
                .style("text-anchor", "end")
                .text(graphs[stream].yAxisName);
            runtimeGraphs[stream].paths = {};
        }

        for (var stream in multiData) {

            runtimeGraphs[stream].path = runtimeGraphs[stream].svg.append("g")
                .attr("clip-path", "url(#clip)")
                .append("path")
                .data([multiData[stream]])
                .attr("stroke", graphs[stream].color != null ? graphs[stream].color : "steelblue")
                .attr("class", "line")
                .attr("d", d3.svg.line().interpolate("linear")
                    .x(function(d) { return runtimeGraphs[stream].x(d.timestamp); })
                    .y(function(d) { return runtimeGraphs[stream].y(graphs[stream].yAxisData != null ? graphs[stream].yAxisData(d) : d.mean); }));

//                paths[node].on("mouseover", function(d, i, t) {alert("Draw a tooltip with data-set " + t + " :: " + xAxisData(data[i]) + ", "+ yAxisData(data[i]));});

        }

        setInterval(function() {
            blockUIActive = false;
            $.getJSON(getCallURLBase() + "metrics/retrieveMultiGauges/" + gaugeStreamString + "/" + counterStreamString + "/" + timerStreamString + "/after/" + nextCallTime, function(multiData2) {
                for (var stream in multiData2) {
//                        multiData2[stream].reverse();
                    var dAll = multiData2[stream].splice(0, 1)[0];
                    if (graphs[stream].drawCallback != null) {
                        graphs[stream].drawCallback(dAll);
                    }
                    if (graphs[stream].stats != null) {
                        if (graphs[stream].allDataTransform != null && graphs[stream].allDataTransform != undefined) {
                            graphs[stream].allDataTransform(dAll);
                        }
                        var average = graphs[stream].stats.average;
                        if (average != null) {
                            $('#' + average).html(formatDecimalNumber(dAll.mean));
                        }
                        var min = graphs[stream].stats.min;
                        if (min != null) {
                            $('#' + min).html(formatDecimalNumber(dAll.min));
                        }
                        var max = graphs[stream].stats.max;
                        if (max != null) {
                            $('#' + max).html(formatDecimalNumber(dAll.max));
                        }
                        var count = graphs[stream].stats.count;
                        if (count != null) {
                            $('#' + count).html(formatNumber(dAll.count));
                        }
                        var sum = graphs[stream].stats.sum;
                        if (sum != null) {
                            $('#' + sum).html(formatNumber(dAll.sum));
                        }
                        var rate = graphs[stream].stats.rate;
                        if (rate != null) {
                            $('#' + rate).html(formatDecimalNumber(dAll.averageRate));
                        }
                        var median = graphs[stream].stats.median;
                        if (median != null) {
                            $('#' + median).html(formatDecimalNumber4(dAll.median));
                        }
                        var percentValues = [75, 95, 98, 99, 999, 9999];
                        for (var percent in percentValues) {
                            var percentKey = "_" + percentValues[percent] + "Percentile";
                            var percentile = graphs[stream].stats[percentKey];
                            if (percentile != null) {
                                $('#' + percentile).html(formatDecimalNumber4(dAll[percentKey]));
                            }
                        }
                    }
                        multiData2[stream].forEach(function(d) {
                            if (graphs[stream].dataTransform != null && graphs[stream].dataTransform != undefined) {
                                graphs[stream].dataTransform(d);
                            }
                            if (d.timestamp > nextCallTime) {
                                nextCallTime = d.timestamp;
                            }
                            multiData[stream].push(d);
                            if (multiData[stream].length > steps) {
                                var shifted = multiData[stream].shift();
                                var min = d3.min(multiData[stream], function(d) { return d.timestamp; });
                                if (shifted.timestamp > min) {
//                                    alert("problem found, shifted " + shifted.timestamp + " while min being " + min);
                                }
                            }
                        });

                    runtimeGraphs[stream].x.domain(d3.extent(multiData[stream], graphs[stream].xAxisData != null ? graphs[stream].xAxisData : function(d) { return d.timestamp; }));
                    runtimeGraphs[stream].ymax = d3.max(multiData[stream], graphs[stream].yAxisData != null ? graphs[stream].yAxisData : function(d) { return d.mean; });
                    runtimeGraphs[stream].y.domain([0, runtimeGraphs[stream].ymax > graphs[stream].defaultMax ? runtimeGraphs[stream].ymax : graphs[stream].defaultMax]);

                        runtimeGraphs[stream].path
                            .attr("d", d3.svg.line().interpolate("linear")
                                .x(function(d) { return runtimeGraphs[stream].x(d.timestamp); })
                                .y(function(d) { return runtimeGraphs[stream].y(graphs[stream].yAxisData != null ? graphs[stream].yAxisData(d) : d.mean); }))
                            .attr("transform", null)
                            .transition()
                            .duration(500)
                            .ease("linear");

                    runtimeGraphs[stream].timeAxis.transition()
                        .duration(500)
                        .ease("linear")
                        .call(runtimeGraphs[stream].xAxis);
                    runtimeGraphs[stream].timeGrid.transition()
                        .duration(500)
                        .ease("linear")
                        .call(runtimeGraphs[stream].xAxisGrid);
                    runtimeGraphs[stream].memAxis.transition()
                        .ease("linear")
                        .call(runtimeGraphs[stream].yAxis);
                    runtimeGraphs[stream].memGrid.transition()
                        .ease("linear")
                        .call(runtimeGraphs[stream].yAxisGrid);

                }
                for (var stream in multiData) {
//                    if (multiData[stream].length > 300) {
//                        alert(multiData[stream].length);
//                    }
//                    for (node in multiData[stream]) {
//                        if (multiData[stream][node].length > steps) {
//                            for (var k = 0; k < multiData[stream][node].length - steps; k++) {
//                                multiData[stream][node].shift();
//                            }
//                        }
//                    }
                }
            });
        }, refreshInterval);
    });

}