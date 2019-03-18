var gloFiles = null;
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
        layui.use('table', function(){
            table = layui.table;
            //方法级渲染
            table.render({
                elem: '#transFlowTable'
                ,method:'post'
                ,where:{flag: $('.table-active').index()==0?1:0,icId:$('#walletAddr').val()}
                // ,width:1200

                ,url: '/operarions/iclist'
                ,headers:{
                    "Authorization":localStorage.getItem("loginToken")
                }
                ,cols: [[
                    // {checkbox: true, fixed: true}
                    {field:'icId', title: 'ID',minWidth:100,templet:'<div><span title="{{d.icId}}">{{d.icId}}</span></div>'}
                    ,{field:'icRate', title: '加息利率',minWidth:100,templet:'<div><span title="{{d.icRate*100}}">{{d.icRate}}%</span></div>'}
                    ,{field:'icLength', title: '加息时长（天）',minWidth:100,templet:'<div><span title="{{d.icLength}}">{{d.icLength}}</span></div>'}
                    ,{field:'icProject', title: '适用项目',minWidth:200,templet:'<div><span title="{{d.icProject}}">{{d.icProject}}</span></div>'}
                    ,{field:'validityTime', title: '有效期',minWidth:200,templet:'<div><span title="{{d.validityTime}}">{{d.validityTime}}</span></div>'}
                    ,{field:'', title: '操作',minWidth:100,align:'center', toolbar: '#barDemo',templet:'<div><span title="操作">操作</span></div>'}
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
    /* retrieveData :function( sSource, aoData, fnCallback) {
         /!*aoData.push( { "name": "id", "value": $('#walletAddr').val()} );*!/
         aoData.push( { "name": "geStatus", "value": $('.table-active').index()} );
         aoData.push( { "name": "userId", "value": $('input[name="walletAddr"]').val()} );
         innderAjax('post',sSource,JSON.stringify(aoData),function (rep){
             fnCallback(rep)
         },"application/json")
     },*/
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
    },
    newCreateProject : function () {
        $('input[type="text"]').val('');
        $('input[type="radio"]').eq(0)[0].checked = true;
        $('.btn-upload').prevAll().remove();
        $("#newProjectModal").modal({backdrop: 'static', keyboard: false});
        innerFunc.initDate();

        // $('#editProjectModal').modal('show');
    },
    saveProject : function () {
        var paramter = {};
        var geAmount = $('#amount_1').val();
        var experienceLength = $('#amount_2').val(),ids = {};
        var liItemLength = $('.select-items .li-item').length;
        if($('#fixed_day')[0].checked){
            var validityDay = $('#amount_4').val();
            if(!(geAmount&&experienceLength&&liItemLength&&validityDay)){
                $('.alertTip').slideDown();
                $('.alertTip').text('带红星项为必填项！');
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
                return
            }
            paramter.icValidityDay = validityDay
        }else{
            var validityBeginTime = $('#createTimeStart').val();
            var validityEndTime = $('#createTimeEnd').val();
            if(!(geAmount&&experienceLength&&liItemLength&&validityBeginTime&&validityEndTime)){
                $('.alertTip').slideDown();
                $('.alertTip').text('带红星项为必填项！');
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
                return
            }
            var check = innerFunc.checkTime();
            if(!check){return}
            paramter.icValidityBeginTime = validityBeginTime;
            paramter.icValidityEndTime = validityEndTime;
        }
        $('.select-items .li-item').each(function (i,val) {
            ids[val.id] = $.trim($(val).find('div').text());
        });
        paramter.icRate = geAmount;
        paramter.icLength = experienceLength;
        paramter.icProject = ids;
        innderAjax('post',"/operarions/icGenerator",JSON.stringify(paramter),function (resp) {
            if(resp.code === '200'){
                $("#newProjectModal").modal('hide');
                $('#success').slideDown();
                $('#success').text(resp.data);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                table.reload('transFlowTable', {
                    where:{flag: $('.table-active').index()==0?1:0,icId:$('#walletAddr').val()}
                });
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text('加息券保存失败！');
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
            }
        },'application/json')
    },
    newSelectItem : function () {
        $("#selectItems").modal({backdrop: 'static', keyboard: false});
        // innderAjax('post',"/admin/financing/EditLanager",{financingUuid:uuid},function (resp) {})
        $('.item-all .li-item').removeClass('item-active');
        var liItemArr = $('.select-items .li-item'),liItemArr1 = $('.item-all .li-item');
        var len = liItemArr.length,len1 = liItemArr1.length,i,j;
        if(len){
            for(i = 0 ; i < len ; i++){
                for(j = 0 ; j < len1 ; j++){
                    if(liItemArr.eq(i).attr('id') === liItemArr1.eq(j).attr('id')){
                        liItemArr1.eq(j).addClass('item-active');
                        continue;
                    }
                }
            }
        }
    },
    itemCollect : function () {
        $('.btn-upload').prevAll().remove();
        var itemArr = $('.item-active');
        var len = itemArr.length,arrId =[],arrValue = [],html = '';
        if(!len){
            $('.alertTip').slideDown();
            $('.alertTip').text('至少选择一项理财项目！');
            setTimeout(function () {
                $('.alertTip').slideUp();
            },2000);
            return
        }
        if(len>5){
            $('.alertTip').slideDown();
            $('.alertTip').text('理财项目不得超过五个！');
            setTimeout(function () {
                $('.alertTip').slideUp();
            },2000);
            return
        }
        $.each(itemArr,function (i,val) {
            html += '<li class="li-item" id="'+val.id+'" title="'+$(val).text()+'"><div class="ell">'+$(val).text()+'</div><span>×</span></li>'
        })
        $('.select-items').prepend(html);
        $("#selectItems").modal('hide');
    },
    showIssue : function () {
        $("#issueModal").modal('show');
        var self = this;
        $('#select_user').val(0);
        $('#excel_name').text('-');
        $('#excel_upload').val('');
        if(!($('.excel-div').hasClass('dn'))){
            $('.excel-div').addClass('dn')
        }
        $('.confirmUserType').attr('tradeId',$(self).attr('tradeId'));
    },
    saveIssue : function () {
        var self = this,paramter = {};
        paramter.geId = $(self).attr('tradeId');
        paramter.flag = $('#select_user').val();

        var formdata = new FormData();
        formdata.append("icId",paramter.geId);
        formdata.append("flag",paramter.flag);
        formdata.append("icUserExecl",gloFiles);
        $.ajax({
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            url : '/operarions/icSend',
            type : 'POST',
            dataType: 'json',
            data : formdata,
            cache: false,
            processData: false,
            contentType: false,
            success : function(resp) {
                if(resp.code === '200'){
                    $("#issueModal").modal('hide');
                    $('#success').slideDown();
                    $('#success').text(resp.message);
                    setTimeout(function () {
                        $('#success').slideUp();
                    },2000);
                    gloFiles = null;
                    $("#issueModal").modal('hide');
                    table.reload('transFlowTable', {
                        where:{flag: $('.table-active').index()==0?1:0,geId:$('#walletAddr').val()}
                    });
                }else{
                    $('.alertTip').slideDown();
                    $('.alertTip').text(resp.message);
                    setTimeout(function () {
                        $('.alertTip').slideUp();
                    },2000);
                }
            }
        });
    }
};

$('#rateCoupon').on('click','#trandQuerybyServer',function () {
    table.reload('transFlowTable', {
        where:{flag: $('.table-active').index()==0?1:0,icId:$('#walletAddr').val()}
    });
})//搜索表格
.on('click','.new-create',innerFunc.newCreateProject)//打开新建弹窗
.on('click','#saveProject',innerFunc.saveProject)//保存新建
.on('click','.invest-tab span',function(){
    var self = this;
    if($(self).hasClass('table-active')){
        return
    }
    $(self).addClass('table-active').siblings().removeClass('table-active');
    table.reload('transFlowTable', {
        where:{flag: $('.table-active').index()==0?1:0,icId:$('#walletAddr').val()}
    });
})//表格切换
.on('click','.btn-radio',function () {
    var self = this;
    var index = $(self).index();
    if(index === 0){
        $('.change-box').eq(index).removeClass('dn');
        $('.change-box').eq(index+1).addClass('dn')
    }else{
        $('.change-box').eq(index-1).removeClass('dn');
        $('.change-box').eq(index-2).addClass('dn')
    }
})//新建弹窗中radio切换操作
.on('click','.li-item span',function () {
    var self = this;
    $(self).parent().remove();
})//点击删除按钮操作
.on('click','.btn-upload',innerFunc.newSelectItem)//点击选择项目按钮
.on('click','.item-all li',function () {
    var self = this;
    $(self).toggleClass('item-active');
})//单个项目点击操作
.on('click','#saveItems',innerFunc.itemCollect)//保存收集的项目
.on('click','.showIssue',innerFunc.showIssue)//点击发放
.on('click','.confirmUserType',innerFunc.saveIssue)//点击发放保存
.on('click','.export-tmpl',function () {
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
                window.location.href = '/operarions/icExportExeclUser';
            } else {
                $('.alertTip').slideDown();
                $('.alertTip').text(rep.message);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                }, 2000);
            }
        }
    })
    // window.location.href = '/operarions/icExportExeclUser';
})//导出模板
.on('change','#select_user',function (e) {
    if($('#select_user').val() == 2){
        $('.excel-div').removeClass('dn')
    }else{
        if(!($('.excel-div').hasClass('dn'))){
            $('.excel-div').addClass('dn')
        }
    }
})//导出模板切换
.on('click','.showAgents-bbin',function () {
    window.open("operationManage/rateCouponDetail.html?id="+this.id+"&step="+($('.table-active').index()==0?1:0))
})//点击详情
.on('click','.btn-import',function () {
    $('#rateCoupon #excel_upload').trigger('click');
})
.on("change","#excel_upload",function (e) {
    var $this = $(e.currentTarget);
    var fs = $this[0].files;
    if(!fs.length) return;
    // file为前面获得的
    var _type=fs[0].name;
    if(_type.indexOf("xls")!=-1||_type.indexOf("xlsx")!=-1){//判断他是不excel文件
        gloFiles = fs[0];
        $('#excel_name').text($this[0].files[0].name);
        $('#excel_name').attr('title',$this[0].files[0].name);
    }else{
        layer.msg('请，上传excel文件！！');
    }

})
.on('input','#amount_4',function () {
    var reg = /^[1-9]\d*$/;
    if(!(reg.test(Number($.trim($('#amount_4').val()))))){
        $('#amount_4').val('')
    }
}).on('input','#amount_2',function () {
    var reg = /^[1-9]\d*$/;
    if(!(reg.test(Number($.trim($('#amount_2').val()))))){
        $('#amount_2').val('')
    }
}).on('input','#amount_1',function () {
    var reg = /^(\d|[1-9]\d|100)(\.\d{1,2})?$/;
    if(!(reg.test(Number($.trim($('#amount_1').val()))))){
        $('#amount_1').val('')
    }
});
var initData = function(){
    innderAjax('post',"/operarions/icfinancing",{},function(r){
        var liArr = [],html = '';
        for(var i in r.data){
            html += '<li class="li-item ell" id="'+i+'" title="'+r.data[i]+'"> '+r.data[i]+'</li>';
        }
        $('.item-all').html(html)
    });

    /* var html = '';
     var liArr = [{id:1,value:'6月期理财项目'},{id:2,value:'7月期理财项目'},{id:3,value:'8月期理财项目'},{id:4,value:'9月期理财项目'},{id:5,value:'10月期理财项目'}]
     $.each(liArr,function (i,val) {
         html += '<li class="li-item" id="'+val.id+'"> '+val.value+'</li>';
     })
     $('.item-all').html(html)*/
}
//入口
$(function () {
    innerFunc.tableShow();
    initData();
});