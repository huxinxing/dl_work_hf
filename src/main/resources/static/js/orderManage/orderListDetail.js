var step;
function innderAjax(type,url,data,callback,contentType){
    contentType = contentType ? contentType:'application/x-www-form-urlencoded;charset=UTF-8';
    $.ajax({
        "headers": {
            "Authorization":localStorage.getItem("loginToken")
        },
        "type": type,
        "url" : url,
        "dataType": "json",
        "contentType": contentType,
        "data": data,
        success : function (rep) {
            callback(rep)
        }
    })
}
var innerFunc = {

    initData: function(){
        var id = innerFunc.urlSplit();
        var url = step == 0?  '/order/orderGoingDetail':
            step == 1 ?  '/order/orderRedeemptionDetail':
                step == 2 ?  '/order/orderFinishDetail':
                    step == 3 ?   '/order/orderFaildDetail':
                        '/order/orderEveryDayDetail';
        /*if(step == 2 || step == 3){
            $('.time-left').remove();
            if(step == 2){
                $('#price_cur').text('结算价格：');
                $('#rate_cur').text('结算收益率：');
                $('.keepMy').append('<div class="s-gray">=<span class="s-num s-gray">-</span></div>');
                $('.keepHi').append('<div class="s-gray">=<span class="s-num s-gray">-</span></div>')
            }
            if(step == 3){
                $('.deal-state').removeClass('dn');
                $('#changeState').removeClass('dn');
                $('.box1-right-bottom').html('');
            }

        }*/
        innderAjax('post',url,{'recordId':id},function (r) {
            var i,len = r.data.length;
            var boxLeft = r.data[0];
            var boRightTop = r.data[1];
            var boxRightBottom = r.data[2];
            var tableBottom = r.data[3];
            var status = boxLeft[1].value;
            if(status.indexOf('已完成')>-1 || status.indexOf('已失败')>-1){
                $('.time-left').remove();
                if(status.indexOf('已完成')>-1){
                    $('#price_cur').text('结算价格：');
                    $('#rate_cur').text('结算收益率：');
                    $('.keepMy').append('<div class="s-gray">=<span class="s-num s-gray">-</span></div>');
                    $('.keepHi').append('<div class="s-gray">=<span class="s-num s-gray">-</span></div>')
                }
                if(status.indexOf('已失败')>-1){
                    $('.deal-state').removeClass('dn');
                    $('#changeState').removeClass('dn');
                    $('.box1-right-bottom').html('');
                }
            }
            $.each(boxLeft,function (i,val) {
                $('.left-ul li span').eq(i).text(val.value);
            });
            $.each(boRightTop,function (i,val) {
                $('.s-num').eq(i).text(val.value);
            });
            $.each(boxRightBottom,function (i,val) {
                $('.box1-right-bottom li span').eq(i).text(val.value).attr('title',val.value);
            });
            var tableObj = {};
            $.each(tableBottom,function (i,val) {
                tableObj[val.key] = val.value;
            });
            var tableArr = [tableObj];
            innerFunc.tableShow(tableArr);
        })
    },
    tableShow : function(tableObj){
        var cols = [
            {field:'financingName', title: '项目名称',minWidth:50,templet:'<div><span title="{{d.financingName}}">{{d.financingName}}</span></div>'}
            ,{field:'userId', title: '用户ID',minWidth:50,templet:'<div><span title="{{d.userId}}">{{d.userId}}</span></div>'}
            ,{field:'userName', title: '昵称',minWidth:50,templet:'<div><span title="{{d.userName}}">{{d.userName}}</span></div>'}
            ,{field:'fromAddress', title: 'From',minWidth:75,templet:'<div><span title="{{d.fromAddress}}">{{d.fromAddress}}</span></div>'}
            ,{field:'toAddress', title: 'To',minWidth:100,templet:'<div><span title="{{d.toAddress}}">{{d.toAddress}}</span></div>'}
            ,{field:'incomAmount', title: '投资金额',minWidth:75,templet:'<div><span title="{{d.incomAmount}}">{{d.incomAmount}}</span></div>'}
            ,{field:'recordCreateTime', title: '确认时间',minWidth:75,templet:'<div><span title="{{d.recordCreateTime}}">{{d.recordCreateTime}}</span></div>'}
            ,{field:'', title: '区块确认',minWidth:50,align:'center', toolbar: '#barDemo',templet:'<div><span title="区块确认">区块确认</span></div>'}
        ];
        var id = innerFunc.urlSplit();
        layui.use('table', function(){
            table = layui.table;
            //方法级渲染
            table.render({
                elem: '#transFlowTable'
                // ,method:'post'
                // ,where:{'recordId':id}
                // ,width:1200
                ,data:tableObj
                // ,url: '/order/orderGoingDetail'
                ,cols: [cols]
                /*,request: {
                    pageName: 'pageNum' //页码的参数名称，默认：page
                    ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }*/
                /*,response: {
                    statusName: 'code' //数据状态的字段名称，默认：code
                    ,statusCode: 200 //成功的状态码，默认：0
                    ,msgName: 'message' //状态信息的字段名称，默认：msg
                    ,countName: 'total' //数据总数的字段名称，默认：count
                    ,dataName: 'list' //数据列表的字段名称，默认：data
                }*/
                // ,done: function(res, curr, count){
                //
                // }
                // ,height: 315
            });
        });

    },
    urlSplit:function(){
        var urlArr = window.location.search.slice(1).split('&'),id;
        $.each(urlArr,function (i,val) {
            var single = val.split('=');
            if(single[0] === 'id'){
                id = single[1];
            }
            if(single[0] === 'step'){
                step = single[1];
            }
        });
        return id;
    }
};


$(document).on('click','#changeState',function () {
   var state = $('.deal-state span').text() === '未处理' ? 1:0;
   var id = innerFunc.urlSplit();
    innderAjax('post','/order/failOrderStatusChange',{'recordId':id,failOrderStatus:state},function (r) {
        $('.deal-state span').text(r.data)
    })
});//点击详情
$(document).on('click','.showAgents',function () {
    window.open("https://etherscan.io/tx/"+this.id)
});//点击详情

//入口
$(function () {
    innerFunc.initData();
    // innerFunc.tableShow();
});