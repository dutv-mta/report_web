$(function() {
    $("#report-select-date").daterangepicker({
        singleDatePicker: true,
        showDropdowns: true,
        maxDate: moment().subtract(1, 'days'),
        minDate: moment().subtract(60, 'days'),
        locale: {
                    format: 'YYYY/MM/DD'
                }
    }, function(start, end, label) {
        var date = new Date(start.subtract(1, 'days')),
            year = date.getFullYear(),
            month = ("0" + (date.getMonth() + 1)).slice(-2),
            day = ("0" + date.getDate()).slice(-2),
            dateChoose = year + '' + month + '' + day;
        getListRepostPost(dateChoose);
    });
});

function getListRepostPost(dateChoose) {
    var topicId = $("#report-topic-id").val();
    var url = window.location.origin + "/api/detail/" + topicId + "?date=" + dateChoose;
    $.getJSON(url, function(data) {
        $("#report-list-post ul").empty();
        var template = $("#item-template-post").html();
        var str = "";
        $.each(data, function(item, val) {
            str += template.replace("{{id}}",val["id"])
                .replace("{{title}}", val["title"])
                .replace("{{date}}", val["date"]);
        })
        $("#report-list-post ul").append(str);
        showDetailPost();
    }).fail(function() {
        console.log("Error");
    })
}

function showDetailPost(){
    $(".block_content").click(function(){
        var point = $(this),
                strDate =$("#report-select-date").val(),
                id;
        $(".block_content").removeClass("block_content_last");
        point.addClass("block_content_last");
        id = point.parent().parent().data("id");
        var date= strDate.split("/").join("");
        var url = window.location.origin+"/api/detail/"+date+"/"+id;
        $.getJSON(url, function(data){
            console.log(data);
            $("#detail-post").empty();
            var detailTemplate =$("#detail-template-post").html();
            var str = detailTemplate.replace("{{title}}", data["title"])
                                       .replace("{{url}}", data["url"])
                                       .replace("{{pubTime}}", data["pubTime"])
                                       .replace("{{content}}", data["content"]);
            $("#detail-post").append(str);
        }).fail(function(){
            console.log("Get detail post fail!")
        });
    });
};