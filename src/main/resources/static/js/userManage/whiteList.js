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
    tableShow : function(index){
        var cols = [
            {field:'id', title: '编号',width:'15%',templet:'<div><span title="{{d.id}}">{{d.id}}</span></div>'}
            ,{field:'walletAddress', title: '钱包地址',width:'15%',templet:'<div><span title="{{d.walletAddress}}">{{d.walletAddress}}</span></div>'}
            ,{field:'modifyTime', title: '时间',width:'20%',templet:'<div><span title="{{d.modifyTime}}">{{d.modifyTime}}</span></div>'}
            ,{field:'remark', title: '备注',width:'20%',templet:'<div><span title="{{d.remark}}">{{d.remark}}</span></div>'}
            ,{field:'', title: '操作',width:'30%',align:'center', toolbar: '#oper_whiteList',templet:'<div><span title="操作">操作</span></div>'}
        ];
        layui.use('table', function(){
            table = layui.table;
            //方法级渲染
            table.render({
                elem: '#transFlowTable'
                ,method:'post'
                ,where:{walletAddress:$('#uidWalletAddr').val()}
                ,url: '/whitelist/whiteListList'
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
    createWhiteList : function(){
        $('#createModal').modal('show');
    },
    saveAddr : function(){
        var newAddr = $.trim($('#newAddr').val());
        if(!newAddr){layer.msg('请输入钱包地址！！');}
        innderAjax('post',"/whitelist/whiteAdd",{walletAddress:newAddr},function (resp) {
            if(resp.code === '200'){
                $('#createModal').modal('hide');
                $('#success').slideDown();
                $('#success').text(resp.data);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                table.reload('transFlowTable', {
                    where:{walletAddress:$('#uidWalletAddr').val()}
                });
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text(resp.data);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
            }
        })
    },
    removeList : function () {
        $('#removeModal').modal('show');
        $('#confirmRemove').attr('targetId',$(this).attr('targetId'));
        $('#removeAddr').text($(this).attr('targetAddr'))
    },
    confirmRemove : function(){
        var userId = $(this).attr("targetId");
        innderAjax('post',"/whitelist/whiteDel",{whiteId:$(this).attr('targetId')},function (resp) {
            if(resp.code === '200'){
                $('#removeModal').modal('hide');
                $('#success').slideDown();
                $('#success').text(resp.data);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                table.reload('transFlowTable', {
                    where:{walletAddress:$('#uidWalletAddr').val()}
                });
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text(resp.data);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
            }
        })
    },
    updateRemark : function () {
        $('#remarkModal').modal('show');
        $('#saveUpdateRemark').attr('targetId',$(this).attr('targetId'));
    },
    saveUpdateRemark : function () {
        innderAjax('post',"/whitelist/whiteUpdateRemark",{whiteId:$(this).attr('targetId'),remark:$('#white_remark').val()},function (resp) {
            if(resp.code === '200'){
                $('#remarkModal').modal('hide');
                $('#success').slideDown();
                $('#success').text(resp.data);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                table.reload('transFlowTable', {
                    where:{walletAddress:$('#uidWalletAddr').val()}
                });
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text(resp.data);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
            }
        })

    }

};
$('#whiteList').on('click','#trandQuerybyServer',function () {//点击查询
    table.reload('transFlowTable', {
        where:{walletAddress:$('#uidWalletAddr').val()}
    });
}).on('click','#remove_whiteList',innerFunc.removeList)//点击移除
    .on('click','#confirmRemove',innerFunc.confirmRemove)//新增白名单
.on('click','#update_remark',innerFunc.updateRemark)//点击修改备注
    .on('click','#saveUpdateRemark',innerFunc.saveUpdateRemark)//保存修改备注
.on('click','#createWhiteList',innerFunc.createWhiteList)//新增白名单
    .on('click','#saveNewAddr',innerFunc.saveAddr)//保存白名单

//入口
$(function () {
    innerFunc.tableShow();
});