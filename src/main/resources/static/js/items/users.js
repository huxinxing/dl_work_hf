function search() {
    oTable.ajax.reload();
}

$(function(){
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
        "fnServerData": retrieveData,
        "autoWidth":false,
        "sAjaxSource": "/statistics/FinancingUser",
        "bFilter": true,
        "bLengthChange": true,
        "bProcessing": true,
        "bSort":true,//开启排序
        "aaSorting": [[ 6, "desc" ]],//指定默认排序
        "aoColumnDefs": [ { "bSortable": false, "aTargets": [0,1,2,3,4,5,8] }],//对其中某行不排序*/
        "fnCreatedRow": function (nRow, aData, iDataIndex) {
            var opts='<a href="dataStatistics/usersDetail.html?id='+aData[0]+'&step='+aData[2]+'" target="_blank" class="showAgents" tradeId="'+aData[0]+'">详情</a>';
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

    function retrieveData( sSource, aoData, fnCallback ) {
        aoData.push( { "name": "User", "value": $('input[name="walletAddr"]').val()} );
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
    }
});