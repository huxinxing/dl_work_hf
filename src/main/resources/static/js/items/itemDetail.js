var urlArr = window.location.search.slice(1).split('&'),id;
var innerFunc = {
    eachPart : function (parList) {
        var html = '<div class="'+parList.clsName+'">';
        html += '<h5>'+parList.title+'</h5>';
        html += '<ul>';
        $.each(parList.liArr,function (i,val) {
            if(val.name ==='基金公司<br>绩效佣金'){
                html += '<li><span class="sinle-name">'+val.name+'</span><div class="rate" style="width: '+val.width+'px;position:relative;top: 10px;"></div><span class="single-num" style="position:relative;top: 10px;">'+val.num+'</span></li>';
            }else{
                html += '<li><span class="sinle-name">'+val.name+'</span><div class="rate" style="width: '+val.width+'px"></div><span class="single-num">'+val.num+'</span></li>';
            }
        });
        html += '</ul></div>';
        $('.invest-part').append(html);
    },
    smallParam : function (name1,num1,width) {
        var pList = {},len = num1.length,arr1 = [];
        for(i = 0 ; i < len ; i++){
            pList = {};
            pList.name = name1[i];
            pList.num = num1[i];
            pList.width = width[i]*210;
            arr1.push(pList)
        }
        return arr1;
    },
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
            "sAjaxSource": "/statistics/FinancingProjectDetailUser",
            "bFilter": true,
            "bSort":false,//开启排序
            "bLengthChange": true,
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
        });
    },
    retrieveData:function( sSource, aoData, fnCallback ) {
        aoData.push( { "name": "serialNum", "value": id} );
        aoData.push( { "name": "flag", "value": $('.user-active').index()} );
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
    initCircle: function (data1,data2) {
        var optionPie = {
            color:['#5c89fd','#67bed7'],
            title : {
                text: '',
                subtext: '',
                x:'center'
            },
            tooltip : {
                trigger: 'item',
                position: function (pos, params, dom, rect, size) {
                    // 鼠标在左侧时 tooltip 显示到右侧，鼠标在右侧时 tooltip 显示到左侧。
                    var obj = {top: 60};
                    obj[['left', 'right'][+(pos[0] < size.viewSize[0] / 2)]] = 5;
                    return obj;
                },
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                type: 'scroll',
                orient: 'vertical',
                left: 20,
                top: 10,
                data: ['已收资金','已支付资金']
            },
            series : [
                {
                    name: '对比',
                    type: 'pie',
                    radius : ['30%','50%'],
                    center: ['40%', '50%'],
                    data:[
                        {value:data1, name:'已收资金'},
                        {value:data2, name:'已支付资金'}
                    ],
                    label:{
                        normal: {
                            show: false,
                            formatter: function(val){
                                var result = val.name+val.value+'usd'+'\n'+'占比'+val.percent+'%';
                                return result;
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
        var myChartPie = echarts.init(document.getElementById('d-circle'));
        myChartPie.setOption(optionPie);
    },
    initBar:function (index,data,title) {
        var color = ['#5c89fd','#67bed7'];
        var bottom = ['20','32'];
        var option = {
            color:[color[index]],
            tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: bottom[index],
                containLabel: true
            },
            xAxis : [
                {
                    type : 'category',
                    data : title,
                    axisTick: {
                        show:false,
                        alignWithLabel: false
                    },
                    axisLine:{
                        lineStyle:{
                            color:'#E4E4E4'
                        }
                    },
                    axisLabel:{
                        color:'#999'
                    }
                }
            ],
            yAxis : [
                {
                    type : 'value',
                    show:true,
                    // splitNumber:8,
                    axisLine:{
                        show:false
                    },
                    axisLabel:{
                        show:false
                    },
                    axisTick:{
                        show:false
                    }
                }
            ],
            series : [
                {
                    name:'直接访问',
                    type:'bar',
                    barWidth: '20',
                    label:{
                        normal: {
                            show: true,
                            color:'#000',
                            position: 'top'

                        }
                    },
                    data:data
                }
            ]
        };
        var myChartPie = index ? echarts.init(document.getElementById('d-payed'))
                                :echarts.init(document.getElementById('d-money'));
        // var myChartPie = echarts.init(document.getElementById('d-money'));
        // myChartPie.setOption(option);
        // var myChartPie = echarts.init(document.getElementById('d-payed'));
        myChartPie.setOption(option);
    },
    urlSplit:function(){
        $.each(urlArr,function (i,val) {
            var single = val.split('=');
            if(single[0] === 'id'){
                id = single[1];
            }
            if(single[0] === 'flag'){
                if(single[1] == '1'){
                    $('.user-flag span:last-child').addClass('user-active').siblings().removeClass('user-active')
                }else{
                    $('.user-flag span:first-child').addClass('user-active').siblings().removeClass('user-active')
                }
            }
        })
    }
}
function initData(){
    var data = {
        flag:$('.user-active').index(),
        serialNum:id
    }
    $.ajax({
        "headers": {
            "Authorization":localStorage.getItem("loginToken")
        },
        "type": "post",
        "url" : "/statistics/FinancingProjectDetail",
        "dataType": "json",
        "data": data,
        success : function (rep) {
            $('.raise-num').text(rep.data.amount);
            $('.publish-time').text(rep.data.createTime);
            $('.item-name').text(rep.data.title);
            var status = rep.data.status;
            var text = status == 0?'初始状态':
                status == 1?'募集中':
                    status == 2?'募集暂停':
                       '募集结束';

            $('.item-state').text(text);
            $('.raised-num').text(rep.data.coinAmount);
            $('.raise-progress').text(rep.data.schedules);
            $('.invest-people').text(rep.data.humanNum);
            $('.invest-money').text(rep.data.totalInvest);
            innerFunc.initCircle(rep.data.received,rep.data.paid);
            $('#money_my').text(rep.data.received);
            $('#money_paid').text(rep.data.paid);
            $('#paidBCB').text('='+rep.data.paidBCB+'BCB');
            var lengendA = ['投资本金','服务费','认购费','代收税费','基金公司\n绩效佣金'];
            var lengendB = ['本金','保底收益','额外收益','项目返点','体验金','加息券'];
            var dataA = [rep.data.totalInvest,rep.data.receivedServiceFee,rep.data.receivedPaymentFee,rep.data.receivedTax,rep.data.receivedAchievements];
            var dataB = [rep.data.paidPrincipal,rep.data.paidFixedProfit,rep.data.paidAdditionalProfit,rep.data.paidAgentRebeat,rep.data.paidGeAmount,rep.data.paidIcAmount];
            innerFunc.initBar(0,dataA,lengendA);
            innerFunc.initBar(1,dataB,lengendB);
            innerFunc.tableShow();
        }
    })
}
$('.user-flag span').on('click',function(){
    var self = this;
    $(self).addClass('user-active').siblings().removeClass('user-active');
    initData();
    oTable.ajax.reload();
});
$('#export_excel_item').on('click',function () {
    $.ajax({
        "headers": {
            "Authorization":localStorage.getItem("loginToken")
        },
        "type": "get",
        "url" : "/operarions/isTrueExportExecl",
        "dataType": "json",
        "data": {},
        success : function (rep) {
            if(rep.code == '200'){
                window.location.href = '/statistics/geExportFinancingUser?flag='+($('.user-active').index())+'&serialNum='+id;
            }/* else {
                $('.alertTip').slideDown();
                $('.alertTip').text(rep.message);
                setTimeout(function () {
                    $('.alertTip').slideUp();
                }, 2000);
            }*/
        }
    })
    // window.location.href = '/statistics/geExportFinancingUser?flag='+($('.user-active').index())+'&serialNum='+id;
});
$(function () {
    innerFunc.urlSplit();
    initData();
});