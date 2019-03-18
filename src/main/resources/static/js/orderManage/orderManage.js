function initDate() {
    //选择时间
    $("#createTimeStart").datetimepicker({
        // format: 'yyyy-mm-dd hh:ii:00',//日期的格式
        format: 'yyyy-mm-dd',//日期的格式
        startDate:'1900-01-01',//选择器的开始日期
        autoclose:true,//日期选择完成后是否关闭选择框
        bootcssVer:2,//显示向左向右的箭头
        language:'zh_CN',//语言
        minView: "month"//表示日期选择的最小范围，默认是hour
    }).on('changeDate', function(ev){
        var initDate = todayFunc();
        var timeArr = [];
        if($("#createTimeStart").val() !== initDate){
            $('.btn-totay').addClass('gray-zi');
            timeArr = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24];
        }else{
            if($('.btn-totay').hasClass('gray-zi')){
                $('.btn-totay').removeClass('gray-zi');
            }
            var totay_in = new Date();
            var hour = totay_in.getHours();
            for(var i = 0 ; i< hour ; i++){
                timeArr.push(i+1);
            }
        }
        diagrams(timeArr)
    });
}
function diagrams(timeArr) {
    $.ajax({
        "headers": {
            "Authorization":localStorage.getItem("loginToken")
        },
        "type": "post",
        "url" : "/statistics/FinancingTranferTime",
        "dataType": "json",
        "data": {Time:$("#createTimeStart").val()},
        success : function (r) {
            var myChartLine = echarts.init(document.getElementById('order_line'));
            var optionLine = {
                title: {
                    text: ''
                },
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    bottom:0,
                    itemGap:50,
                    data:['平均到账时间']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '60',
                    containLabel: true
                },
                /*toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },*/
                xAxis: {
                    name:'时',
                    type: 'category',
                    boundaryGap: false,
                    data: timeArr
                },
                yAxis: {
                    name:'到账时间（分）',
                    nameGap:30,
                    type: 'value'
                },
                series: [
                    {
                        name:'平均到账时间',
                        type:'line',
                        data:r.data
                    }
                ]
            };
            myChartLine.setOption(optionLine);
        }
    })
    // innderAjax('post',"/statistics/FinancingTranferTime",{selectTime:$("#createTimeStart").val()},function(r){
        /*var r = {
            "status": "success",
            "code": "200",
            "message": "请求成功",
            "data": [
                18,
                19,
                17,
                20,
                21,
                15,
                21,
                13,
                17,
                16,
                18,
                19,
                17,
                19,
                19,
                19,
                21,
                14,
                19,
                16,
                19,
                17,
                19,
                13
            ]
        };*/

}
function todayFunc(){
    var totay = new Date();
    var year = totay.getFullYear();
    var month = totay.getMonth()+1;
    var day = totay.getDate();
    var hour = totay.getHours();
    var minute = totay.getMinutes();
    var initDate = year+'-'+ (month<10?'0'+month:month) + '-' + (day<10?'0'+day:day) /*+ ' ' + (hour<10?'0'+hour:hour) + ':' + (minute<10?'0'+minute:minute) + ':' +'00'*/;
    return initDate
}
function hourDeal(){
    var timeArr = [];
    var totay_in = new Date();
    var hour = totay_in.getHours();
    for(var i = 0 ; i< hour ; i++){
        timeArr.push(i+1);
    }
    return timeArr
}
$('.btn-totay').on('click',function () {
    if($('.btn-totay').hasClass('gray-zi')){
        $('.btn-totay').removeClass('gray-zi');
    }
    var initDate = todayFunc();
    $("#createTimeStart").val(initDate);
    var timeArr = hourDeal();
    diagrams(timeArr);
});
$(function () {
    initDate();
    $('.btn-totay').trigger('click')
});