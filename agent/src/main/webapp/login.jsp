<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8"  %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    <title>慧富宝支付系统 - 登录</title>
    <link href="Public/Front/css/bootstrap.min.css" rel="stylesheet">
    <link href="Public/Front/css/font-awesome.css" rel="stylesheet">
    <link rel="stylesheet" src="Public/Front/bootstrapvalidator/css/bootstrapValidator.min.css">
    <link href="Public/Front/css/animate.css" rel="stylesheet">
    <link rel="stylesheet" href="Public/Front/login/css/style.css">
    <script src="Public/Front/login/js/modernizr-2.6.2.min.js"></script>
    <!--[if lt IE 9]>
    <meta http-equiv="refresh" content="0;ie.html"/>
    <![endif]-->
    <script>
        if (window.top !== window.self) {
            window.top.location = window.location;
        }
    </script>
    <meta name="__hash__" content="fd408b4a51869cccb4db8232743e0693_de8eadff5b65f5baf4801f6196739598" /></head>
<body class="style-3">

<div class="container">
    <div class="row col-hidden-xs">
        <div class="mt-50"></div>
    </div>
    <div class="row">
        <div class="col-md-4 col-md-push-8">

            <form class="fh5co-form animate-box form-horizontal" data-animate-effect="fadeInRight" id="formlogin"
                  method="post" role="form" action="/agent/user/login">
                <div class="form-group"><h2>代理登录</h2>  </div>
                <div class="form-group">
                    <label for="username" class="sr-only">用户名</label>
                    <input type="text" class="form-control" id="loginId" name="loginId" placeholder="用户名" required="" minlength="2" aria-required="true" autocomplete="off">
                </div>
                <div class="form-group">
                    <label for="password" class="sr-only">密码</label>
                    <input type="password" class="form-control" id="password" name="password" placeholder="密码" required="" aria-required="true" placeholder="Password" autocomplete="off">
                </div>
                <div class="form-group">
                    <label for="remember"><input type="checkbox" id="remember"> 记住我</label>
                </div>
                <div class="form-group">
                    <button class="btn btn-primary">点击登录</button>
                </div>
                <input type="hidden" name="__hash__" value="fd408b4a51869cccb4db8232743e0693_de8eadff5b65f5baf4801f6196739598" /></form>
            <!-- END Sign In Form -->
        </div>
    </div>
    <div class="row" style="padding-top: 60px; clear: both;">
        <div class="col-md-12 text-center"><p><small>&copy; 慧富宝支付系统 All Rights Reserved.  </small></p></div>
    </div>
</div>
<script src="Public/Front/js/jquery.min.js"></script>
<script src="Public/Front/js/bootstrap.min.js"></script>
<script src="Public/Front/bootstrapvalidator/js/bootstrapValidator.min.js"></script>

<div style="display:none;"><script type="text/javascript">var cnzz_protocol = (("https:" == document.location.protocol) ? " https://" : " http://");document.write(unescape("%3Cspan id='cnzz_stat_icon_1261742514'%3E%3C/span%3E%3Cscript src='" + cnzz_protocol + "s11.cnzz.com/stat.php%3Fid%3D1261742514' type='text/javascript'%3E%3C/script%3E"));</script></div></body>
</html>