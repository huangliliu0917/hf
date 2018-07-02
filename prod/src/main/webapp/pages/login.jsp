<!DOCTYPE html>
<html style="overflow-x:auto;overflow-y:auto;">
<head>
    <meta charset="utf-8">
    <title>智能管理平台 登录</title><!--  - Powered By JeeSite -->
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" /><meta name="author" content="http://jeesite.com/"/>
    <meta name="renderer" content="webkit"><meta http-equiv="X-UA-Compatible" content="IE=8,IE=9,IE=10" />
    <meta http-equiv="Expires" content="0"><meta http-equiv="Cache-Control" content="no-cache"><meta http-equiv="Cache-Control" content="no-store">
    <script src="/payManager/static/jquery/jquery-1.8.3.min.js" type="text/javascript"></script>
    <link href="/payManager/static/bootstrap/2.3.1/css_cerulean/bootstrap.min.css" type="text/css" rel="stylesheet" />
    <script src="/payManager/static/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
    <link href="/payManager/static/bootstrap/2.3.1/awesome/font-awesome.min.css" type="text/css" rel="stylesheet" />
    <!--[if lte IE 7]><link href="/payManager/static/bootstrap/2.3.1/awesome/font-awesome-ie7.min.css" type="text/css" rel="stylesheet" /><![endif]-->
    <!--[if lte IE 6]><link href="/payManager/static/bootstrap/bsie/css/bootstrap-ie6.min.css" type="text/css" rel="stylesheet" />
<script src="/payManager/static/bootstrap/bsie/js/bootstrap-ie.min.js" type="text/javascript"></script><![endif]-->
    <link href="/payManager/static/jquery-select2/3.4/select2.min.css" rel="stylesheet" />
    <script src="/payManager/static/jquery-select2/3.4/select2.min.js" type="text/javascript"></script>
    <link href="/payManager/static/jquery-validation/1.11.0/jquery.validate.min.css" type="text/css" rel="stylesheet" />
    <script src="/payManager/static/jquery-validation/1.11.0/jquery.validate.min.js" type="text/javascript"></script>
    <link href="/payManager/static/jquery-jbox/2.3/Skins/Bootstrap/jbox.min.css" rel="stylesheet" />
    <script src="/payManager/static/jquery-jbox/2.3/jquery.jBox-2.3.min.js" type="text/javascript"></script>
    <script src="/payManager/static/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
    <script src="/payManager/static/common/mustache.min.js" type="text/javascript"></script>
    <link href="/payManager/static/common/jeesite.css" type="text/css" rel="stylesheet" />
    <link href="/payManager/static/common/bms.min.css" type="text/css" rel="stylesheet" />
    <script src="/payManager/static/common/jeesite.js" type="text/javascript"></script>
    <script type="text/javascript">var ctx = '/payManager/a', ctxStatic='/payManager/static';</script>
    <!-- Baidu tongji analytics --><script>var _hmt=_hmt||[];(function(){var hm=document.createElement("script");hm.src="//hm.baidu.com/hm.js?82116c626a8d504a5c0675073362ef6f";var s=document.getElementsByTagName("script")[0];s.parentNode.insertBefore(hm,s);})();</script>


    <meta name="decorator" content="blank"/>
    <style type="text/css">
        html,body,table{background-color:#f5f5f5;width:100%;text-align:center;}.form-signin-heading{font-family:Helvetica, Georgia, Arial, sans-serif, 黑体;font-size:36px;margin-bottom:20px;color:#0663a2;}
        .form-signin{position:relative;text-align:left;width:300px;padding:25px 29px 29px;margin:0 auto 20px;background-color:#fff;border:1px solid #e5e5e5;
            -webkit-border-radius:5px;-moz-border-radius:5px;border-radius:5px;-webkit-box-shadow:0 1px 2px rgba(0,0,0,.05);-moz-box-shadow:0 1px 2px rgba(0,0,0,.05);box-shadow:0 1px 2px rgba(0,0,0,.05);}
        .form-signin .checkbox{margin-bottom:10px;color:#0663a2;} .form-signin .input-label{font-size:16px;line-height:23px;color:#999;}
        .form-signin .input-block-level{font-size:16px;height:auto;margin-bottom:15px;padding:7px;*width:283px;*padding-bottom:0;_padding:7px 7px 9px 7px;}
        .form-signin .btn.btn-large{font-size:16px;} .form-signin #themeSwitch{position:absolute;right:15px;bottom:10px;}
        .form-signin div.validateCode {padding-bottom:15px;} .mid{vertical-align:middle;}
        .header{height:80px;padding-top:20px;} .alert{position:relative;width:300px;margin:0 auto;*padding-bottom:0px;}
        label.error{background:none;width:270px;font-weight:normal;color:inherit;margin:0;}
    </style>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#loginForm").validate({
                rules: {
                    validateCode: {remote: "/payManager/servlet/validateCodeServlet"}
                },
                messages: {
                    username: {required: "请填写用户名."},password: {required: "请填写密码."},
                    validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
                },
                errorLabelContainer: "#messageBox",
                errorPlacement: function(error, element) {
                    error.appendTo($("#loginError").parent());
                }
            });
        });
        // 如果在框架或在对话框中，则弹出提示并跳转到首页
        if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.jbox').length > 0){
            alert('未登录或登录超时。请重新登录，谢谢！');
            top.location = "/payManager/a";
        }
    </script>

</head>
<body>

<!--[if lte IE 6]><br/><div class='alert alert-block' style="text-align:left;padding-bottom:10px;"><a class="close" data-dismiss="alert">x</a><h4>温馨提示：</h4><p>你使用的浏览器版本过低。为了获得更好的浏览体验，我们强烈建议您 <a href="http://browsehappy.com" target="_blank">升级</a> 到最新版本的IE浏览器，或者使用较新版本的 Chrome、Firefox、Safari 等。</p></div><![endif]-->
<div class="header">
    <div id="messageBox" class="alert alert-error hide"><button data-dismiss="alert" class="close">×</button>
        <label id="loginError" class="error"></label>
    </div>
</div><!-- 智能管理平台 -->
<h1 class="form-signin-heading">智能管理平台</h1>
<form id="loginForm" class="form-signin" action="/payManager/a/login" method="post">
    <label class="input-label" for="username">登录名</label>
    <input type="text" id="username" name="username" class="input-block-level required" value="">
    <label class="input-label" for="password">密码</label>
    <input type="password" id="password" name="password" class="input-block-level required">

    <input class="btn btn-large btn-primary" type="submit" value="登 录"/>&nbsp;&nbsp;
    <label for="rememberMe" title="下次不需要再登录"><input type="checkbox" id="rememberMe" name="rememberMe" /> 记住我（公共场所慎用）</label>
    <div id="themeSwitch" class="dropdown">
        <a class="dropdown-toggle" data-toggle="dropdown" href="#">默认主题<b class="caret"></b></a>
        <ul class="dropdown-menu">
            <li><a href="#" onclick="location='/payManager/theme/default?url='+location.href">默认主题</a></li><li><a href="#" onclick="location='/payManager/theme/cerulean?url='+location.href">天蓝主题</a></li><li><a href="#" onclick="location='/payManager/theme/readable?url='+location.href">橙色主题</a></li><li><a href="#" onclick="location='/payManager/theme/united?url='+location.href">红色主题</a></li><li><a href="#" onclick="location='/payManager/theme/flat?url='+location.href">Flat主题</a></li>
        </ul>
        <!--[if lte IE 6]><script type="text/javascript">$('#themeSwitch').hide();</script><![endif]-->
    </div>
</form>
<div class="footer">



    Copyright &copy; 2012-2019 <a href="/payManager/f">智能管理平台</a> - Powered By <a href="" target="_blank"></a> V1.0.1


</div>
<script src="/payManager/static/flash/zoom.min.js" type="text/javascript"></script>

</body>
</html>