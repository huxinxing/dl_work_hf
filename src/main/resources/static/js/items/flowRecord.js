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
    tableShow : function(){
        var check = innerFunc.checkTime();
        if(!check){return}
        layui.use('table', function(){
            table = layui.table;
            //方法级渲染
            table.render({
                elem: '#transFlowTable'
                ,method:'post'
                ,where:{status:$('#paymentType').val(),tranferId: $('#flowId').val(),user:$('#nameId').val(),startTime:$('#createTimeStart').val(),endTime:$('#createTimeEnd').val()}
                // ,width:1200

                ,url: '/statistics/FinancingTranferRecord'
                ,headers:{
                    "Authorization":localStorage.getItem("loginToken")
                }
                ,cols: [[
                    // {checkbox: true, fixed: true}
                    {field:'id', title: '流水ID',minWidth:100,templet:'<div><span title="{{d.id}}">{{d.id}}</span></div>'}
                    ,{field:'type', title: '状态',minWidth:100,templet:'<div><span title="{{d.type}}">{{d.type}}</span></div>'}
                    ,{field:'userId', title: '用户ID',minWidth:100,templet:'<div><span title="{{d.userId}}">{{d.userId}}</span></div>'}
                    ,{field:'userName', title: '昵称',minWidth:100,templet:'<div><span title="{{d.userName}}">{{d.userName}}</span></div>'}
                    ,{field:'fromAddress', title: 'from',minWidth:150,templet:'<div><span title="{{d.fromAddress}}">{{d.fromAddress}}</span></div>'}
                    ,{field:'toAddress', title: 'to',minWidth:150,templet:'<div><span title="{{d.toAddress}}">{{d.toAddress}}</span></div>'}
                    ,{field:'coinType', title: '币种',minWidth:100,templet:'<div><span title="{{d.coinType}}">{{d.coinType}}</span></div>'}
                    ,{field:'amount', title: '金额',minWidth:100,templet:'<div><span title="{{d.amount}}">{{d.amount}}</span></div>'}
                    ,{field:'TransferTime', title: '时间',minWidth:100,templet:'<div><span title="{{d.TransferTime}}">{{d.TransferTime}}</span></div>'}
                ]]
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
    }
};

$('#flowRecord').on('click','#trandQuerybyServer',function () {
    var check = innerFunc.checkTime();
    if(!check){return}
    table.reload('transFlowTable', {
        where:{status:$('#paymentType').val(),tranferId: $('#flowId').val(),user:$('#nameId').val(),startTime:$('#createTimeStart').val(),endTime:$('#createTimeEnd').val()}    });
})//搜索表格
.delegate('#paymentType','change',function(){
    var check = innerFunc.checkTime();
    if(!check){return}
    table.reload('transFlowTable', {
        where:{status:$('#paymentType').val(),tranferId: $('#flowId').val(),user:$('#nameId').val(),startTime:$('#createTimeStart').val(),endTime:$('#createTimeEnd').val()}    });
}).on('click','#downList',function () {
    var check = innerFunc.checkTime();
    if(!check){return}
    $.ajax({
        "headers": {
            "Authorization":localStorage.getItem("loginToken")
        },
        "type": "get",
        "url" : "/operarions/isTrueExportExecl",
        "dataType": "json",
        "data": {},
        success : function (rep) {
            if(rep.code == '200'){
                window.location.href = '/statistics/FinancingTransferExecl?status='+$("#paymentType").val()+'&tranferId='+ $("#flowId").val()+'&user='+$("#nameId").val()+'&startTime='+$("#createTimeStart").val()+'&endTime='+$("#createTimeEnd").val()
            } else {
                $('.alertTip').slideDown();
                $('.alertTip').text(rep.message);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                }, 2000);
            }

        }
    })
})//下载列表
//入口
$(function () {
    innerFunc.initDate();
    innerFunc.tableShow();
});