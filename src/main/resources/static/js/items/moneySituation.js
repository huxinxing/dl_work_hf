var theadArr1 = ['日期','投资本金(usdx)','认购费(usdx)','服务费(usdx)','基金公司绩效佣金(usdx)','代收税费(usdx)','<span class="red-cl">*</span> 总计(usdx)'];
var theadArr2 = ['日期','本金(usdx)','保底收益(usdx)','额外收益(usdx)','项目返点(usdx)','体验金(usdx)','加息券(usdx)','<span class="red-cl">*</span> 总计(usdx)','<span class="red-cl">*</span> 兑换BCB'];
var theadArr3 = ['日期','保底收益(usdx)','额外收益(usdx)','<span class="red-cl">*</span> 总计(usdx)'];
var oTable,oTable1,showTab = true;
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
    overflow:function () {
        var param = innerFunc.innerParam();
        var enddate = new Date(($('input[name="createTimeEnd"]').val()).replace(/-/g,"/"));
        var startTime = new Date(( $('input[name="createTimeStart"]').val()).replace(/-/g,"/"));
        if(startTime !=null && enddate!=null && enddate < startTime)
        {   layer.msg('结束时间不能早于开始时间');
            return;
        }
        innderAjax('post',"/statistics/Overview",param,function(r){
            $('.publish-num').text(r.data.financingCount);
            $('#to_invest').text(r.data.totalInvest);
            $('#usdx_invest').text(r.data.totalInvestUSDX);
            $('#eth_invest').text(r.data.totalInvestETH);
            $('.invest-stake').text(r.data.receivedPrincipal);
            $('.sales-charge').text(r.data.receivedPaymentFee);
            $('.service-charge').text(r.data.receivedServiceFee);
            $('.fund').text(r.data.receivedAchievements);
            $('.invest-taxes').text(r.data.receivedTax);
            $('.draw-fee').text(r.data.receivedWithdrawFee);
            $('#box1 .total-1').text(r.data.receivedTotal);

            $('#box2 .my-mo').text(r.data.paidPrincipal);
            $('#box2 .my-income').text(r.data.paidFixedProfit);
            $('#box2 .extra-income').text(r.data.paidAdditionalProfit);
            $('.item-return').text(r.data.paidAgentRebeat);
            $('#box2 .total-income').text(r.data.paidTotal);
            $('#box2 .total-bcb').text(r.data.paidTotalBCB);
            $('.bbin-s').text(r.data.paidGe);
            $('.rateCoupon-s').text(r.data.paidIC);
            var optionPie = {
                title : {
                    text: '',
                    subtext: '',
                    x:'center'
                },
                tooltip : {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    type: 'scroll',
                    orient: 'vertical',
                    right: 20,
                    bottom: 30,
                    data: ['已收资金','已支付资金']
                },
                series : [
                    {
                        name: '对比',
                        type: 'pie',
                        radius : ['35%','55%'],
                        center: ['40%', '50%'],
                        data:[
                            {value:r.data.receivedTotal, name:'已收资金'},
                            {value:r.data.paidTotal, name:'已支付资金'}
                        ],
                        label:{
                            normal: {
                                show: true,
                                formatter: function(val){
                                    var result = val.name+val.value+'usdx'+'\n'+'占比'+val.percent+'%';
                                    return result;
                                }
                            },
                            emphasis: {
                                show: true,
                                textStyle: {
                                    fontSize: '30',
                                    fontWeight: 'bold'
                                }
                            }
                        },
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            };
            var myChartPie = echarts.init(document.getElementById('invest_circle'));
            myChartPie.setOption(optionPie);
        });
    },
    diagrams:function () {
        var param = {
            length:parseInt($('.active-day').text()),
            flag:$('.user-active').index()
        };
        innderAjax('post',"/statistics/Diagrams",param,function(r){
            var myChartLine = echarts.init(document.getElementById('line_trend'));
            var optionLine = {
                title: {
                    text: ''
                },
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    bottom:0,
                    itemGap:50,
                    data:['已收资金','已支付资金']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '60',
                    containLabel: true
                },
                /*toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },*/
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: r.data.time
                },
                yAxis: {
                    type: 'value'
                },
                series: [
                    {
                        name:'已收资金',
                        type:'line',
                        data:r.data.receivedTotals
                    },
                    {
                        name:'已支付资金',
                        type:'line',
                        data:r.data.paidTotals
                    }
                ]
            };
            myChartLine.setOption(optionLine);
        })

        // var r = {"status":"success","code":"200","message":"请求成功","data":{"length":1000,"receivedTotals":[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"paidTotals":[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"toPaidTotals":[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"dates":[1528871015299,1528871015299,1528871015299,1528871015299,1528871015299,1528871015299,1528871015299,1528871015299,1528871015299,1528871015299]}};


    },
    tableShow:function (index) {
        var url,domId;
        if(index === undefined || index === 0){
            $('#transFlowTable_wrapper').removeClass('dn');
            $('#transFlowTable2_wrapper').addClass('dn');
            url = "/statistics/ReceivedFlows";
            domId = 'transFlowTable';
            innerFunc.initThead(theadArr1,domId)


        }else/* if(index === 1)*/{
            $('#transFlowTable2_wrapper').removeClass('dn');
            $('#transFlowTable_wrapper').addClass('dn');
            url = "/statistics/PaidFlows";
            domId = 'transFlowTable2';
            innerFunc.initThead(theadArr2,domId)

        }
        var option = {
            "bJQueryUI": true,
            "iDisplayStart":0,
            "iDisplayLength":30,
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
            "bLengthChange": true,
            "bSort":false,//开启排序
            /* "bInfo":true,
             "bSort":false,//开启排序
             "aaSorting": [[ 4, "desc" ]],//指定默认排序
             "aoColumnDefs": [ { "bSortable": false, "aTargets": [ 0 ] }],//对其中某行不排序*/
            "bProcessing": true,
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
        }
        if(index === undefined || index === 0){
            oTable = $('#'+domId).DataTable(option);
        }else{
            oTable1 = $('#'+domId).DataTable(option);
        }
    },
    innerParam:function(){
        var param = {};
        param.flag = $('.user-active').index();
        param.beginTime = $('#createTimeStart').val();
        param.endTime = $('#createTimeEnd').val();
        return param
    },
    initDate:function(){
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
    retrieveData:function( sSource, aoData, fnCallback) {
        var param = innerFunc.innerParam();
        aoData.push( { "name": "length", "value": parseInt($('.active-day').text())} );
        aoData.push( { "name": "flag", "value": param.flag} );
        /*aoData.push( { "name": "walletAddr", "value": $('input[name="walletAddr"]').val()} );
        aoData.push( { "name": "paymentType", "value": $('select[name="paymentType"]').val()} );*/
        /*var enddate = new Date(($('input[name="createTimeEnd"]').val()).replace(/-/g,"/"));
        var startTime = new Date(( $('input[name="createTimeStart"]').val()).replace(/-/g,"/"));
        if(startTime !=null && enddate!=null && enddate < startTime)
        {   layer.msg('结束时间不能早于开始时间');
            // return false;
        }*/
        innderAjax('post',sSource,JSON.stringify(aoData),function (rep){
            fnCallback(rep)
        },"application/json")
    },
    initThead:function (theadArr,domId) {
        var html = '<tr>';
        $.each(theadArr,function (i,val) {
            html += '<th>'+val+'</th>';
        })
        html += '</tr>';
        $('#'+domId+' thead').html(html);
    }
};
function initData(){
    innerFunc.initDate();
    innerFunc.overflow();
    innerFunc.diagrams();
    innerFunc.tableShow();

};
$('.user-flag span').on('click',function(){
    var self = this;
    $(self).addClass('user-active').siblings().removeClass('user-active');
    innerFunc.overflow();
    innerFunc.diagrams();
    innerFunc.tableShow();
    oTable.ajax.reload();
});
$('.day-tab span').on('click',function(){
    var self = this;
    if($(self).hasClass('active-day')){
        return
    }
    $(self).addClass('active-day').siblings().removeClass('active-day');
    innerFunc.diagrams();
    var index = $('.table-active').index();
    index?oTable1.ajax.reload():oTable.ajax.reload();
});
$('.invest-tab span').on('click',function(){
    var self = this;
    if($(self).hasClass('table-active')){
        return
    }
    var index = $(self).index();
    $(self).addClass('table-active').siblings().removeClass('table-active');
    innerFunc.tableShow(index);
    showTab ? innerFunc.tableShow(index):
    index?oTable1.ajax.reload():oTable.ajax.reload();
    showTab = false;
});
$('.table-active span').on('click',function(){
    var self = this;
    $(self).addClass('active-day').siblings().removeClass('user-active')
});
$('#trandQuerybyServer').on('click',function(){
    innerFunc.overflow();
    // oTable.ajax.reload();
});

$(function(){
    initData();
})