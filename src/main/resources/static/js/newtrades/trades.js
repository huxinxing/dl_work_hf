$(function(){
    oTable = $('.data-table').DataTable({
        "bJQueryUI": true,"sDom": '<".example_processing"r><".tablelist"t><"F"<"#filteropt">flp>',
        "iDisplayStart":0,"iDisplayLength":10,
        "lengthMenu": [ 10, 15, 20, 25, 50, 100],
        "bDestroy":true,"bRetrieve":true,"bStateSave":true,
        "bServerSide": true,"fnServerData": retrieveData,
        "sAjaxSource": "/admin/trade/list",
        "bFilter": true,"bLengthChange": true,"bProcessing": true,
        "fnCreatedRow": function (nRow, aData, iDataIndex) {

        },
        "oLanguage": {
            "sProcessing": "正在获取数据，请稍后...",
            "sLengthMenu": "每页显示 _MENU_ 条",
            "sZeroRecords": "目前还没有交易记录！",
            "sInfoEmpty": "记录数为0",
            "sInfoFiltered": "(全部记录数 _MAX_ 条)",
            "sSearch": "搜索 ",
            "oPaginate": {
                "sFirst": "第一页",
                "sPrevious": "上一页",
                "sNext": "下一页",
                "sLast": "最后一页"
            }
        },
        "initComplete": function(settings, json) {
            var tabs='<a href="#" class="stalist curr" order-status="0">理财已确认</a>';
            tabs+='<a href="#" class="stalist" order-status="1">理财已成功</a>';
            tabs+='<a href="#" class="stalist" order-status="-1">理财失败</a>';
            $('#filteropt').html(tabs);
        }
    });
    $('body').delegate('#filteropt a','click',function(){
        var opt = $(this).attr('data-currorhis');
        $(this).addClass('curr').siblings().removeClass('curr');
        oTable.page('first').draw(false);
        return false;
    }).delegate(".changeStat",'click',function(){
        var projectUuid = $(this).attr("uuid");
        var val = $(this).attr("value");
        $.ajax({
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            "type": "post",
            "url": "/admin/project/status/update",
            "dataType": "json",
            "data": {projectUuid:projectUuid,status:val},
            "success": function(data) {
                window.location.reload();
            }});
    }).delegate(".distribute",'click',function(){
        var projectUuid = $(this).attr("uuid");
        $('#distproid').val(projectUuid);
        $.ajax( {
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            "type": "post",
            "url": "/admin/sys/project/distribute",
            "dataType": "json",
            "data": {projectUuid:projectUuid},
            "success": function(data) {
                var list = data.data;
                $('#distbody').html('');
                var html='';
                for(var i=0;i<list.length;i++) {
                    html+='<div class="form-group">';
                    html+='<div class="col-lg-10">';
                    html+='<label class="uniform">';
                    html+='<input class="uniform_on" type="checkbox" name="accids"';
                    if(list[i].hasRight=='1'){
                        html+=' checked ';
                    }
                    html+=' value="'+list[i].id+'">'+list[i].loginName;
                    html+='</label>';
                    html+='</div>';
                    html+='</div>';
                }
                $('#distbody').html(html);
                $("#distModal").modal("show");
            }
        });
    }).delegate('.subdist','click',function(){
        var projectUuid = $('#distproid').val();
        var boxstr='';
        $.each($('input:checkbox'),function(){
            boxstr+=$(this).val()+":"+(this.checked?"1":"0")+";";
        });
        $.ajax( {
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            "type": "post",
            "url": "/admin/sys/project/subdist",
            "dataType": "json",
            "data":{projectUuid:projectUuid,distStr:boxstr},
            "success": function(data) {
                window.location.reload();
            }
        });
    });
    function retrieveData( sSource, aoData, fnCallback ) {
        aoData.push( { "name": "status", "value":$('#filteropt a.curr').attr('order-status')||'0'} );
        aoData.push( { "name": "search", "value":$('input[type="search"]').val()} );
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
function importFile() {
    $.ajaxFileUpload({
        url: '/admin/project/import',
        secureuri: false,
        fileElementId: 'fileInput',
        dataType: 'json',
        success: function (data, status) {
            var json = eval(data);
            alert(json.message);
        }})
    return false;
}
