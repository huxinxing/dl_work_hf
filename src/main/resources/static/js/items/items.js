var oTable;
var innerFunc = {
    tableShow:function () {
        oTable = $('#transFlowTable').DataTable({
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
            "sAjaxSource": "/statistics/FinancingProject",
            "bFilter": true,
            "bSort":false,//开启排序
            "bLengthChange": true,
            "bProcessing": true,
            "fnCreatedRow": function (nRow, aData, iDataIndex) {
                var opts='<a href="dataStatistics/itemDetail.html?id='+aData[0]+'&flag='+$('.user-active').index()+'" class="showDetail" tradeId="'+aData[0]+'" target="_blank">详情</a>';
                $('td:eq(8)', nRow).html(opts);
            },
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
        });
    },
    retrieveData:function( sSource, aoData, fnCallback ) {
        var param = {};
        param.flag = $('.user-active').index();
        param.beginTime = $('#createTimeStart').val();
        param.endTime = $('#createTimeStart').val();
        aoData.push( { "name": "beginTime", "value": $('input[name="createTimeStart"]').val()} );
        aoData.push( { "name": "endTime", "value": $('input[name="createTimeEnd"]').val()} );
        aoData.push( { "name": "flag", "value": $('.user-active').index()} );
        aoData.push( { "name": "financingUuid", "value": $('#walletAddr').val()} );
        aoData.push( { "name": "projectState", "value": $('select[name="paymentType"]').val()} );
        var enddate = new Date(($('input[name="createTimeEnd"]').val()).replace(/-/g,"/"));
        var startTime = new Date(( $('input[name="createTimeStart"]').val()).replace(/-/g,"/"));
        if(startTime !=null && enddate!=null && enddate < startTime)
        {   layer.msg('结束时间不能早于开始时间');
        $('#transFlowTable_processing').css('display','none');
            return false;
        }
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
            }
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
    }
}
$('.user-flag span').on('click',function(){
    var self = this;
    $(self).addClass('user-active').siblings().removeClass('user-active');
    oTable.ajax.reload();
});
$('#trandQuerybyServer').on('click',function(){
    oTable.ajax.reload();
});
$('#paymentType').on('change',function(){
    oTable.page('first').draw(false);
    return false;
})
$(function(){
    innerFunc.initDate();
    innerFunc.tableShow();
})