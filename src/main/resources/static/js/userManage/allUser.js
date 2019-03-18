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
            {field:'userId', title: 'UID',width:'10%',templet:'<div><span title="{{d.userId}}">{{d.userId}}</span></div>'}
            ,{field:'codeId', title: '邀请码',width:'10%',templet:'<div><span title="{{d.codeId}}">{{d.codeId}}</span></div>'}
            ,{field:'displayName', title: '昵称',width:'10%',templet:'<div><span title="{{d.displayName}}">{{d.displayName}}</span></div>'}
            ,{field:'step', title: '层级',width:'10%',templet:'<div><span title="{{d.step}}">{{d.step}}</span></div>'}
            ,{field:'financingScale', title: '理财返点',width:'10%',templet:'<div><span title="{{d.financingScale}}">{{d.financingScale}}</span></div>'}
            ,{field:'registTime', title: '注册时间',width:'15%',templet:'<div><span title="{{d.registTime}}">{{d.registTime}}</span></div>'}
            ,{field:'', title: '操作',align:'center', toolbar: '#oper_user',templet:'<div><span title="操作">操作</span></div>'}
        ];
        layui.use('table', function(){
            table = layui.table;
            //方法级渲染
            table.render({
                elem: '#transFlowTable'
                ,method:'post'
                ,where:{condition:$('#uidWalletAddr').val()}
                // ,width:1200

                ,url: '/admin/user/userList'
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
    phoneNum : function () {
        var userId = $(this).attr("targetId");
        $('#checkMobileNumber').modal('show');
        $.ajax({
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            "type": "post",
            "url": "/admin/user/checkMobileNumber",
            "dataType": "json",
            "data":{userId:userId},
            "success": function(data) {
                $('#phoneNumber').val(data.data);
            }
        })
    },
    wallet : function () {
        $("#walletModal").modal('show');
        var userid = $(this).attr("targetId");
        var uname = $(this).attr("targetName");
        $("#projectN").html(uname+"--钱包地址：");
        $.ajax( {
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            "type": "post",
            "url": "/admin/user/checkWallet",
            "dataType": "json",
            "data":{userId:userid},
            "success": function(data) {
                var wallets = data.data;
                var trs ='';
                for(var key in wallets){
                    trs+='<tr>';
                    trs+='<td>'+key+'</td>';
                    trs+='<td>'+wallets[key]+'</td>';
                    trs+='</tr>'
                }
                /*$.each(wallets,function(index,wallet){
                    trs+='<tr>';
                    trs+='<td>'+(wallet.type=="3"?"以太币":"其他币")+'</td>';
                    trs+='<td>'+wallet.token+'</td>';
                    // var status =wallet.status=='1'?'有效':'无效';
                    // trs+='<td>'+status+'</td>';
                    // trs+='<td><a  href="javascript:void(0);" onclick="unbind(\''+wallet.id+'\')">解绑钱包</a></td>';
                    trs+='</tr>'
                });*/
                $(".walletTable").html(trs);
            }
        });
    },
    setting : function () {
        $('#setModal').modal('show');
        $('.s-tip').remove();
        var userid = $(this).attr("targetId");
        $('#saveSetting').attr("targetId",userid);
        innderAjax('post',"/admin/user/userMessage",{userId:userid},function (resp) {
            if(resp.code == '200'){
                $('#return_rate').val(resp.data.financingScale);
                $('#discount_rate').val(resp.data.userDiscountRate);
                $('#change_leader').val(resp.data.parentId);
            }
        })
    },
    saveSetting : function () {
        var userid = $(this).attr("targetId");
        var returnRate = $.trim($('#return_rate').val());
        var discount = $.trim($('#discount_rate').val());
        var changeLeader = $.trim($('#change_leader').val());
        if(returnRate){
            var p=/^[0-4](\.[0-9])?$/;
            if(!(p.test(returnRate)||returnRate == 5)){
                $('#return_rate').parent().parent().append('<div class="red-sp s-tip">输入有误，请按要求设置</div>')
                return
            }
            // if(!returnRate){$('#return_rate').parent().parent().append('<div class="red-sp s-tip">输入有误，请按要求设置</div>')}
        }
        if(discount){
            var reg = /^\d(\.\d)?$|^[1-9]\d(\.\d)?$/
            if(!(reg.test(discount)||discount == 100)){
                $('#discount_rate').parent().parent().append('<div class="red-sp s-tip">输入有误，请按要求设置</div>')
                return
            }
        }
        innderAjax('post',"/admin/user/settingUserMessage",{userId:userid,financingScale:returnRate,userDiscountRate:discount,parentId:changeLeader},function (resp) {
            if(resp.code === '200'){
                $('#setModal').modal('hide');
                $('#success').slideDown();
                $('#success').text(resp.data);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                table.reload('transFlowTable', {
                    where:{condition:$('#uidWalletAddr').val()}
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
    removeTip : function () {
        $(this).parent().parent().find('div.s-tip').remove();
    }
};
$('#allUser').on('click','#trandQuerybyServer',function () {//点击查询
    table.reload('transFlowTable', {
        where:{condition:$('#uidWalletAddr').val()}
    });
}).on('click','#user_detail',function () {//点击数据
    window.open('dataStatistics/usersDetail.html?id='+($(this).attr('targetId'))+'&step='+$(this).attr('targetStep'))
}).on('click','#user_phoneNum',innerFunc.phoneNum)//点击手机号
.on('click','#user_addr',innerFunc.wallet)//点击钱包
.on('click','#user_set',innerFunc.setting)//点击设置
    .on('click','#saveSetting',innerFunc.saveSetting)//点击保存设置
    .on('focus','#return_rate',innerFunc.removeTip)
    .on('focus','#discount_rate',innerFunc.removeTip)

//入口
$(function () {
    innerFunc.tableShow();
});