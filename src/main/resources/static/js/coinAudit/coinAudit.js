var pass = true;
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
    tableShow : function(index){
        var cols = [
            {field:'userId', title: '用户ID',minWidth:100,templet:'<div><span title="{{d.userId}}">{{d.userId}}</span></div>'}
            ,{field:'userName', title: '昵称',minWidth:100,templet:'<div><span title="{{d.userName}}">{{d.userName}}</span></div>'}
            ,{field:'coinName', title: '提币币种',minWidth:100,templet:'<div><span title="{{d.coinName}}">{{d.coinName}}</span></div>'}
            ,{field:'amount', title: '提币额度',minWidth:150,templet:'<div><span title="{{d.amount}}">{{d.amount}}</span></div>'}
            ,{field:'amountBalance', title: '账户余额',minWidth:200,templet:'<div><span title="{{d.amountBalance}}">{{d.amountBalance}}</span></div>'}
            ,{field:'address', title: '提币地址',minWidth:150,templet:'<div><span title="{{d.address}}">{{d.address}}</span></div>'}
            ,{field:'createTime', title: '申请时间',minWidth:150,templet:'<div><span title="{{d.createTime}}">{{d.createTime}}</span></div>'}
        ]
        if(index == 0 ||index == undefined ){
            cols.push({field:'', title: '操作',minWidth:100,align:'center', toolbar: '#barDemo',templet:'<div><span title="操作">操作</span></div>'})

            // cols.push({field:'surplusDay', title: '订单剩余时间',minWidth:150,templet:'<div><span title="{{d.surplusDay}}">{{d.surplusDay}}</span></div>'});
        }
        if(index == 1){
            cols.push({field:'confirmType', title: '打币状态',minWidth:150,templet:'<div><span title="{{d.confirmType}}">{{d.confirmType}}</span></div>'},{field:'reviewTime', title: '审核时间',minWidth:100,templet:'<div><span title="{{d.reviewTime}}">{{d.reviewTime}}</span></div>'},{field:'reviewer', title: '审核人',minWidth:100,templet:'<div><span title="{{d.reviewer}}">{{d.reviewer}}</span></div>'});
        }
        if(index == 2){
            cols.push({field:'reviewTime', title: '审核时间',minWidth:100,templet:'<div><span title="{{d.reviewTime}}">{{d.reviewTime}}</span></div>'},{field:'reviewer', title: '审核人',minWidth:100,templet:'<div><span title="{{d.reviewer}}">{{d.reviewer}}</span></div>'},{field:'remark', title: '备注',minWidth:150,templet:'<div><span title="{{d.remark}}">{{d.remark}}</span></div>'});
        }
        layui.use('table', function(){
            table = layui.table;
            //方法级渲染
            table.render({
                elem: '#transFlowTable'
                ,method:'post'
                ,where:{type: $('.table-active').index(),user:$('#mixSearch').val()}
                // ,width:1200

                ,url: '/admin/financing/withdraw/list'
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
                    $('span[title="已完成"]').addClass('color-success');
                    $('span[title="未确认"]').addClass('color-fail');
                }
                // ,height: 315
            });
        });
    }
};
$('#trandQuerybyServer').on('click',function () {
    table.reload('transFlowTable', {
        where:{type: $('.table-active').index(),user:$('#mixSearch').val()}
    });
});//搜索表格
$('.invest-tab span').on('click',function(){
    $('#mixSearch').val('');
    var self = this;
    if($(self).hasClass('table-active')){
        return
    }
    $(self).addClass('table-active').siblings().removeClass('table-active');
    var index = $('.table-active').index();
    innerFunc.tableShow(index);
});//表格切换

$('#coinAudit').on('click','.pass-audit',function () {
    pass = true;
    $('.modal-body').html('');
    $('#myModalLabel').html('');
    $('#myModal').modal('show');
    $('.confirm').attr('targetId',$(this).attr('targetId'));
    $('.modal-body').html('<div class="message-audit">确定通过提币审核？</div>');
}).on('click','.refuse-audit',function () {
    pass = false;
    $('#myModal').modal('show');
    $('#myModalLabel').html('驳回申请');
    $('.confirm').attr('targetId',$(this).attr('targetId'));
    $('.modal-body').html('<textarea id="refuse_audit_comment" placeholder="驳回原因"></textarea>');
}).on('click','.confirm',function () {
    var type = pass?1:2;
    var data = {
        id :$(this).attr('targetId'),
        type :type,
        refuseContain:$('#refuse_audit_comment').val()
    }
    innderAjax('post',"/admin/financing/approval",data,function (resp) {
        if(resp.code == '200'){
            $('#success').slideDown();
            $('#success').text(resp.message);
            setTimeout(function () {
                $('#success').slideUp();
            },2000);
        }else{
            $('.alertTip').slideDown();
            $('.alertTip').text(resp.message);
            setTimeout(function () {
                $('.alertTip').slideUp();
            },2000);
        }
        table.reload('transFlowTable', {
            where:{type: $('.table-active').index(),user:$('#mixSearch').val()}
        });
        $('#myModal').modal('hide');
    });

});

//入口
$(function () {
    innerFunc.tableShow();
});