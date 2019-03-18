$(function(){
    var protocal = "http://";
    $('body').delegate('#logout','click',function(){
        $.ajax({
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            "type": "post",
            "url": "/admin/login/loginout",
            "dataType": "json",
            "success": function(data) {

            }
        });
        localStorage.setItem("loginToken","");
        window.location.href=protocal+window.location.host;
    }).delegate('#modifyPass','click',function(){
        createModal();
    }).delegate('#mpSubmit','click',function(){
        var oldpass = $('input[name="oldpass"]').val();
        var newpass = $('input[name="newpass"]').val();
        if(oldpass=='') {
            $('#changePassword').slideDown();
            $('#changePassword').text("请输入原密码");
            setTimeout(function () {
                $('#changePassword').slideUp();
            },2000);
            $('input[name="oldpass"]').focus();
            return false;
        }
        if(newpass=='') {
            $('#changePassword').slideDown();
            $('#changePassword').text("请输入新密码");
            setTimeout(function () {
                $('#changePassword').slideUp();
            },2000);

            $('input[name="newpass"]').focus();
            return false;
        }
        if(newpass.length<6 || newpass.length>32){
            $('#changePassword').slideDown();
            $('#changePassword').text("密码长度为6到32位");
            setTimeout(function () {
                $('#changePassword').slideUp();
            },2000);
            return;
        }
        $.ajax({
            "headers": {
                "Authorization":localStorage.getItem("loginToken")
            },
            "type": "post",
            "url": "/admin/login/modifyPass",
            "dataType": "json",
            data:{oldpass:oldpass,newpass:newpass},
            "success": function(data) {
                if(data.code=='401'){
                    localStorage.setItem("loginToken","");
                    $('#changePassword').slideDown();
                    $('#changePassword').text("token不存在请重新登录");
                    setTimeout(function () {
                        $('#changePassword').slideUp();
                    },2000);
                    window.location.href=protocal+window.location.host;
                }else {
                    if(data.code!='200'){
                        $('#agentsModal').modal('hide');
                        $('#changePassword').slideDown();
                        $('#changePassword').text(data.message);
                        setTimeout(function () {
                            $('#changePassword').slideUp();
                        },2000);
                    }else {
                        $('#agentsModal').modal('hide');
                        $('#changePassword').slideDown();
                        $('#changePassword').text(data.message+"即将跳转到登录页面");
                        setTimeout(function () {
                            $('#changePassword').slideUp();
                        },2000);
                        setTimeout(function () {
                            localStorage.setItem("loginToken","");
                            window.location.href=protocal+window.location.host;
                        },2500);

                    }
                }
            }
        });
    }).delegate('#logoClick','click',function () {
        var html = '<div class="innerContent"><div class="welcome" style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 50px">欢迎来到数字谷理财后台管理系统</div> </div>'
        $('.mainContent').html(html);
    });
    function createModal(){
        var html='<div class="alertTip" id="changePassword">';
        html+='</div>'
        html+='<div id="agentsModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">';
        html+='<div class="modal-dialog">';
        html+='<div class="modal-content">';
        html+='<div class="modal-header">';
        html+='<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>';
        html+='<h4  class="modal-title"><span> </span>修改密码</h4>';
        html+=' </div>';
        html+='<div class="modal-body">';
        html+='<div class="form-group">';
        html+='<label class="col-lg-2 control-label" for="oldpass">原密码：</label>';
        html+='<div class="col-lg-10">';
        html+='<input class="form-control" id="oldpass" type="password" name ="oldpass">';
        html+='</div>';
        html+='</div>';

        html+='<div class="form-group">';
        html+='<label class="col-lg-2 control-label" for="passwd">新密码：</label>';
        html+='<div class="col-lg-10">';
        html+='<input class="form-control" id="passwd" type="password" name ="newpass">';
        html+=' </div>';
        html+='</div>';

        html+='</div>';
        html+='<div class="modal-footer" style="border-top:0;margin-top:30px;">';

        html+='<button type="button" class="btn btn-default" style="margin-top:20px;" id="mpSubmit">提交</button>';
        html+='<button type="button" class="btn btn-default" data-dismiss="modal" style="margin-top:20px;">关闭</button>';
        html+='</div></div></div></div>';
        var $modal=$(html);
        $('body').append($modal);
        $('#agentsModal').modal('show');
    }
});