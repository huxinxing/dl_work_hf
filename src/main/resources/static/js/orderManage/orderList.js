var oTable;
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
    checkTime : function(){
        var enddate = new Date(($('input[name="createTimeEnd"]').val()).replace(/-/g,"/"));
        var startTime = new Date(( $('input[name="createTimeStart"]').val()).replace(/-/g,"/"));
        if(startTime !=null && enddate!=null && enddate < startTime)
        {   layer.msg('结束时间不能早于开始时间');
            return false;
        }else{
            return true;
        }
    },
    tableShow : function(index){
        var check = innerFunc.checkTime();
        if(!check){return}
        var cols = [
            {field:'recordId', title: '订单ID',minWidth:100,templet:'<div><span title="{{d.recordId}}">{{d.recordId}}</span></div>'}
            ,{field:'financingName', title: '项目名称',minWidth:100,templet:'<div><span title="{{d.financingName}}">{{d.financingName}}</span></div>'}
            ,{field:'userId', title: '用户ID',minWidth:100,templet:'<div><span title="{{d.userId}}">{{d.userId}}</span></div>'}
            ,{field:'disPlayName', title: '昵称',minWidth:150,templet:'<div><span title="{{d.disPlayName}}">{{d.disPlayName}}</span></div>'}
            ,{field:'address', title: '钱包地址',minWidth:200,templet:'<div><span title="{{d.address}}">{{d.address}}</span></div>'}
            ,{field:'investMent', title: '投资金额',minWidth:150,templet:'<div><span title="{{d.investMent}}">{{d.investMent}}</span></div>'}
            ,{field:'recordCreateTime', title: '订单确认时间',minWidth:150,templet:'<div><span title="{{d.recordCreateTime}}">{{d.recordCreateTime}}</span></div>'}
        ]
        if(index == 0 ||index == undefined ){
            cols.push({field:'surplusDay', title: '订单剩余时间',minWidth:150,templet:'<div><span title="{{d.surplusDay}}">{{d.surplusDay}}</span></div>'});
        }
        if(index == 1){
            cols.push({field:'surplusDay', title: '订单剩余时间',minWidth:150,templet:'<div><span title="{{d.surplusDay}}">{{d.surplusDay}}</span></div>'},{field:'redeemptionStatus', title: '赎回状态',minWidth:100,templet:'<div><span title="{{d.redeemptionStatus}}">{{d.redeemptionStatus}}</span></div>'});
        }
        if(index == 3){
            cols.push({field:'failOrderStatus', title: '已处理状态',minWidth:150,templet:'<div><span title="{{d.failOrderStatus}}">{{d.failOrderStatus}}</span></div>'});
        }
        cols.push({field:'', title: '操作',minWidth:100,align:'center', toolbar: '#barDemo',templet:'<div><span title="操作">操作</span></div>'})
        var parameter = {flag: $('.table-active').index()+1,recordId:$('#walletAddr').val(),financingName:$('#itemName').val(),orderCollection:$('#mixSearch').val()}
        if(index == 4){
            parameter.searchTime = $('#select_time').val();
        }else{
            parameter.beginTime = $('#createTimeStart').val();
            parameter.endTime = $('#createTimeEnd').val();
        }
        layui.use('table', function(){
            table = layui.table;
            //方法级渲染
            table.render({
                elem: '#transFlowTable'
                ,method:'post'
                ,where:parameter
                // ,width:1200

                ,url: '/order/orderList'
                ,headers:{
                    "Authorization":localStorage.getItem("loginToken")
                }
                ,cols: [cols]
                ,request: {
                    pageName: 'pageNum' //页码的参数名称，默认：page
                    ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
                }
                ,response: {
                    statusName: 'code' //数据状态的字段名称，默认：code
                    ,statusCode: 200 //成功的状态码，默认：0
                    ,msgName: 'message' //状态信息的字段名称，默认：msg
                    ,countName: 'total' //数据总数的字段名称，默认：count
                    ,dataName: 'list' //数据列表的字段名称，默认：data
                }
                ,page: true
                ,done: function(res, curr, count){

                }
                // ,height: 315
            });
        });

    },

    initDate:function () {
        //开始时间
        $("#createTimeStart").datetimepicker({
            format: 'yyyy-mm-dd hh:ii:00',//日期的格式
            startDate:'1900-01-01',//选择器的开始日期
            autoclose:true,//日期选择完成后是否关闭选择框
            bootcssVer:2,//显示向左向右的箭头
            language:'zh_CN',//语言
            minView: "hour"//表示日期选择的最小范围，默认是hour
        });

        $("#createTimeEnd").datetimepicker({
            format: 'yyyy-mm-dd hh:ii:00',//日期的格式
            startDate:'1900-01-01',//选择器的开始日期
            autoclose:true,//日期选择完成后是否关闭选择框
            bootcssVer:2,//显示向左向右的箭头
            language:'zh_CN',//语言
            minView: "hour"//表示日期选择的最小范围，默认是hour
        });

        $("#select_time").datetimepicker({
            format: 'yyyy-mm-dd',//日期的格式
            startDate:'1900-01-01',//选择器的开始日期
            autoclose:true,//日期选择完成后是否关闭选择框
            bootcssVer:2,//显示向左向右的箭头
            language:'zh_CN',//语言
            initialDate: new Date(),
            minView: 2//表示日期选择的最小范围，默认是hour
        });
        var now = new Date();
        $("#select_time").val(now.getFullYear()+'-'+(now.getMonth()+1<10?'0'+(now.getMonth()+1):now.getMonth()+1)+'-'+(now.getDate()<10?'0'+now.getDate():now.getDate()))

    }

};

$('#trandQuerybyServer').on('click',function () {
    var check = innerFunc.checkTime();
    if(!check){return}
    var index = $('.table-active').index();
    var parameter = {flag: $('.table-active').index()+1,recordId:$('#walletAddr').val(),financingName:$('#itemName').val(),orderCollection:$('#mixSearch').val()}
    if(index == 4){
        parameter.searchTime = $('#select_time').val();
    }else{
        parameter.beginTime = $('#createTimeStart').val();
        parameter.endTime = $('#createTimeEnd').val();
    }
    table.reload('transFlowTable', {
        where:parameter
    });
});//搜索表格
$('.invest-tab span').on('click',function(){
    $('#walletAddr').val('');
    $('#itemName').val('');
    $('#mixSearch').val('');
    $('#createTimeStart').val('');
    $('#createTimeEnd').val('');
    var self = this;
    if($(self).hasClass('table-active')){
        return
    }
    $(self).addClass('table-active').siblings().removeClass('table-active');
    var index = $('.table-active').index();
    if(index == 4){
        $('#select_time').parent().removeClass('dn');
        $('#createTimeStart').parent().addClass('dn');
        $('#createTimeEnd').parent().addClass('dn');
    }else{
        $('#select_time').parent().addClass('dn');
        $('#createTimeStart').parent().removeClass('dn');
        $('#createTimeEnd').parent().removeClass('dn');
    }
    innerFunc.tableShow(index);
});//表格切换
$(document).on('click','.showAgents-1',function () {
    window.open("orderManage/orderListDetail.html?id="+this.id+"&step="+$('.table-active').index())
});//点击详情

//入口
$(function () {
    innerFunc.initDate();
    innerFunc.tableShow();
});