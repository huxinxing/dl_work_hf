var gloFiles = null,edit = false;
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
    changeTab: function(){
        var check = innerFunc.checkTime();
        if(!check){return}
        var index = $('.table-active').index();
        if(index == 0){
            table.reload('transFlowTable', {
                where:{status: $('#upOrDown').val() == 0?null:$('#upOrDown').val() ,deviceType:$('#iosOrAndroid').val() == 0?null:$('#iosOrAndroid').val(),beginTime:$('#createTimeStart').val(),endTime:$('#createTimeEnd').val()
                }
            })
        }else{
            table.reload('transFlowTable', {
                where:{status: $('#upOrDown').val() == 0?null:$('#upOrDown').val()}
            })
        }

    },
    tableShow : function(index){
        if(index == 0 || index == undefined){
            var check = innerFunc.checkTime();
            if(!check){return}
            layui.use('table', function(){
                table = layui.table;
                //方法级渲染
                table.render({
                    elem: '#transFlowTable'
                    ,method:'post'
                    ,where:{status: $('#upOrDown').val() == 0?null:$('#upOrDown').val() ,deviceType:$('#iosOrAndroid').val() == 0?null:$('#iosOrAndroid').val(),beginTime:$('#createTimeStart').val(),endTime:$('#createTimeEnd').val()}
                    // ,width:1200

                    ,url: '/operarions/hcBannerList'
                    ,headers:{
                        "Authorization":localStorage.getItem("loginToken")
                    }
                    ,cols: [[
                        // {checkbox: true, fixed: true}
                        {field:'typeId', title: 'ID',minWidth:50,templet:'<div><span title="{{d.typeId}}">{{d.typeId}}</span></div>'}
                        ,{field:'picUrl', title: '轮播图',minWidth:100,templet:'<div><img src="{{d.picUrl}}" height="26" /></div>'}
                        ,{field:'title', title: '标题',minWidth:100,templet:'<div><span title="{{d.title}}">{{d.title}}</span></div>'}
                        ,{field:'validityBeginTime', title: '开始时间',minWidth:100,templet:'<div><span title="{{d.validityBeginTime}}">{{d.validityBeginTime}}</span></div>'}
                        ,{field:'validityEndTime', title: '结束时间',minWidth:100,templet:'<div><span title="{{d.validityEndTime}}">{{d.validityEndTime}}</span></div>'}
                        ,{field:'deviceType', title: '端口',minWidth:60,templet:'<div><span title="{{d.deviceType}}">{{d.deviceType}}</span></div>'}
                        ,{field:'weight', title: '权重',minWidth:50,templet:'<div><span title="{{d.weight}}">{{d.weight}}</span></div>'}
                        ,{field:'status', title: '状态',minWidth:100,templet:'<div><span title="{{d.status}}">{{d.status}}</span></div>'}
                        ,{field:'', title: '操作',minWidth:100,align:'center', toolbar: '#barDemo_banner',templet:'<div><span title="操作">操作</span></div>'}
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
                        $('span[title="已上架"]').addClass('color-orange');
                    }
                    // ,height: 315
                });
            });
        }else if(index == 1){
            var option = {
                elem: '#transFlowTable'
                ,method:'post'
                ,where:{status: $('#upOrDown').val() == 0?null:$('#upOrDown').val()}
                ,url: '/operarions/hcGeList'
                ,headers:{
                    "Authorization":localStorage.getItem("loginToken")
                }
                ,cols: [[
                    // {checkbox: true, fixed: true}
                    {field:'geId', title: '体验金ID',minWidth:50,templet:'<div><span title="{{d.geId}}">{{d.geId}}</span></div>'}
                    ,{field:'geAmount', title: '体验金额（usdx）',minWidth:100,templet:'<div><span title="{{d.geAmount}}">{{d.geAmount}}</span></div>'}
                    ,{field:'geStatus', title: '体验金状态',minWidth:100,templet:'<div><span title="{{d.geStatus}}">{{d.geStatus}}</span></div>'}
                    ,{field:'status', title: '状态',minWidth:100,templet:'<div><span title="{{d.status}}">{{d.status}}</span></div>'}
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
                    $('span[title="已上架"]').addClass('color-orange');
                }
            };
            layui.use('table', function(){
                table = layui.table;
                //方法级渲染
                table.render(option);
            });
        }else{
            var option = {
                elem: '#transFlowTable'
                ,method:'post'
                ,where:{status: $('#upOrDown').val() == 0?null:$('#upOrDown').val()}
                ,url: '/operarions/hcNewManPicList'
                ,headers:{
                    "Authorization":localStorage.getItem("loginToken")
                }
                ,cols: [[
                    // {checkbox: true, fixed: true}
                    {field:'hcNewManId', title: 'ID',minWidth:50,templet:'<div><span title="{{d.hcNewManId}}">{{d.hcNewManId}}</span></div>'}
                    ,{field:'hcNewManPic', title: '图片',minWidth:100,templet:'<div><img src="{{d.hcNewManPic}}" height="26" /></div>'}
                    ,{field:'hcNewManTitle', title: '标题',minWidth:100,templet:'<div><span title="{{d.hcNewManTitle}}">{{d.hcNewManTitle}}</span></div>'}
                    ,{field:'hcNewManStatus', title: '状态',minWidth:100,templet:'<div><span title="{{d.hcNewManStatus}}">{{d.hcNewManStatus}}</span></div>'}
                    ,{field:'', title: '操作',minWidth:100,align:'center', toolbar: '#barDemo_hc',templet:'<div><span title="操作">操作</span></div>'}
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
                    $('span[title="已上架"]').addClass('color-orange');
                }
            };
            layui.use('table', function(){
                table = layui.table;
                //方法级渲染
                table.render(option);
            });
        }


    },
    initDate:function () {
        var start = {
            format: 'yyyy-mm-dd hh:ii:00',//日期的格式
            startDate:'1900-01-01',//选择器的开始日期
            autoclose:true,//日期选择完成后是否关闭选择框
            bootcssVer:2,//显示向左向右的箭头
            language:'zh_CN',//语言
            minView: "hour"//表示日期选择的最小范围，默认是hour
        };
        var end = {
            format: 'yyyy-mm-dd hh:ii:00',//日期的格式
            startDate:'1900-01-01',//选择器的开始日期
            autoclose:true,//日期选择完成后是否关闭选择框
            bootcssVer:2,//显示向左向右的箭头
            language:'zh_CN',//语言
            minView: "hour"//表示日期选择的最小范围，默认是hour
        };
        var endNew = {
            format: 'yyyy-mm-dd 23:59:59',//日期的格式
            startDate:'1900-01-01',//选择器的开始日期
            autoclose:true,//日期选择完成后是否关闭选择框
            bootcssVer:2,//显示向左向右的箭头
            language:'zh_CN',//语言
            minView: 2//表示日期选择的最小范围，默认是hour
        };

        //开始时间
        $("#createTimeStart").datetimepicker(start);
        // $("#startTime").datetimepicker(start);
        $("#createTimeEnd").datetimepicker(end);
        $("#endTime").datetimepicker(endNew)
    },
    initNewStartTime : function(){
        var now = new Date();
        return now.getFullYear()+'-'+(now.getMonth()+1<10?'0'+(now.getMonth()+1):now.getMonth()+1)+'-'+(now.getDate()<10?'0'+now.getDate():now.getDate())+' '+(now.getHours()<10?'0'+now.getHours():now.getHours())+':'+(now.getMinutes()<10?'0'+now.getMinutes():now.getMinutes())+':'+(now.getSeconds()<10?'0'+now.getSeconds():now.getSeconds());
    },
    newCreateProject : function () {
        edit = false;
        $('input[type="text"]').val('');

        $('.tis').text('将图片拖到此处或者点击上传按钮');
        var index = $('.table-active').index();
        if(index == 0){
            $('#homeConfig #token_show img').remove();
            $("#newProjectModal").modal({backdrop: 'static', keyboard: false});
            $('#startTime').val(innerFunc.initNewStartTime());
            $('#file_upload').val('');
        }else if(index == 1){
            $('#homeConfig .modal-box').modal('show');
            innderAjax('post',"/operarions/hcGe",{}/*JSON.stringify(paramter)*/,function (resp) {
                if (resp.code === '200') {
                    var html = '';
                    for(var key in resp.data){
                        html += '<option value="'+key+'">'+resp.data[key]+'</option>';
                    }
                    $('#select_user').html(html);
                } else {
                    $('.alertTip').slideDown();
                    $('.alertTip').text(resp.message);
                    setTimeout(function () {
                        $('.alertTip').slideUp();
                    }, 2000);
                }
            })
        }else{
            $('#homeConfig #token_show_gift img').remove();
            $('#gift_modal').modal('show');
            $('#file_upload_gift').val('');
        }
    },
    saveProject : function () {
        var index = $('.table-active').index(),url;
        var imgLen = $('#token_show img').length;
        var title = $('#title').val();
        var endTime = $('#endTime').val();
        if(!(imgLen && title && endTime)){
            $('.alertTip').slideDown();
            $('.alertTip').text('带红星项为必填项！');
            setTimeout(function () {
                $('.alertTip').slideUp();
            },2000);
            return
        }
        var formdata = new FormData();
        var startTime = new Date(($('#startTime').val()).replace(/-/g,"/"));
        var enddate = new Date(( $('#endTime').val()).replace(/-/g,"/"));
        if(startTime !=null && enddate!=null && enddate < startTime)
        {   layer.msg('结束时间不能早于开始时间');
            return
        }
        var addr = $.trim($('#toAddr').val());
        if(addr){
            var reg = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/;
            if(!reg.test(addr)){
                layer.msg('请，输入正确的网站地址！！');
                return
            }
            formdata.append("contentUrl",addr);
        }
        if(!edit){
            url = '/operarions/hcBannerGenerate';
            formdata.append("bannerPic",gloFiles);
        }else{
            url = "/operarions/hcBannerupdate";
            formdata.append("typeId",$('#saveProject').attr('pid'));
        }
        formdata.append("title",title);

        formdata.append("beginTime",$('#startTime').val());
        formdata.append("endTime",$('#endTime').val());
        // if(!($('#quanz').val())){
            formdata.append("weight",$('#quanz').val());
        // }
        $.ajax({
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            url : url,
            type : 'POST',
            dataType: 'json',
            data : formdata,
            cache: false,
            processData: false,
            contentType: false,
            success : function(responseStr) {
                if(responseStr.code == '200'){
                    $("#newProjectModal").modal('hide');
                    $('#success').slideDown();
                    $('#success').text(responseStr.message);
                    setTimeout(function () {
                        $('#success').slideUp();
                    },2000);
                    innerFunc.changeTab();
                    gloFiles = null;
                }else{
                    $('.alertTip').slideDown();
                    $('.alertTip').text(responseStr.message);
                    setTimeout(function () {
                        $('.alertTip').slideUp();
                    },2000);
                }
            }
        });
    },
    saveProjectNew: function(){
        var imgLen = $('#token_show_gift img').length;
        var title = $.trim($('#title_new').val());
        if(!(imgLen && title)){
            $('.alertTip').slideDown();
            $('.alertTip').text('带红星项为必填项！');
            setTimeout(function () {
                $('.alertTip').slideUp();
            },2000);
            return
        }
        var formdata = new FormData(),url;
        url = '/operarions/hcNewManPicGenerator';
        formdata.append("newManPic",gloFiles);
        formdata.append("title",title);
        $.ajax({
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            url : url,
            type : 'POST',
            dataType: 'json',
            data : formdata,
            cache: false,
            processData: false,
            contentType: false,
            success : function(responseStr) {
                if(responseStr.code == '200'){
                    $('#gift_modal').modal('hide');
                    $('#success').slideDown();
                    $('#success').text(responseStr.data);
                    setTimeout(function () {
                        $('#success').slideUp();
                    },2000);
                    innerFunc.changeTab();
                    gloFiles = null;
                }else{
                    $('.alertTip').slideDown();
                    $('.alertTip').text(responseStr.message);
                    setTimeout(function () {
                        $('.alertTip').slideUp();
                    },2000);
                }
            }
        });
    },
    update : function (e) {
        edit = true;
        $('input[type="text"]').val('');
        $('#homeConfig #token_show img').remove();
        $('.tis').text('将图片拖到此处或者点击上传按钮');
        var index = $('.table-active').index();
        var id = $(e.currentTarget).attr('pid');
        if(!index){
            $("#newProjectModal").modal({backdrop: 'static', keyboard: false});
            $('.confirmUserType').attr('tradeId',$(self).attr('tradeId'));
            innderAjax('post',"/operarions/hcBannerDetail",{typeId:id},function (resp) {
                if(resp.code === '200'){
                    $('#title').val(resp.data.title);
                    $('#startTime').val(resp.data.validityBeginTime);
                    $('#endTime').val(resp.data.validityEndTime);
                    $('#toAddr').val(resp.data.contentUrl);
                    $('#quanz').val(resp.data.weight);
                    var oImg=$("<img src='' width='46' height='46' />");
                    oImg.attr("src",resp.data.picUrl);
                    $("#homeConfig #token_show").append(oImg);
                    var op=$("#homeConfig #token_show p.tis");
                    $('#saveProject').attr('pid',id);
                    op.html("");
                }
            })

        }else{
            $('#homeConfig .modal-box').modal('show');
        }
        var self = this;

    },
    saveGe : function () {
        var paramter = {};
        paramter.geId = $('#select_user').val();
        innderAjax('post',"/operarions/hcGeGenerate",paramter,function (resp) {
            if(resp.code === '200'){
                $("#homeConfig .modal-box").modal('hide');
                $('#success').slideDown();
                $('#success').text(resp.message);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                innerFunc.changeTab()
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text(resp.message);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
            }
        })
    },
    pullOnOrOff : function (e,index) {
        $('.modal-single').modal('show');
        var id = $(e.currentTarget).attr('pid');
        if(index == 1){
            $('.modal-single .modal-body').text('确定上架吗？')
        }else{
            $('.modal-single .modal-body').text('确定下架吗？')
        }
        $('.modal-single .confirm').attr('flag',index).attr('pid',id);
    },
    confirm : function () {
        var index = $(this).attr('flag'),id = $(this).attr('pid');
        var flag = $('.table-active').index(),parameter = {},url;
        parameter.status = index;
        if(flag == 0){
            url = '/operarions/hcBannerUD';
            parameter.typeId = id;
        }else if(flag == 1){
            url = '/operarions/hcGeUD';
            parameter.geId = id;
        }else{
            url = '/operarions/hcNewManPicUD';
            parameter.hcNewManId = id;
        }
        innderAjax('post',url,parameter,function (resp) {
            if(resp.code == '200'){
                $('#success').slideDown();
                $('#success').text(resp.message);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                innerFunc.changeTab();
                $('.modal-single').modal('hide');
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text(resp.message);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
            }
        });
    },
    filUpload : function (e) {
        $(e.currentTarget).hasClass('btn-upload-new') ? $('#homeConfig #file_upload_gift').trigger('click'):
                                    $('#homeConfig #file_upload').trigger('click');
    },
    getFile : function(e){
        var $this = $(e.currentTarget);
        var fs = $this[0].files;
        if(!fs.length) return;
        var op= $this.siblings('div').find("p.tis");
        // file为前面获得的
        var _type=fs[0].type;
        if(_type.indexOf("image")!=-1){//判断他是不是图片文件
            $this.siblings('div').find('img').remove();
            gloFiles = fs[0];
            var fd=new FileReader();
            fd.readAsDataURL(fs[0]);
            fd.onload=function(){
                var oImg=$("<img src='' width='46' height='46' />");
                oImg.attr("src",this.result);
                $this.siblings('div').append(oImg);
                op.html("");
            }
        }else{
            layer.msg('请，上传图片文件！！');
        }
    },
    dragover: function (e) {
        e.preventDefault(); // 必须阻止默认事件
    },
    drop : function (e) {
        e.preventDefault(); // 阻止默认事件
        var $this = $(e.currentTarget);
        var op=$("p.tis");
        //获取拖过来的文件
        var fs=e.dataTransfer.files;
        // var len=fs.length; //获取文件个数
        // for(var i=0;i<len;i++){
            var _type=fs[0].type;
            if(_type.indexOf("image")!=-1){//判断他是不是图片文件
                $this.find('img').remove();
                // $('#homeConfig #token_show_gift img').remove();
                gloFiles = fs[0];
                var fd=new FileReader();
                fd.readAsDataURL(fs[0]);
                fd.onload=function(){
                    var oImg=$("<img src='' width='46' height='46' />");
                    oImg.attr("src",this.result);
                    $this.append(oImg);
                    op.html("");
                }
            }else{
                layer.msg('请，上传图片文件！！');
            }
        // }
    }
};

$('#homeConfig').on('click','#trandQuerybyServer',innerFunc.changeTab)//搜索表格
.on('click','.new-create',innerFunc.newCreateProject)//打开新建弹窗
.on('click','#saveProject',innerFunc.saveProject)//保存banner新建
.on('click','#saveProject_new',innerFunc.saveProjectNew)//保存新人见面礼新建
.on('click','.invest-tab span',function(){
    var self = this;
    if($(self).hasClass('table-active')){ return }
    $(self).addClass('table-active').siblings().removeClass('table-active');
    var index = $('.table-active').index();
    $('#upOrDown').val(1);
    if(index == 0){
        $('#iosOrAndroid').removeClass('dn');
        $('.stime').removeClass('dn');
        $('.etime').removeClass('dn');
    }else{
        if(!($('#iosOrAndroid').hasClass('dn'))){
            $('#iosOrAndroid').addClass('dn');
            $('.stime').addClass('dn');
            $('.etime').addClass('dn');
        }
    }/*else{
        $('#iosOrAndroid').removeClass('dn');
        if(!($('.stime').hasClass('dn'))){
            $('.stime').addClass('dn');
            $('.etime').addClass('dn');
        }
    }*/
    innerFunc.tableShow(index)
})//表格切换
.on('change','#upOrDown',innerFunc.changeTab)//选择上下架
.on('change','#iosOrAndroid',innerFunc.changeTab)//选择型号
.on('click','.btn-upload',innerFunc.filUpload)//点击上传
    .on('click','.btn-upload-new',innerFunc.filUpload)//点击上传
/*.on('dragover','#token_show',innerFunc.dragover,false)//拖拽图片事件
.on('drop','#token_show',innerFunc.drop,false)//拖拽图片事件*/
.on('change','#file_upload',innerFunc.getFile)//更换上传文件
    .on('change','#file_upload_gift',innerFunc.getFile)//更换上传文件
.on('click','.home-update',innerFunc.update)//点击修改
.on('click','.confirmUserType',innerFunc.saveGe)//点击保存体验金
.on('click','.pull-on',function (e){innerFunc.pullOnOrOff(e,1)})//点击上架
.on('click','.pull-off',function (e){innerFunc.pullOnOrOff(e,2)})//点击下架
.on('click','.confirm',innerFunc.confirm)//点击确定
.on('input','#quanz',function () {
    var reg = /^[1-9]\d*$/;
    if(!(reg.test(Number($.trim($('#quanz').val()))))){
        $('#quanz').val('')
    }
});
//找到要拖进去的目标元素
var oDiv=$("#token_show").get(0);
var pDiv=$("#token_show_gift").get(0);
// box为被拖放的区域
oDiv.addEventListener('dragover', innerFunc.dragover, false);
oDiv.addEventListener('drop', innerFunc.drop, false);

pDiv.addEventListener('dragover', innerFunc.dragover, false);
pDiv.addEventListener('drop', innerFunc.drop, false);
var initData = function(){

}
//入口
$(function () {
    innerFunc.initDate();
    innerFunc.tableShow();
});