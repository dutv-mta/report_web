var startDateVal, endDateVal;

$(document).ready(function() {
    $(document).ajaxStart(function() {
        $("#auth-before-load").css("display", "block");
        $("#auth-after-load").css("opacity", "0.3");
    });
    $(document).ajaxStop(function() {
        $("#auth-before-load").css("display", "none");
        $("#auth-after-load").css("opacity", "1");
    });
    var start = moment().subtract(7, 'days');
    var end = moment().subtract(1, 'days');

    function cb(start, end) {
        $('#reportrange span').html(start.format('MMMM DD, YYYY') + ' - ' + end.format('MMMM DD, YYYY'));
        startDateVal = start.format('YYYYMMDD');
        endDateVal = end.format('YYYYMMDD')
        showChartOverview(start.format('YYYYMMDD'), end.format('YYYYMMDD'));
    }

    $('#reportrange').daterangepicker({
        startDate: start,
        endDate: end,
        maxDate: end,
        minDate: moment().subtract(60, 'days'),
        ranges: {
            'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            'Last 7 Days': [moment().subtract(7, 'days'), moment().subtract(1, 'days')],
            'Last 30 Days': [moment().subtract(30, 'days'), moment().subtract(1, 'days')],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        }
    }, cb);

    cb(start, end);
});

function showChartOverview(start, end) {
    var topicId = $("#report-topic-id").val();
    showChartSumMention(topicId, start, end);
    showChartByHour(topicId, start, end);
    showKeywordRank(topicId, start, end);
    showHeader(topicId, start, end);
    showListPost(topicId, start, end);
}

function showChartSumMention(topicId, start, end) {
    var url = window.location.origin + "/api/overview/" + topicId + "?start=" + start + "&end=" + end;
    $.getJSON(url, function(data) {
        var xDate = [],
            yCount = [];
        for (var i = 0; i < data.length; i++) {
            xDate.push(data[i].date);
            yCount.push(data[i].number);
        };
        Highcharts.chart('chart_01', {
            chart: {
                type: 'area'
            },
            accessibility: {
                description: ''
            },
            title: {
                text: ''
            },
            yAxis: {
                title: {
                    text: 'Số đề cập'
                },
                labels: {
                    formatter: function() {
                        return this.value / 1000 + 'k';
                    }
                }
            },
            tooltip: {
                pointFormat: '<b>{point.y:,.0f}</b>'
            },
            plotOptions: {
                area: {
                    marker: {
                        enabled: false,
                        symbol: 'circle',
                        radius: 2,
                        states: {
                            hover: {
                                enabled: true
                            }
                        }
                    }
                }
            },
            xAxis: {
                categories: xDate
            },
            series: [{
                name: "Đề cập",
                data: yCount
            }]
        });
    }).fail(function() {

    });
}

function showChartByHour(topicId, start, end) {
    var url = window.location.origin + "/api/overview/" + topicId + "/getDecapTheoGio?start=" + start + "&end=" + end;
    $.getJSON(url, function(data) {
        var xHour = [],
            yCount = [];
        for (var i = 0; i < data.length; i++) {
            xHour.push(data[i].date);
            yCount.push(data[i].number);
        };
        Highcharts.chart('chart_02', {
            chart: {
                type: 'area'
            },
            title: {
                text: ''
            },
            yAxis: {
                title: {
                    text: 'Số lượng'
                },
                labels: {
                    formatter: function() {
                        return this.value;
                    }
                }
            },
            tooltip: {
                split: true,
                valueSuffix: 'k'
            },
            plotOptions: {
                area: {
                    stacking: 'normal',
                    lineColor: '#666666',
                    lineWidth: 1,
                    marker: {
                        lineWidth: 1,
                        lineColor: '#666666'
                    }
                }
            },
            xAxis: {
                categories: xHour,
                tickmarkPlacement: 'on',
                title: {
                    enabled: false
                }
            },
            series: [{
                name: 'Khoảng giờ',
                data: yCount
            }]
        });
    }).fail(function() {

    });
}

function showKeywordRank(topicId, start, end) {
    var url = window.location.origin + "/api/overview/" + topicId + "/countKeyWord?start=" + start + "&end=" + end;
    $.getJSON(url, function(data) {
        var xName = [],
            yCount = [];
        for (var i = 0; i < data.length; i++) {
            xName.push(data[i].date);
            yCount.push(data[i].number);
        };
        Highcharts.chart('chart_03', {
            chart: {
                type: 'column'
            },
            title: {
                text: ''
            },
            subtitle: {
                text: ''
            },
            xAxis: {
                categories: xName,
                crosshair: true
            },
            yAxis: {
                min: 0,
                title: {
                    text: 'Số lượng'
                }
            },
            tooltip: {
                headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                    '<td style="padding:0"><b>{point.y}</b></td></tr>',
                footerFormat: '</table>',
                shared: true,
                useHTML: true
            },
            plotOptions: {
                column: {
                    pointPadding: 0.2,
                    borderWidth: 0
                }
            },
            series: [{
                name: 'Từ khóa',
                data: yCount
            }]
        });
    }).fail(function() {

    });
}

function showHeader(topicId, start, end){
    var url = window.location.origin + "/api/overview/"+topicId+"/getHeader?start=" + start + "&end=" + end;
    $.getJSON(url, function(data){
        $('#sum-mention').text(data.deCap);
        $('#sum-post').text(data.postCount);
        $('#sum-commnet').text(data.commentCount);
    })
}

function showListPost(topicId, start, end){
    var url = window.location.origin + "/api/overview/"+topicId+"/getListPost?start=" + start + "&end=" + end;
    $.getJSON(url, function(data){
        var template = $("#list-template-post").html();
        var str = "";
        $("#report-list-post ul").empty();
        $.each(data, function(item, val) {
            str += template.replace("{{url}}",val["url"])
                    .replace("{{title}}", val["title"])
                    .replace("{{date}}", val["date"])
                    .replace("{{content}}",val['content']);
        })
        $("#report-list-post ul").append(str);
        showReportPost();
    })
}

function showReportPost(){
    $(".block_content").click(function(){
        var strValue= '';
        $("#content-post").empty();
        $("#report-reaction-count").empty();
        var point = $(this);
        $(".block_content").removeClass("block_content_last");
        point.addClass("block_content_last");
        var topicId = $("#report-topic-id").val();
        var urlVal = point.parent().parent().data("url");
        $("#show-source-post").attr("href", urlVal)
        $("#show-list-comment").data("url", urlVal);
        $("#content-post").append(point.find(".read-more").text());
        var urlReactionForPost = window.location.origin + "/api/overview/" +topicId+"/getReactionForPost?start="+startDateVal+"&end="+endDateVal+"&url="+urlVal;
        $.getJSON( urlReactionForPost, function(dataSum){
            var template = $("#report-template-post").html();
            strValue = template.replace("{{likeCount}}",dataSum.like)
                               .replace("{{shareCount}}", dataSum.share)
                               .replace("{{commentCount}}", dataSum.comment);
            $("#report-reaction-count").append(strValue);
        });
        var urlReactionForTime = window.location.origin + "/api/overview/" +topicId+"/getReactionForTime?start="+startDateVal+"&end="+endDateVal+"&url="+urlVal;
        $.getJSON(urlReactionForTime, function(data){
            console.log(data);
            var xDate=[],
                yLike=[],
                yShare=[],
                yComment=[];
            for (var i = 0; i < data.length; i++) {
                xDate.push(data[i].date);
                var reaction = data[i].reaction;
                yLike.push(reaction.like),
                yShare.push(reaction.share),
                yComment.push(reaction.comment);
            };
            Highcharts.chart('report-reaction-post', {
                title: {
                    text: 'Biểu đồ tương tác của bài viết'
                },
                yAxis: {
                    title: {
                        text: 'Count'
                    }
                },
                xAxis: {
                    categories: xDate,
                },
                series: [{
                    name: 'Like',
                    data: yLike
                }, {
                    name: 'Share',
                    data: yShare
                }, {
                    name: 'Comment',
                    data: yComment
                }]
            });
        });
        showListCommentOfPost();
    })
}

function showListCommentOfPost(){
    $("#show-list-comment").click(function(){
        $("#list-comment-by-post").empty();
        var point = $(this);
        var topicId = $("#report-topic-id").val();
        var urlVal = point.data("url");
        var url = window.location.origin + "/api/overview/"+topicId+"/getListComment?start="+startDateVal+"&end="+endDateVal+"&url="+urlVal;
        $.getJSON(url, function( data){
            console.log(data);
            var str = '';
            $.each(data, function(item, val){
                var itemTemp='';
                itemTemp+='<li>';
                itemTemp+='<p>';
                itemTemp+='<div class="" style="position: relative;">';
                itemTemp+='<a href="'+val['url']+'"><i>'+val['userName']+'</i></a>';
                itemTemp+='</div>';
                itemTemp+=''+val['content']+'';
                itemTemp+='</p>';
                itemTemp+='</li>';
                str += itemTemp;
            })
            $("#list-comment-by-post").append(str);
        });
        $("#modal-comment-post").modal();
    })
}

function removeReportPost(){

}