
String.prototype.trim = function () {
    return this.replace(/^\s*|\s*$/g, '');
}

$(document).ready(function(){
    var container = $("#login");

    container.find(".login-input").unbind().keydown(function(event) {
        if (event.keyCode == 13) {//按回车
            tologin(container);
        }
    });

    container.find(".to-login-btn").unbind().click(function(){
        tologin(container);
    });

});

var tologin =  function(container){

    var username = container.find(".username").val().trim();
    var password = container.find(".password").val().trim();

    if(username.trim() == ""){
        container.find(".error-tip").html("请输入用户名！").show();
        return;
    }
    if(password.trim() == ""){
        container.find(".error-tip").html("请输入密码！").show();
        return;
    }
    var param = {};
    param.username = username.trim();
    param.password = password.trim();

    $.ajax({
        type: 'post',
        url: '/ALLogin/doAdminLogin',
        data: param,
        success: function (dataJson) {

            if(dataJson.isOk == false){
                container.find(".error-tip").html(dataJson.msg).show();
                return;
            }

            location.href = "/admin/index"
        }
    });


}


