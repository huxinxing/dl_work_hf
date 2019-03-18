var contadion=[],oTable,financingUuid,editFlag,uuid;
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
    tableShow:function () {
        oTable = $('#tableProject').DataTable({
            "bJQueryUI": true,
            "sDom": '<".example_processing"r><".tablelist"t><"#tableBottomWra"i>lp',
            "iDisplayStart":0,
            "iDisplayLength":10,
            "lengthMenu": [ 10, 15, 20, 25, 50, 100],
            "searching":false,
            "bDestroy":true,
            "bRetrieve":true,
            "bStateSave":false,
            "bServerSide": true,
            "fnServerData": innerFunc.retrieveData,
            "sAjaxSource": "/admin/financing/base/list",
            "bFilter": true,
            "bSort":false,
            "bLengthChange": true,
            "bProcessing": true,
            "fnCreatedRow": function (nRow, aData, iDataIndex) {
                var opts='<a uuid="'+aData[9]+'" class="editbtn linebtn" href="itemManageDetail.html?uuid='+aData[9]+'" target="_blank">详情</a>';
                var status = $('#financingStatus').val();
                opts+='<a style="margin-left: 15px;" href="javascript:void(0);" class="languageSet" dval="'+aData[9]+'" uuid="'+aData[9]+'">适配语言</a>';
                $('td:eq(9)', nRow).html(opts);
            },
            "oLanguage": {
                "sProcessing": "正在获取数据，请稍后...",
                "sLengthMenu": "每页显示 _MENU_ 条",
                "sZeroRecords": "目前还没有项目记录！",
                "sInfoEmpty": "记录数为0",
                "sInfoFiltered": "(全部记录数 _MAX_ 条)",
                "sInfo": "第 _START_ 到 _END_条 /共 _TOTAL_ 条",
                // "sSearch": "搜索 ",
                "oPaginate": {
                    "sFirst": "第一页",
                    "sPrevious": "上一页",
                    "sNext": "下一页",
                    "sLast": "最后一页"
                }
            },
            "initComplete": function(settings, json) {
            }
        });
    },
    searchData : function(){
        oTable.ajax.reload();
    },
    newProject:function () {
        $("#newProjectModal").modal({backdrop: 'static', keyboard: false});
        $('#newProjectModal input[type="checkbox"]')[0].checked = false;
        $('#newProjectModal input[type="checkbox"]')[1].checked = false;
        $('#newProjectModal input[type="text"]').val('');
        $('#newProjectModal input[type="file"]').val('');
        $('#newProjectModal textarea').val('');
        if(!($('#upload_usdx').hasClass('dn'))){
            $('#upload_usdx').addClass('dn');
        }
        if(!($('#upload_eth').hasClass('dn'))){
            $('#upload_eth').addClass('dn');
        }


        innderAjax('post',"/admin/financing/queryTagList",{},function (resp) {
            var html = '';
            $.each(resp.data,function (i,val) {
                html += '<span itemid="'+val.key+'">'+val.value+'</span>'
            });
            $('#item_tag').html(html);
            $('#item_tag span:first-child').addClass('item-active');
        })

        // $('#editProjectModal').modal('show');
    },
    saveProject:function () {
        var activityName = $.trim($('#itemName').val());
        var attentionEvent = $.trim($('#attentions').val());
        var moneyAmount = $.trim($('#amount').val());//理财金额
        var coinMinLimit = $.trim($('#minMoney').val());
        var maxMoney = $.trim($('#maxMoney').val());
        var yearRate = $.trim($('#yearRate').val());
        var userMinimumRate = $.trim($('#userMinimumRate').val());
        var backRate = $.trim($('#backRate').val());
        var subscriptionFee = $.trim($('#subscriptionFee').val());
        var subscriptionFeeRate = $.trim($('#subscriptionFeeRate').val());
        var unitNum = $.trim($('#unitNum').val());
        var url = $('#token_show').val();
        var urlETH = $('#token_show_ETH').val();
        var payType;
        var asa = $('input[type="checkbox"]:checked');
        var lenArr = asa.length,paymentType = [];//选币种lenArr
        if(lenArr){
            $.each(asa,function (i,val) {
                paymentType.push(val.value);
            });
            paymentType = paymentType.join(',');
        }
        if(!(activityName && attentionEvent && moneyAmount && yearRate && backRate && subscriptionFee && subscriptionFeeRate && lenArr && unitNum)){
            $('.alertTip').slideDown();
            $('.alertTip').text('带星号必须填选！');
            setTimeout(function () {
                $('.alertTip').slideUp();
            },2000);
            return
        }
        if(!(url||urlETH)){
            $('.alertTip').slideDown();
            $('.alertTip').text('带星号必须填选！');
            setTimeout(function () {
                $('.alertTip').slideUp();
            },2000);
            return
        }
        if(coinMinLimit&&maxMoney) {
            if (Number(coinMinLimit) >= Number(maxMoney)) {
                layer.msg('最小值不能大于最大值');
                return
            }
        }
        var formdata = new FormData();
        if(url){
            formdata.append("BCB",$('#file_upload')[0].files[0]);
        }
        if(urlETH){
            formdata.append("ETH",$('#file_upload_ETH')[0].files[0]);
        }
        formdata.append("tag",$('.item-active').attr('itemid'));
        formdata.append("Title",activityName);
        formdata.append("Attentions",attentionEvent);
        formdata.append("CoinType",$('#coinType').val());
        formdata.append("Amount",moneyAmount);
        formdata.append("CoinLimit",maxMoney);
        formdata.append("coinMinLimit",coinMinLimit);
        formdata.append("Freeze",$('#unitNum').val()+$('#frozenUnit').val());
        formdata.append("AnnualRate",yearRate);
        formdata.append("TotalRate",userMinimumRate);
        formdata.append("FoundationRate",backRate);
        formdata.append("SubscriptionFeeRate",subscriptionFee);
        formdata.append("PaymentType",paymentType);
        formdata.append("DiscountRate",subscriptionFeeRate);
        $.ajax({
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            url : "/admin/financing/add",
            type : 'POST',
            dataType: 'json',
            data : formdata,
            cache: false,
            processData: false,
            contentType: false,
            success : function(responseStr) {
                if(responseStr.status == 'ok'){
                    $("#newProjectModal").modal('hide');
                    oTable.ajax.reload();
                    $('#success').slideDown();
                    $('#success').text(responseStr.message);
                    setTimeout(function () {
                        $('#success').slideUp();
                    },2000);
                    $('#file_upload').replaceWith('<input type="file" class="form-control dn" id="file_upload" name="file_upload"/>');
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
    languageSet : function(e){
        var $this = $(e.currentTarget);
        $('#lan_detail input').val('');
        $('#lan_detail textarea').val('');
        $('#languageModal').modal('show');
        var uuid=$this.attr('uuid');
        $('#editLanguage').attr('uuid',uuid);
        if(uuid !=null && uuid !='') {
            innderAjax('post',"/admin/financing/EditLanager",{financingUuid:uuid},function (resp) {
                if(resp.status === 'ok'){
                    var lanType = resp.data.languageEnum,lanContent = resp.data.financingLanagerMessageDto,content,html = '',content = '';
                    $.each(lanType,function (i,val) {
                        if(i === 0){return};
                        html += '<li class="lan-li"><input type="checkbox" id="'+val.key+'"/><label for="'+val.key+'">'+val.value+'</label></li>';
                    });
                    $('#type_lan').html(html);
                    if(lanContent.length){
                        $.each(lanContent,function (i,val) {
                            if(i === 0){
                                $('#cn_title').text(val.title);
                                $('#cn_attention').text(val.attentions);
                            }else{
                                content += '<div>';
                                content += '<h5>'+val.lanagerType+'</h5>';
                                content += '<div class="form-group">';
                                content += '<label class="col-lg-2 control-label"><span class="red-sp">*</span>项目名称</label>';
                                content += '<div class="col-lg-10">';
                                content += '<input type="text" class="form-control activityName" placeholder="请输入'+val.lanagerType+'项目名称" value="'+val.title+'">';
                                content += '</div></div>';
                                content += '<div class="form-group"><label class="col-lg-2 control-label"><span class="red-sp">*</span>理财说明</label>';
                                content += '<div class="col-lg-10"><textarea name="attentions" class="form-control textarea-wysihtml5 attentionEvent" style="width: 100%; height: 100px" placeholder="请输入'+val.lanagerType+'理财说明">'+val.attentions+'</textarea></div></div></div>';
                                $('.lan-li label').each(function (i,sval) {
                                    if($(sval).text() === val.lanagerType){
                                        $(sval).siblings('input')[0].checked = true;
                                    }
                                })
                            }
                        })
                        $('#lan_detail').html(content);
                    }

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
                content += '<h5>'+text+'</h5>';
                content += '<div class="form-group">';
                content += '<label class="col-lg-2 control-label"><span class="red-sp">*</span>项目名称</label>';
                content += '<div class="col-lg-10">';
                content += '<input type="text" class="form-control activityName" placeholder="请输入'+text+'项目名称">';
                content += '</div></div>';
                content += '<div class="form-group"><label class="col-lg-2 control-label"><span class="red-sp">*</span>理财说明</label>';
                content += '<div class="col-lg-10"><textarea name="attentions" class="form-control textarea-wysihtml5 attentionEvent" style="width: 100%; height: 100px" placeholder="请输入'+text+'理财说明"></textarea></div></div></div>';
                $('#lan_detail').append(content);
            }
        }
    },
    editLanguage : function(e){
        var $this = $(e.currentTarget);
        var uid = $this.attr('uuid'),typeInput = $('#type_lan input');
        var len = $('#lan_detail h5').length,i,j,paramter = {},typeInputLen = typeInput.length;
        paramter.financingUuid = $this.attr('uuid');
        paramter.list = [];
        paramter.list.push({lan:'中文',title:$('#cn_title').text(),attention:$('#cn_attention').text()});
        for(i = 0 ; i < len ; i++){
            for(j = 0 ; j < typeInputLen ; j++){
                if(typeInput.eq(j)[0].checked && typeInput.eq(j).siblings().text() == $('#lan_detail h5').eq(i).text()){
                    var smallParam = {};
                    smallParam.lan = $('#lan_detail h5').eq(i).text();
                    smallParam.title = $.trim($('#lan_detail .activityName').eq(i).val());
                    if(!smallParam.title){
                        $('.alertTip').slideDown();
                        $('.alertTip').text('带星号必须填选！');
                        setTimeout(function () {
                            $('.alertTip').slideUp();
                        },2000);
                        return}
                    smallParam.attention = $.trim($('#lan_detail .attentionEvent').eq(i).val());
                    if(!smallParam.attention){
                        $('.alertTip').slideDown();
                        $('.alertTip').text('带星号必须填选！');
                        setTimeout(function () {
                            $('.alertTip').slideUp();
                        },2000);
                        return}
                    paramter.list.push(smallParam);
                }
            }

        }
        innderAjax('post',"/admin/financing/SaveFinancingLanager",{params:JSON.stringify(paramter)},function (resp) {
            if(resp.status === 'ok'){
                $('#languageModal').modal('hide');
                oTable.ajax.reload();
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
        });

    },
    uploadFile : function(e){
      $('#file_upload').trigger('click');
    },
    getFile : function(e){
        var $this = $(e.currentTarget);
        $('#token_show').val($this[0].files[0].name);
        $('#token_show').attr('title',$this[0].files[0].name);
    },
    uploadFileETH : function(e){
        $('#file_upload_ETH').trigger('click');
    },
    getFileETH : function(e){
        var $this = $(e.currentTarget);
        $('#token_show_ETH').val($this[0].files[0].name);
        $('#token_show_ETH').attr('title',$this[0].files[0].name);
    },
    retrieveData : function( sSource, aoData, fnCallback ) {
        var index = $('.table-active').index();
        if(index == 0){
            index = 1
        }else if(index == 1){index = 0}
        aoData.push( { "name": "status", "value":index} );
        aoData.push( { "name": "financing", "value":$('#financing').val()} );
        innderAjax('post',sSource,JSON.stringify(aoData),function (resp) {
            fnCallback(resp);
        },"application/json");
    }
};

//所有点击事件
$("#financing_detail")
    .on("click","#btn_search_1",innerFunc.searchData)
    .on("click","#newProject",innerFunc.newProject)
    .on("click",".btn-upload",innerFunc.uploadFile)
    .on("change","#file_upload",innerFunc.getFile)
    .on("click",".btn-upload-ETH",innerFunc.uploadFileETH)
    .on("change","#file_upload_ETH",innerFunc.getFileETH)
    .on("click","#saveProject",innerFunc.saveProject)
    .on('change','.backgroundWall',function () {
        var vals = $(this).val();
        $(this).attr('value',vals);
    })
.delegate('.languageSet','click',innerFunc.languageSet)
.delegate('#editLanguage','click',innerFunc.editLanguage)
.on('change','.lan-li input',innerFunc.checkChange)
.on('click','#item_tag span',function () {
    if($(this).hasClass('item-active')){return}
    $(this).addClass('item-active').siblings().removeClass('item-active')
}).on('change','.check_select',function () {
    if(this.id == 'usdx_coin'){
        $('#upload_usdx').toggleClass('dn')
    }else{
        $('#upload_eth').toggleClass('dn')
    }
    if(!($('#upload_usdx').hasClass('dn'))){
        $('#eth_label').html('');
    }else{
        $('#eth_label').html('<span class="red-sp">*</span>钱包地址');
    }
})
.on('click','.invest-tab span',function(){
    var self = this;
    if($(self).hasClass('table-active')){return}
    $(self).addClass('table-active').siblings().removeClass('table-active');
    oTable.page('first').draw(false);
    return false;
});//表格切换
//页面入口
$(function(){
    //初始化表格
    innerFunc.tableShow();
    $('.btn-group.bootstrap-select.show-tick').css('width','540px');
});