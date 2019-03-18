var edit = false;
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

    tableShow : function(){
        layui.use('table', function(){
            table = layui.table;
            //方法级渲染
            table.render({
                elem: '#transFlowTable'
                ,method:'post'
                ,where:{ccqType: $('#selectType').val()}
                ,url: '/operarions/ccqlist'
                ,headers:{
                        "Authorization":localStorage.getItem("loginToken")
                    }
                ,cols: [[
                    // {checkbox: true, fixed: true}
                    {field:'ccqId', title: 'ID',minWidth:50,templet:'<div><span title="{{d.ccqId}}">{{d.ccqId}}</span></div>'}
                    ,{field:'ccqType', title: '分类',minWidth:50,templet:'<div><span title="{{d.ccqType}}">{{d.ccqType}}</span></div>'}
                    ,{field:'ccqTitle', title: '问题',minWidth:100,templet:'<div><span title="{{d.ccqTitle}}">{{d.ccqTitle}}</span></div>'}
                    ,{field:'ccqQuestion', title: '回答',minWidth:100,templet:'<div><span title="{{d.ccqQuestion}}">{{d.ccqQuestion}}</span></div>'}
                    ,{field:'ccqWeight', title: '权重',minWidth:50,templet:'<div><span title="{{d.ccqWeight}}">{{d.ccqWeight}}</span></div>'}
                    ,{field:'ccqCreateTime', title: '创建时间',minWidth:100,templet:'<div><span title="{{d.ccqCreateTime}}">{{d.ccqCreateTime}}</span></div>'}
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
    newCreateProject : function () {
        edit = false;
        $('input[type="text"]').val('');
        $('textarea').val('');
        $('#item_pro span').removeClass('item-active');
        $('#item_pro span:first-child').addClass('item-active');
        $("#newProjectModal").modal({backdrop: 'static', keyboard: false});
    },
    saveProject : function () {

        var paramter = {},url;
        var problem = $('#pro_pro').val();
        var answer = $.trim($('#pro_answer').val());
        var weight =  $.trim($('#weight_pro').val());
        if(!(answer && problem)){
            $('.alertTip').slideDown();
            $('.alertTip').text('带星号必须填选！');
            setTimeout(function () {
                $('.alertTip').slideUp();
            },2000);
            return
        }
        if(edit){
            paramter.ccqId = $('#saveProject').attr('targetId');
            url = "/operarions/ccqUpdate";
        }else{
            url = "/operarions/ccqGenerator";
        }
        paramter.ccqType = Number($('.item-active').attr('itemid'));
        paramter.ccqTitle = problem;
        paramter.ccqQuesttion = answer;
        paramter.ccqWeight = Number(weight);
        innderAjax('post',url,paramter,function (resp) {
            if(resp.code === '200'){
                $('#success').text(resp.data);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                table.reload('transFlowTable', {
                    where:{ccqType: $('#selectType').val()}
                });
                $("#newProjectModal").modal('hide');
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text(resp.data);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
            }
        })
    },
    updateProject : function (e) {
        edit = true;
        $('input[type="text"]').val('');
        $('textarea').val('');
        $('#item_pro span').removeClass('item-active');
        $("#newProjectModal").modal({backdrop: 'static', keyboard: false});
        innderAjax('post',"/operarions/ccqDetail",{ccqId:$(e.currentTarget).attr('tradeId')},function (resp) {
            if(resp.code === '200'){
                var proItem = $('#item_pro span');
                var i,len = proItem.length;
                for(i = 0 ; i < len;i++){
                    if(resp.data.ccqType == proItem.eq(i).attr('itemid')){
                        proItem.eq(i).addClass('item-active');
                        break;
                    }
                }
                $('#pro_pro').val(resp.data.ccqTitle);
                $('#pro_answer').val(resp.data.ccqQuestion);
                $('#weight_pro').val(resp.data.ccqWeight);
                $('#saveProject').attr('targetId',resp.data.ccqId);
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text(resp.message);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
            }
        })
    },
    lanConfig : function () {
        $('#language_pro').modal('show');
    },
    delete : function (e) {
        $('#myModal').modal('show');
        $('.delete-confirm').attr('tradeId',$(e.currentTarget).attr('tradeId'));
    },
    deleteConfirm : function (e) {
        innderAjax('post',"/operarions/ccqDelete",{ccqId:$(e.currentTarget).attr('tradeId')},function (resp) {
            if(resp.code === '200'){
                $("#myModal").modal('hide');
                $('#success').slideDown();
                $('#success').text(resp.data);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                table.reload('transFlowTable', {
                    where:{ccqType: $('#selectType').val()}
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
    languageSet : function(e){
        var $this = $(e.currentTarget);
        $('#lan_detail input').val('');
        $('#lan_detail textarea').val('');
        $('#language_pro').modal('show');
        var uuid=$this.attr('tradeId');
        $('#editLanguage').attr('tradeId',uuid);
        if(uuid !=null && uuid !='') {
            innderAjax('post',"/operarions/ccqLangList",{ccqId:uuid},function (resp) {
                if(resp.data.length){
                    var content = '';
                    $.each(resp.data,function (i,val) {
                        if(i === 0){
                            $('#cn_title').text(val.ccqTitle);
                            $('#cn_attention').text(val.ccqQuestion);
                            $('.cn-show h5').attr('lan',val.ccqLang).attr('tId',val.ccqLangAscription)
                        }else{
                            content += '<div>';
                            content += '<h5 lan="'+val.ccqLang+'">'+val.ccqLangCN+'</h5>';
                            content += '<div class="form-group">';
                            content += '<label class="col-lg-2 control-label"><span class="red-sp">*</span>问题</label>';
                            content += '<div class="col-lg-10">';
                            content += '<input type="text" class="form-control activityName" placeholder="请输入'+val.ccqLangCN+'问题" value="'+val.ccqTitle+'">';
                            content += '</div></div>';
                            content += '<div class="form-group"><label class="col-lg-2 control-label"><span class="red-sp">*</span>回答</label>';
                            content += '<div class="col-lg-10"><textarea name="attentions" class="form-control textarea-wysihtml5 attentionEvent" style="width: 100%; height: 100px" placeholder="请输入'+val.ccqLangCN+'回答">'+val.ccqQuestion+'</textarea></div></div></div>';
                            $('.lan-li label').each(function (i,sval) {
                                if($(sval).text() === val.ccqLangCN){
                                    $(sval).siblings('input')[0].checked = true;
                                }
                            })
                        }
                    });
                    $('#lan_detail').html(content);
                }
            });


        }
    },
    checkChange : function(e){
        var self = e.currentTarget;
        var text = $(self).siblings('label').text();
        var sinArr = $('#lan_detail h5');
        var i,len = sinArr.length;

        var textStr = sinArr.text();
        if(textStr.indexOf(text)!= -1){
            for(i= 0; i < len; i++){
                if(sinArr.eq(i).text() === text){
                    self.checked ? sinArr.eq(i).parent().show()
                        :sinArr.eq(i).parent().hide();
                    break
                }
            }
        }else{
            var content = '';
            if(self.checked){
                content += '<div>';
                content += '<h5 lan="'+self.id+'">'+text+'</h5>';
                content += '<div class="form-group">';
                content += '<label class="col-lg-2 control-label"><span class="red-sp">*</span>问题</label>';
                content += '<div class="col-lg-10">';
                content += '<input type="text" class="form-control activityName" placeholder="请输入'+text+'问题">';
                content += '</div></div>';
                content += '<div class="form-group"><label class="col-lg-2 control-label"><span class="red-sp">*</span>答案</label>';
                content += '<div class="col-lg-10"><textarea name="attentions" class="form-control textarea-wysihtml5 attentionEvent" style="width: 100%; height: 100px" placeholder="请输入'+text+'答案"></textarea></div></div></div>';
                $('#lan_detail').append(content);
            }
        }
    },
    editLanguage : function(e){
        var $this = $(e.currentTarget);
        var uid = $this.attr('tradeId'),typeInput = $('#type_lan input');
        var len = $('#lan_detail h5').length,i,j,paramter = [],typeInputLen = typeInput.length;
        paramter = [{ccqLang:$('.cn-show h5').attr('lan'),ccqLangAscription:$('.cn-show h5').attr('tid'),ccqTitle:$('#cn_title').text(),ccqQuestion:$('#cn_attention').text()}];
        for(i = 0 ; i < len ; i++){
            for(j = 0 ; j < typeInputLen ; j++){
                if(typeInput.eq(j)[0].checked && typeInput.eq(j).siblings().text() == $('#lan_detail h5').eq(i).text()){
                    var smallParam = {};
                    smallParam.ccqLang = ($('#lan_detail h5').eq(i).attr('lan')).toLowerCase();
                    smallParam.ccqLangAscription = $('.cn-show h5').attr('tid');
                    smallParam.ccqTitle = $.trim($('#lan_detail .activityName').eq(i).val());
                    if(!smallParam.ccqTitle){
                        $('.alertTip').slideDown();
                        $('.alertTip').text('带星号必须填选！');
                        setTimeout(function () {
                            $('.alertTip').slideUp();
                        },2000);
                        return}
                    smallParam.ccqQuestion = $.trim($('#lan_detail .attentionEvent').eq(i).val());
                    if(!smallParam.ccqQuestion){
                        $('.alertTip').slideDown();
                        $('.alertTip').text('带星号必须填选！');
                        setTimeout(function () {
                            $('.alertTip').slideUp();
                        },2000);
                        return}
                    paramter.push(smallParam);
                }
            }

        }
        innderAjax('post',"/operarions/ccqUpateLang", JSON.stringify(paramter),function (resp) {
            if(resp.code === '200'){
                $('#language_pro').modal('hide');
                $('#success').slideDown();
                $('#success').text(resp.data);
                setTimeout(function () {
                    $('#success').slideUp();
                },2000);
                table.reload('transFlowTable', {
                    where:{ccqType: $('#selectType').val()}
                });
            }else{
                $('.alertTip').slideDown();
                $('.alertTip').text(resp.data);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                },2000);
            }
        },'application/json');

    }
};
$('#problemConfig').on('click','.new-create',innerFunc.newCreateProject)//打开新建弹窗
.on('click','#saveProject',innerFunc.saveProject)//保存新建
.on('click','.update-problem',innerFunc.updateProject)//点击修改
.on('click','.lan-config',innerFunc.lanConfig)//点击多语言配置
.delegate('.lan-config','click',innerFunc.languageSet)
.delegate('#editLanguage','click',innerFunc.editLanguage)
.on('change','.lan-li input',innerFunc.checkChange)
.on('click','.delete-problem',innerFunc.delete)//点击删除
.on('click','.delete-confirm',innerFunc.deleteConfirm)//点击删除确定
.on('change','#selectType',function () {
    table.reload('transFlowTable', {
        where:{ccqType: $('#selectType').val()}
    });
})//点击切换分类
.on('click','#item_pro span',function () {
    if($(this).hasClass('item-active')){return}
    $(this).addClass('item-active').siblings().removeClass('item-active')
})
.on('input','#weight_pro',function () {
    var reg = /^[1-9]\d*$/;
    if(!(reg.test(Number($.trim($('#weight_pro').val()))))){
        $('#weight_pro').val('')
    }
});
var initData = function(){
    innderAjax('post',"/operarions/ccqType",{},function (resp) {
        if(resp.code === '200'){
            var html = '';
            for(var key in resp.data){
                html += '<span itemid="'+key+'">'+resp.data[key]+'</span>'
            }
            $('#item_pro').html(html);
            $('#item_pro span:first-child').addClass('item-active');
        }else{
            $('.alertTip').slideDown();
            $('.alertTip').text(resp.data);
            setTimeout(function () {
                $('.alertTip').slideUp();
            },2000);
        }
    })
    innderAjax('post',"/operarions/ccqLangClassification",{},function (resp) {
        if(resp.code === '200'){
            var html = '';
            for(var i in resp.data){
                if(i == 'CN'){continue};
                html += '<li class="lan-li"><input type="checkbox" id="'+i+'"/><label for="'+i+'">'+resp.data[i]+'</label></li>';
            }
            $('#type_lan').html(html);
        }
    });
};
//入口
$(function () {
    innerFunc.tableShow();
    initData();
});