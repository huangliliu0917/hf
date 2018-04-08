
<!doctype html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
    <title>慧富宝支付系统 后台管理系统 - 您需要登录后才可以使用本功能</title>
    <link href="Public/Front/css/layui.css" rel="stylesheet" type="text/css">
    <link href="Public/Admin/css/login.css" rel="stylesheet" type="text/css">
    <style>
        /*.demo-class{
            width:200px;
            height:100px;
            position:absolute;
            top:50%;
            margin-top:-50px;
            background-color:#fff;
            border-radius:10px;
        }
        .demo-class .layui-layer-title{
            padding:10px 10px 5px 10px;
            background-color:rgba(153,153,153,0.3);
        }
        .demo-class .layui-layer-content{
            padding:5px;
            text-align:center;
        }
        .demo-class .layui-layer-btn{
            text-align:right;
            padding:5px 10px 5px 5px;
            cursor:pointer;
        }
        .demo-class .layui-layer-btn .layui-layer-btn0{
            color:#109fff;
        }*/
    </style>
    <meta name="__hash__" content="8b75791500bc8664783a70c31fe4e597_166e0f6e4cce875f7f411aa332567046" /></head>
<body>
<div class="login login-layout" id="login">
    <div>
        <h3 style="color:#FFF;font-size:20px">慧富宝支付系统管理后台欢迎您</h3>
        <form class="layui-form" method="post" action="/admin/user/login" autocomplete="off" id="loginform">
            <div class="layui-input-block user-name">
                <img src="Public/Admin/images/user.png" class="before">
                <input type="text" class="layui-input" id="username" lay-verify="required" name="loginId" placeholder="用户名" >
            </div>
            <div class="layui-input-block pwd">
                <img src="Public/Admin/images/pass.png" class="before">
                <input type="password" class="layui-input" id="password" lay-verify="required" name="password" placeholder="密码">
            </div>
            <div class="layui-input-block btn">
                <button type="submit" class="layui-btn" lay-submit lay-filter="login" id="loginbutton">登 录</button>
            </div>
            <input type="hidden" name="__hash__" value="8b75791500bc8664783a70c31fe4e597_166e0f6e4cce875f7f411aa332567046" /></form>
    </div>
</div>
<script src="Public/Front/js/jquery-3.2.0.js" type="text/javascript"></script>
<script src="Public/Front/js/plugins/layui/layui.all.js"></script>
<script>
    document.getElementById("username").focus();
    var verify=document.getElementById("verification");
    verify.onkeyup=function(){
        if(this.value.length >= 5){
            ajaxlogin();
            $("#loginbutton").text("登陆中......");
        }
    }
    layui.use(['form','layer'],function(){
        var layform=layui.form;
        var layer=layui.layer;
        layform.on('submit(login)',function(){
            ajaxlogin();
            return false;
        });
    });
    function ajaxlogin(){
        var lf=document.getElementById("loginform");
        var fd=new FormData(lf);
        var url=$("#loginform").attr("action");
        var uname=$("#username").val();
        var upass=$('#loginpassword').val();
        var uveri=$('#verification').val();
        var ajax=new XMLHttpRequest();
        ajax.open("POST",url,true);
        ajax.setRequestHeader("content-type","application/x-www-form-urlencoded");
        ajax.send("username="+uname+"&password="+upass+"&verify="+uveri);
        ajax.onreadystatechange=function(){
            if(ajax.readyState == 4){
                eval("var info="+ajax.responseText);
                if(info.errorno == 1){
                    layer.open({
                        title:'信息',
                        content:info.msg,
                    });
                    $("#loginbutton").text("登陆");
                    $("#verification").val("");
                    document.getElementById("verifycode").click();
                }else{
                    layer.open({
                        title:'信息',
                        content:info.msg,
                        yes:function(){
                            window.location=info.url;
                        }
                    });

                }
            }
        }
    }
</script>
</body>
</html>