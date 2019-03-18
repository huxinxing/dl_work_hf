var table;
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
                ,where:{geStatus: $('#paymentType').val(),userId:$('#walletAddr').val(),geId:sessionStorage.getItem('geId')}
                // ,width:1200

                ,url: '/operarions/geUserList'
                ,headers:{
                    "Authorization":localStorage.getItem("loginToken")
                }
                ,cols: [[
                    // {checkbox: true, fixed: true}
                    {field:'userid', title: '用户ID',minWidth:200}
                    ,{field:'displayName', title: '用户昵称',minWidth:250}
                    ,{field:'reviceTime', title: '领取时间',minWidth:250}
                    ,{field:'geState', title: '状态',minWidth:200}
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
    },
    /*newCreateProject : function () {
        $('input[type="text"]').val('');
        $('input[type="radio"]').eq(0)[0].checked = true;
        $("#newProjectModal").modal({backdrop: 'static', keyboard: false});
        innerFunc.initDate();

        // $('#editProjectModal').modal('show');
    },*/
    saveProject : function () {
        var paramter = {};
        var geAmount = $('#amount_1').val();
        var experienceLength = $('#amount_2').val(),ids = {};
        var liItemLength = $('.select-items .li-item').length;
        var condition = $('#amount_3').val();
        if($('#fixed_day')[0].checked){
            var validityDay = $('#amount_4').val();
            if(!(geAmount&&experienceLength&&liItemLength&&condition&&validityDay)){
                $('.alertTip').slideDown();
                $('.alertTip').text('带红星项为必填项！');
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
                return
            }
            paramter.validityDay = validityDay
        }else{
            var validityBeginTime = $('#createTimeStart').val();
            var validityEndTime = $('#createTimeEnd').val();
            if(!(geAmount&&experienceLength&&liItemLength&&condition&&validityBeginTime&&validityEndTime)){
                $('.alertTip').slideDown();
                $('.alertTip').text('带红星项为必填项！');
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
                return
            }
            var check = innerFunc.checkTime();
            if(!check){return}
            paramter.validityBeginTime = validityBeginTime;
            paramter.validityEndTime = validityEndTime;
        }
        $('.select-items .li-item').each(function (i,val) {
            ids[val.id] = $.trim($(val).find('div').text());
        });
        paramter.geId = sessionStorage.getItem("geId");
        paramter.geStatus = $('#status_bbinDetail').val();
        paramter.geAmount = geAmount;
        paramter.experienceLength = experienceLength;
        paramter.condition = condition;
        paramter.experienceProject = ids;
        innderAjax('post',"/operarions/geUpdate",JSON.stringify(paramter),function (resp) {
            if(resp.code === '200'){
                $("#newProjectModal").modal('hide');
                $('#success').slideDown();
                $('#success').text(resp.data);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                $('#myModal').modal('hide');
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text('体验金更新失败！');
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
    },
    saveIssue : function () {
        var self = this;
        var id = $(self).attr('tradeId');
        $('#select_user').val();
        // innderAjax('post',"/admin/financing/EditLanager",{financingUuid:uuid},function (resp) {})
    },
    saveOrclose : function (e) {
        var $this = $(e.currentTarget);
        var index = $this.index();
        $('.confirm').attr('tid',index);
        $('#myModal').modal('show');
        if(!index){
            $('#myModal .modal-body').text('确定要保存编辑内容吗？');
        }else{
            $('#myModal .modal-body').text('确定要关闭弹窗吗？');
        }
    },
    confirm : function () {
        var index = Number($('.confirm').attr('tid'));
        if(!index){
            innerFunc.saveProject();
        }else{
            window.close()
        }
    },
    urlSplit:function(){
        var urlArr = window.location.search.slice(1).split('&'),id,step;
        $.each(urlArr,function (i,val) {
            var single = val.split('=');
            if(single[0] === 'id'){
                id = single[1];
            }
            if(single[0] === 'step'){
                step = single[1];
            }
            sessionStorage.setItem('geId',id);
            sessionStorage.setItem('flag',step);
        })
    },
};
// $('.new-create').on('click',innerFunc.newCreateProject);//打开新建弹窗
/*$(document).on('click','#saveProject',innerFunc.saveProject);//保存新建*/
$('.invest-tab span').on('click',function(){
    var self = this;
    if($(self).hasClass('table-active')){
        return
    }
    $(self).addClass('table-active').siblings().removeClass('table-active');
    var index = $(self).index();
    $('.box-0').eq(index).removeClass('dn').siblings().addClass('dn')
    if(index){
        table.reload('transFlowTable', {
            where:{geStatus: $('#paymentType').val(),userId:$('#walletAddr').val(),geId:sessionStorage.getItem('geId')}
        });
    }

});//表格切换
$(document).on('click','.btn-radio',function () {
    var self = this;
    var index = $(self).index();
    if(index === 0){
        $('.change-box').eq(index).removeClass('dn');
        $('.change-box').eq(index+1).addClass('dn')
    }else{
        $('.change-box').eq(index-1).removeClass('dn');
        $('.change-box').eq(index-2).addClass('dn')
    }
});//新建弹窗中radio切换操作
$(document).on('click','.li-item span',function () {
    var self = this;
    $(self).parent().remove();
});//点击删除按钮操作
$(document).on('click','.btn-upload',innerFunc.newSelectItem);//点击选择项目按钮
$(document).on('click','.item-all li',function () {
    var self = this;
    $(self).toggleClass('item-active');
});//单个项目点击操作
$(document).on('click','#saveItems',innerFunc.itemCollect);//保存收集的项目
$(document).on('click','.btn-black',innerFunc.saveOrclose)//点击保存或关闭按钮
.on("click",".confirm",innerFunc.confirm);//点击确定按钮
$('#trandQuerybyServer').on('click',function(){
    table.reload('transFlowTable', {
        where:{geStatus: $('#paymentType').val(),userId:$('#walletAddr').val(),geId:sessionStorage.getItem('geId')}
    });
});
$('#paymentType').on('change',function () {
    table.reload('transFlowTable', {
        where:{geStatus: $('#paymentType').val(),userId:$('#walletAddr').val(),geId:sessionStorage.getItem('geId')}
    });
});
$(document).on('input','#amount_4',function () {
    var reg = /^[1-9]\d*$/;
    if(!(reg.test(Number($.trim($('#amount_4').val()))))){
        $('#amount_4').val('')
    }
}).on('input','#amount_1',function () {
    var reg = /^[1-9]\d*$/;
    if(!(reg.test(Number($.trim($('#amount_1').val()))))){
        $('#amount_1').val('')
    }
}).on('input','#amount_2',function () {
    var reg = /^[1-9]\d*$/;
    if(!(reg.test(Number($.trim($('#amount_2').val()))))){
        $('#amount_2').val('')
    }
}).on('input','#amount_3',function () {
    var reg = /^[1-9]\d*$/;
    if(!(reg.test(Number($.trim($('#amount_3').val()))))){
        $('#amount_3').val('')
    }
});
var initData = function(){
    innderAjax('post',"/operarions/gedetail",{geId:sessionStorage.getItem('geId')},function(r){
        if(r.code == '200'){
            $('#amount_1').val(r.data.geAmount);
            $('#amount_2').val(r.data.experienceLength);
            // var liItemLength = $('.select-items .li-item').length;
            $('#amount_3').val(r.data.condition);
            $('.createTime').text(r.data.geCreateTime);
            $('.status').val(r.data.geStatus);
            var validityDay = r.data.validityDay;
            if(!(validityDay === null)){
                $('#fixed_day')[0].checked = true;
                $('#amount_4').val(r.data.validityDay)
            }else{
                $('.btn-radio').eq(1).trigger('click');
                // $('#fixed_date')[0].checked = true;
                $('#amount_4').val(r.data.validityDay);
                $('#createTimeStart').val(r.data.validityBeginTime);
                $('#createTimeEnd').val(r.data.validityEndTime);
            }
            var liArr = [],html = '',liObj = r.data.experienceProject;
            for(var i in liObj){
                html += '<li class="li-item" id="'+i+'" title="'+liObj[i]+'"><div class="ell">'+liObj[i]+'</div><span>×</span></li>'
            }
            $('.select-items').prepend(html);
        }
    });
    innderAjax('post',"/operarions/gefinancing",{},function(r){
        var liArr = [],html = '';
        for(var i in r.data){
            html += '<li class="li-item ell" id="'+i+'" title="'+r.data[i]+'"> '+r.data[i]+'</li>';
        }
        $('.item-all').html(html)
    });
}
//入口
$(function () {
    innerFunc.urlSplit();
    innerFunc.tableShow();
    innerFunc.initDate();
    initData();
});