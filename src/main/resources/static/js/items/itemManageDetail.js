var id,showBox = true;//
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
    getDetail:function () {
        innderAjax('post',"/admin/financing/queryTagList",{},function (resp) {
            var html = '';
            $.each(resp.data,function (i,val) {
                html += '<span itemid="'+val.key+'">'+val.value+'</span>'
            });
            $('#item_tag').html(html);
            innderAjax('post',"/admin/financing/get",{financingUuid:id},function (data) {
                var respList = data.data.financingLanagerMessageList;
                var html = '';
                if(respList.length){
                    $.each(respList,function (i,val) {
                        html += '<div class="fl mrr-45">';
                        html += '<h5>'+val.lanagerType+'</h5><div class="form-group">';
                        html += '<input type="text" class="form-control activityName" placeholder="请输入'+val.lanagerType+'项目名称" value="'+val.title+'"></div><div class="form-group">';
                        html += '<textarea class="form-control textarea-wysihtml5 attentionEvent" style="width: 100%; height: 100px" placeholder="请输入'+val.lanagerType+'理财说明">'+val.attentions+'</textarea></div></div>'
                    });
                    $('#lan_detail').html(html);
                }

                // financingUuid =data.data.financingUuid;
                $('#projectNumber').val(data.data.serialNum);//项目编号
                $('#amount').val(data.data.amount);//理财金额
                $('#coinLimit').val(data.data.coinLimit);//单笔最大限额
                $('#minMoney').val(data.data.coinMinLimit);//单笔最大限额
                if('BCB' in data.data.token){
                    $('#token_show').val(data.data.token.BCB);//钱包地址
                    $('#token_show').attr('title',data.data.BCB);//钱包地址
                }
                if('ETH' in data.data.token){
                    $('#token_show_ETH').val(data.data.token.ETH);//钱包地址
                    $('#token_show_ETH').attr('title',data.data.ETH);//钱包地址
                }
                $('#freezeNumber').val(data.data.freezeNumber);//理财期限
                $('#freezeUnit').val(data.data.freezeUnit);//理财期限
                $('#AnnualRate').val(data.data.annualRate);//年收益
                // $('#TotalRate').val(data.data.totalRate);//用户保底收益
                $('#FoundationRate').val(data.data.foundationRate);//超额收益基金公司返点比例
                $('#subscriptionFee').val(data.data.subscriptionFeeRate);//认购费
                $('#DiscountRate').val(data.data.discountRate);//认购费折扣
                var tagsArr = $('#item_tag span'),k;
                var tagsArrLen = tagsArr.length;
                if(data.data.tag){
                    for (k = 0;k < tagsArrLen;k++){
                        if(tagsArr.eq(k).attr('itemid') == data.data.tag){
                            tagsArr.eq(k).addClass('item-active');
                        }
                    }
                }
                var status = data.data.status;
                $('.status').text(
                    status === 0 ? '待开启':
                        status === 1 ? '募集中':
                            status === 2 ? '募集暂停':
                                '募集结束'
                ).attr('status',status);

                $('.publish-time').text(data.data.createTime);
                var paymentTypeArr = data.data.paymentType.split(','),labelArr = $('.langague-type label');
                var len1 = paymentTypeArr.length,i,j,len2 = labelArr.length;
                for(i = 0 ; i < len1 ; i++){
                    for (j = 0 ; j < len2 ; j++){
                        if(paymentTypeArr[i] == labelArr.eq(j).text()){
                            labelArr.eq(j).prev()[0].checked = true;
                            if(paymentTypeArr[i] == 'USDX'){
                                $('#upload_usdx').removeClass('dn')
                            }else{
                                $('#upload_eth').removeClass('dn')
                            }
                        }
                    }
                }
                $('#langague-type input[type="checkbox"]');
                if(status === 0 || status === 2){
                    $('.btn-stop').addClass('dn')
                }else if(status === 1){
                    $('.btn-start').addClass('dn')
                }else{
                    $('.btn-start').addClass('dn');
                    $('.btn-stop').addClass('dn');
                    $('.btn-over').addClass('dn');
                }
            });
        });

        // var resp = {"financingUuid":"18061738","list":[{"lan":"英文","title":"11","attention":"11"}]};



        $("#editProjectModal").modal({backdrop: 'static', keyboard: false});
        // $('#editProjectModal').modal('show');
    },
    saveDetail:function () {

        var moneyAmount = $.trim($('#amount').val());//理财金额
        var maxMoney = $.trim($('#coinLimit').val());
        var yearRate = $.trim($('#AnnualRate').val());
        // var userMinimumRate = $.trim($('#TotalRate').val());
        var backRate = $.trim($('#FoundationRate').val());
        var subscriptionFee = $.trim($('#subscriptionFee').val());
        var subscriptionFeeRate = $.trim($('#DiscountRate').val());
        var unitNum = $.trim($('#freezeNumber').val());
        var url = $('#token_show').val();
        var urlETH = $('#token_show_ETH').val();
        var payType;
        var asa = $('input[type="checkbox"]:checked');
        var lenArr = asa.length,paymentType = [];//选币种lenArr
        var minMoney = $('#minMoney').val();
        if(lenArr){
            $.each(asa,function (i,val) {
                paymentType.push(val.value);
            });
            paymentType = paymentType.join(',');
        }
        var len = $('#lan_detail h5').length,i,paramter = {};
        paramter.financingUuid = id;
        paramter.status  = $('.status').attr('status');
        paramter.CoinType  = $('#coinType').val();
        paramter.Amount  = moneyAmount;
        paramter.CoinLimit  = maxMoney;
        paramter.Freeze  = $('#freezeNumber').val()+$('#freezeUnit').val();
        paramter.AnnualRate  = yearRate;
        // paramter.TotalRate  = userMinimumRate;
        paramter.coinMinLimit = minMoney;
        paramter.FoundationRate  = backRate;
        paramter.SubscriptionFeeRate  = subscriptionFee;
        paramter.paymentType  = paymentType;
        paramter.DiscountRate  = subscriptionFeeRate;
        paramter.SubscriptionFeeRate  = subscriptionFee;
        paramter.tag  = $('.item-active').attr('itemid');
        paramter.list = [];
        var num = 0;
        for(i = 0 ; i < len ; i++){
            var smallParam = {};
            smallParam.lan = $('#lan_detail h5').eq(i).text();
            smallParam.title = $.trim($('#lan_detail .activityName').eq(i).val());
            smallParam.attention = $.trim($('#lan_detail .attentionEvent').eq(i).val());
            if(smallParam.title || smallParam.attention){
                paramter.list.push(smallParam);

            }
        }
        var cnName = $.trim($('#lan_detail .mrr-45:first-child .activityName').val());
        var cnAttention = $.trim($('#lan_detail .mrr-45:first-child .attentionEvent').val());
        if(!(cnAttention && cnName && moneyAmount && yearRate && backRate && subscriptionFee && subscriptionFeeRate && lenArr && unitNum)){
            $('.alertTip').slideDown();
            $('.alertTip').text('中文项目名称、理财说明及带星号必须填选！');
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
        if(minMoney&&maxMoney){
            if(Number(minMoney)>=Number(maxMoney)){
                layer.msg('最小值不能大于最大值');
                return
            }
        }
        var formdata = new FormData();
        formdata.append("params",JSON.stringify(paramter));
        if($('#file_upload')[0].files[0]){
            formdata.append("BCB",$('#file_upload')[0].files[0]);
        }

        if($('#file_upload_ETH')[0].files[0]){
            formdata.append("ETH",$('#file_upload_ETH')[0].files[0]);
        }
        /*formdata.append("status",$('.status').attr('status'));
        formdata.append("CoinType",$('#coinType').val());
        formdata.append("Amount",moneyAmount);
        formdata.append("CoinLimit",maxMoney);
        formdata.append("Freeze",$('#freezeNumber').val()+$('#freezeUnit').val());
        formdata.append("AnnualRate",yearRate);
        formdata.append("TotalRate",userMinimumRate);
        formdata.append("FoundationRate",backRate);
        formdata.append("SubscriptionFeeRate",subscriptionFee);
        formdata.append("PaymentType",paymentType);
        formdata.append("DiscountRate",subscriptionFeeRate);*/
        $.ajax({
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            url : "/admin/financing/status/update",
            type : 'POST',
            dataType: 'json',
            data : formdata,
            cache: false,
            processData: false,
            contentType: false,
            success : function(responseStr) {
                if(responseStr.status == 'ok'){
                    $("#newProjectModal").modal('hide');
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
    urlSplit:function(){
        var urlArr = window.location.search.slice(1).split('&');
        $.each(urlArr,function (i,val) {
            var single = val.split('=');
            if(single[0] === 'uuid'){
                id = single[1];
            }
        })
    },
    uploadFile : function(){
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
    btnStopStartOver : function () {
        $('#myModal').modal('show');
        var status = $('.status').attr('status'),text;
        if(status == 0 || status == 2){
            text = '确定要开启此项目吗？';
        }else if(status == 1){
            text = '确定要暂停此项目吗？';
        }else{
            text = '确定要结束此项目吗？';
        }
        $('.modal-body').text(text);
        $('.confirm').attr('endItem',false);
        $('.confirm').attr('status',status);
        showBox = false;

    },
    btnOver : function(){
        $('#myModal').modal('show');
        $('.modal-body').text('确定要结束此项目吗？');
        $('.confirm').attr('endItem',true);
        showBox = false;
    },
    btnSave : function(){
        if(showBox){
            $('#myModal').modal('show');
            $('.modal-body').text('确定要保存编辑内容吗？');
        }else {
            innerFunc.saveDetail()
        }
    },
    confirm : function () {
        if(showBox){innerFunc.saveDetail(); $('#myModal').modal('hide');return}
        var status = $('.status').attr('status');
        if($('.confirm').attr('endItem') === 'false'){
            if(status == 0){
                status = 1;
                $('.status').text('募集中');
                $('.btn-start').addClass('dn');
                $('.btn-stop').removeClass('dn')
            }else if(status == 1){
                status = 2;
                $('.status').text('募集暂停');
                $('.btn-stop').addClass('dn');
                $('.btn-start').removeClass('dn')
            }else if(status == 2){
                status = 1;
                $('.status').text('募集中');
                $('.btn-start').addClass('dn');
                $('.btn-stop').removeClass('dn');
            }
        }else{
            status = 3;
            $('.status').text('募集结束');
            $('.btn-stop').addClass('dn');
            $('.btn-start').addClass('dn');
            $('.btn-over').addClass('dn');
        }
        $('.status').attr('status',status);
        innerFunc.saveDetail();
        $('#myModal').modal('hide');
    }
};

$(document).on("click",".btn-upload",innerFunc.uploadFile)
    .on("change","#file_upload",innerFunc.getFile)
    .on("click",".btn-upload-ETH",innerFunc.uploadFileETH)
    .on("change","#file_upload_ETH",innerFunc.getFileETH)
    .on("click",".btn-stop",innerFunc.btnStopStartOver)
    .on("click",".btn-start",innerFunc.btnStopStartOver)
    .on("click",".btn-over",innerFunc.btnOver)
    .on("click",".confirm",innerFunc.confirm)
    .on("click",".btn-save",innerFunc.btnSave)
    .on('click','#item_tag span',function () {
    if($(this).hasClass('item-active')){return}
    $(this).addClass('item-active').siblings().removeClass('item-active')
}).on('change','.check_select',function () {
    if(this.id == 'usdx_coin'){
        $('#upload_usdx').toggleClass('dn')
    }else{
        $('#upload_eth').toggleClass('dn')
    }
});
$(function () {
    innerFunc.urlSplit();
    innerFunc.getDetail();
});