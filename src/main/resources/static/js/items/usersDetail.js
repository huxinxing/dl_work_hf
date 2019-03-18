var urlArr = window.location.search.slice(1).split('&'),id,step,showTitle = 0,showDialog = true;
var innerFunc = {
    tableShow:function (index) {
        var theadArr2 = ['时间','操作','下级代理ID（USDX）','订单ID','收益（BCB）','税费（BCB）','<span class="red-cl">*</span>应得代理收益(BCB)'];
        var theadArr3 = ['UID','昵称','等级','下级代理数','<span class="red-cl">*</span>总返佣(BCB)','操作'];
        var domId;
        if(index !== undefined){
            domId = 'transFlowTable'+(index+1);
            index === 1 ? innerFunc.initThead(theadArr2,domId):
                index === 2 || index === 3? innerFunc.initThead(theadArr3,domId):
                    '';
        }else{
            domId = 'transFlowTable1';
        }
        var url = index === undefined || index === 0 ? "/statistics/FinancingUserInvestmentIncome":
            index === 1 ? "/statistics/FinancingUserAgent":
                "/statistics/FinancingUserJunior";
        var bSort = false;
        var option1 = {};
        if(index === 2 || index ===3){
            bSort = true;
            option1.fnCreatedRow = function (nRow, aData, iDataIndex) {
                var opts='<a href="javascript:void(0);" class="showNextGrate" userId="'+aData[0]+'" grate="'+aData[2]+'">查看下级</a>';
                $('td:eq(5)', nRow).html(opts);
            };

        }else{
            bSort = false;
        }
        var option = {
            "bJQueryUI": true,
            "iDisplayStart":0,
            "iDisplayLength":10,
            "sDom":'<".example_processing"r><".tablelist"t><"#information"i>lp',
            "lengthMenu": [ 10, 15, 20, 25, 50, 100],
            "searching":false,
            "bDestroy":true,
            "bRetrieve":true,
            "bStateSave":false,
            "bServerSide": true,
            "fnServerData": innerFunc.retrieveData,
            "autoWidth":false,
            "sAjaxSource": url,
            "bFilter": true,
            "bSort":bSort,//开启排序
            "aaSorting": [[ 4, "desc" ]],//指定默认排序
            "aoColumnDefs": [ { "bSortable": false, "aTargets": [0,1,2,3,5] }],//对其中某行不排序*/
            "bLengthChange": true,
            "bProcessing": true,
            "bDestroy":true,
            "oLanguage": {
                "sProcessing": "正在获取数据，请稍后...",
                "sLengthMenu": "每页显示 _MENU_ 条",
                "sZeroRecords": "目前没有数据！",
                "sInfoEmpty": "记录数为0",
                "sInfoFiltered": "(全部记录数 _MAX_ 条)",
                "sSearch": "搜索 ",
                "sInfo": "第 _START_ 到 _END_条 /共 _TOTAL_ 条",
                "oPaginate": {
                    "sFirst": "第一页",
                    "sPrevious": "上一页",
                    "sNext": "下一页",
                    "sLast": "最后一页"
                }
            },
            "initComplete": function(settings, json) {}
        };
        $.extend(option, option1);
        oTable = $('#'+domId).DataTable(option);
        // oTable.fnClearTable(false);
        $('#transFlowTable'+(index+1)+'_wrapper').removeClass('dn').siblings().addClass('dn');
        $('#transFlowTable'+(index+1)).removeClass('dn');
    },
    retrieveData:function( sSource, aoData, fnCallback ) {
        var index = $('.table-active').index();
        var enddate = new Date(($('input[name="createTimeEnd"]').val()).replace(/-/g,"/"));
        var startTime = new Date(( $('input[name="createTimeStart"]').val()).replace(/-/g,"/"));
        if(startTime !=null && enddate!=null && enddate < startTime)
        {   layer.msg('结束时间不能早于开始时间');
            return false;
        }
        aoData.push( { "name": "UserId", "value": id} );
        aoData.push( { "name": "step", "value": step} );
        if(index !== 2){
            aoData.push( { "name": "beginTime", "value": $('input[name="createTimeStart"]').val()} );
            aoData.push( { "name": "endTime", "value": $('input[name="createTimeEnd"]').val()} );
        }else{
            aoData.push( { "name": "Junior", "value": $('#walletAddr').val()} );
            $.each(aoData,function (i,val) {
                if(val.name === 'iSortCol_0'){
                    val.value = 6;
                }
            });
        }

        if(showTitle === 0){
            // $('.single-title').text(step);
        }else if(showTitle === 1 || showTitle === 2){
            var html = '<span>></span><span userId="'+id+'" class="return-back">'+step+'</span>';
            $('.com-title').append(html);
            if(showTitle === 1){
                $('.com-title').html($('.com-title').html().substring(17,$('.com-title').html().length));
                // $('.return-back').addClass('color-black');
            }
        }else{

        }

        // aoData.push( { "name": "flag", "value": $('.user-active').index()} );
        $.ajax( {
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            "type": "post",
            "contentType": "application/json",
            "url": sSource,
            "dataType": "json",
            "data": JSON.stringify(aoData),
            "success": function(resp) {
                fnCallback(resp);
                if(sSource =="/statistics/FinancingUserAgent"){
                    $('.item-amount-total').text(resp.InComeTotal);
                }
                if(sSource =="/statistics/FinancingUserInvestmentIncome"){
                    $('.income-to').text(resp.InComeUSDX);
                    $('.invest-money').text(resp.InvestmentUSDX);
                    $('.item-amount').text(resp.projectNum);
                }


                var index = $('.table-active').index();
                $('#transFlowTable'+(index+1)+'_wrapper').removeClass('dn').siblings().addClass('dn');
                $('#transFlowTable'+(index+1)).removeClass('dn');
            }
        });
    },
    urlSplit:function(){
        $.each(urlArr,function (i,val) {
            var single = val.split('=');
            if(single[0] === 'id'){
                id = single[1];
            }
            if(single[0] === 'step'){
                step = single[1];
            }
        })
    },
    initThead:function (theadArr,domId) {
        var html = '<tr>';
        $.each(theadArr,function (i,val) {
            html += '<th>'+val+'</th>';
        })
        html += '</tr>';
        $('#'+domId+' thead').html(html);
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
function initData(){
    var data = {
        // flag:$('.user-active').index(),
        step:step,
        UserId:id
    };
    $.ajax({
        "headers": {
            "Authorization":localStorage.getItem("loginToken")
        },
        "type": "post",
        "url" : "/statistics/FinancingUserMessage",
        "dataType": "json",
        "data": data,
        success : function (rep) {
            // innerFunc.tableShow();
            $('.user-id').text(rep.data.userId);
            $('.user-name').text(rep.data.disPlayName);
            $('.user-address').html(rep.data.address.join('<br>'));
            $('.user-level').text(rep.data.grade);
            $('.user-rate').text(rep.data.returnPointColumn);
        }
    })
}
$('.invest-tab span').on('click',function(){
    var self = this;
    var index = $(self).index();
    if(index !== 2){
        $('#createTimeStart').removeClass('dn');
        $('#createTimeEnd').removeClass('dn');
        $('#walletAddr').addClass('dn');
        if(index === 0){
            $('.tab-left-1').removeClass('dn');
            $('.tab-left-2').addClass('dn');

        }else{
            $('.tab-left-2').removeClass('dn');
            $('.tab-left-1').addClass('dn');
        }
    }else{
        showTab = false;
        $('#createTimeStart').addClass('dn');
        $('#createTimeEnd').addClass('dn');
        $('#walletAddr').removeClass('dn');
        $('.tab-left-1').addClass('dn');
        $('.tab-left-2').addClass('dn');
    }

    $(self).addClass('table-active').siblings().removeClass('table-active');
    innerFunc.tableShow(index);
});
$('#trandQuerybyServer').on('click',function () {
    oTable.ajax.reload()
});
$('#transFlowTable3').on('click','.showNextGrate',function(){

    showTitle = 1;
    id = $(this).attr('userId');
    step = $(this).attr('grate');

    $('#agentsModal').modal('show');
    showDialog ? innerFunc.tableShow(3):
                    oTable.ajax.reload();
    showDialog = false;
});
$('#transFlowTable4').on('click','.showNextGrate',function(){
    showTitle = 2;
    id = $(this).attr('userId');
    step = $(this).attr('grate');
    oTable.ajax.reload();

});
$('#agentsModal').on('hide.bs.modal', function () {
    $('.com-title').html('');
});
$(document).on('click','.return-back',function () {
    showTitle = 3;
    var self = this;
    if($(self).index() === $('.return-back').length){
        return
    }
    id = $(self).attr('userId');
    step = $(self).text();
    oTable.ajax.reload();
    $(self).nextAll().remove();
    // $('.com-title').html($('.com-title').html().substring(0,$('.com-title').html().length-1))
    // $(self).addClass('color-black');
});
$(function () {
    innerFunc.urlSplit();
    innerFunc.initDate();
    initData();
    innerFunc.tableShow();
});