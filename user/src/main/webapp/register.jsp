
<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8"  %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>慧富宝支付系统  - 注册</title>
    <link href=Public/Front/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
    <link href=Public/Front/css/font-awesome.css?v=4.4.0" rel="stylesheet">
    <link rel="stylesheet" src="Public/Front/bootstrapvalidator/css/bootstrapValidator.min.css">
    <link href="Public/Front/css/animate.css" rel="stylesheet">
    <link rel="stylesheet" href="Public/Front/login/css/style.css">
    <meta name="__hash__" content="9174f744b99de00681f4bb29d4bbc9c8_1220a3d7b9e917dfa06a498549bcfd64" /></head>
<body class="style-3">

<%
    String inviteCode = request.getParameter("i");
%>

<div class="container">
    <div class="row">
        <div class="col-md-4 col-md-push-8">

            <!-- Start Sign In Form -->
            <form class="fh5co-form animate-box" data-animate-effect="fadeInRight" role="form" id="Formreg" method="post" action="/user/user/register" autocomplete="off">
                <h2>注册成为会员</h2>

                <input type="hidden" name="inviteCode" value="<%=inviteCode%>"/>

                <div class="form-group">
                    <label for="loginId" class="sr-only">用户名</label>
                    <input type="text" class="form-control" id="loginId" name="loginId" placeholder="请输入用户名" >
                </div>
                <div class="form-group">
                    <label for="password" class="sr-only">登录密码</label>
                    <input type="password" class="form-control" id="password" name="password" placeholder="请输入登录密码" >
                    <div class="progress password-progress">
                        <div id="strengthBar" class="progress-bar" role="progressbar" style="width: 0;"></div>
                    </div>
                </div>
                <div class="form-group">
                    <label for="password" class="sr-only">再次输入密码</label>
                    <input type="password" class="form-control" id="confirmpassword" name="confirmpassword" placeholder="请再次输入登录密码" >
                </div>
                <div class="form-group">
                    <div id="messages"></div>
                </div>
                <div class="form-group">
                    <label for="email" class="sr-only">邮箱</label>
                    <input type="text" class="form-control" id="email" name="email" placeholder="请输入邮箱" >
                </div>
                <div class="form-group">
                    <label for="tel" class="sr-only">手机号</label>
                    <input type="text" class="form-control" id="tel" name="tel" placeholder="请输入手机号" >
                </div>
                <div class="form-group">
                    <p>已经有账号? <a href="/user/login.jsp">点击登录</a></p>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">点击注册</button>
                </div>
                <input type="hidden" name="__hash__" value="9174f744b99de00681f4bb29d4bbc9c8_1220a3d7b9e917dfa06a498549bcfd64" /></form>
            <!-- END Sign In Form -->


        </div>
    </div>
    <div class="row" style="padding-top: 60px; clear: both;">
        <div class="col-md-12 text-center"><p><small>&copy; 慧富宝支付系统 All Rights Reserved.  </small></p></div>
    </div>
</div>

<!-- 全局js -->
<script src="Public/Front/js/jquery.min.js"></script>
<script src="Public/Front/js/bootstrap.min.js"></script>
<script src="Public/Front/bootstrapvalidator/js/bootstrapValidator.min.js"></script>
<script src="Public/Front/js/plugins/zxcvbn/4.4.2/zxcvbn.js"></script>
<script src="Public/Front/js/plugins/layer/layer.min.js"></script>
<script src="Public/Front/login/js/modernizr-2.6.2.min.js"></script>
<style>
    .password-progress {
        margin-top: 10px;
        margin-bottom: 0;
    }
</style>
<script>
    $(document).ready(function() {
        $('form').bootstrapValidator({
            //container: '#messages',
            message: 'This value is not valid',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
                username: {
                    message: '用户名验证失败',
                    validators: {
                        notEmpty: {
                            message: '用户名不能为空'
                        },
                        different: {
                            field: 'password',
                            message: '用户名不能和密码相同'
                        },
                        threshold:6,
                        stringLength: {
                            min: 5,
                            max: 20,
                            message: '用户名长度必须在5到20之间'
                        },
                        remote:{
                            url: "user/check_user",
                            message: '用户已存在',
                            delay :  1000
                        },
                        regexp: {
                            regexp: /^[a-zA-Z0-9_\.]+$/,
                            message: '用户名由数字字母下划线和.组成'
                        }
                    }
                },
                password: {
                    validators: {
                        notEmpty: {
                            message: '密码不能为空'
                        },
                        identical: {
                            field: 'confirmpassword',
                            message: '两次密码不一致'
                        },
                        different: {
                            field: 'username',
                            message: '不能和用户名相同'
                        },
                        stringLength: {
                            min: 6,
                            max: 30,
                            message: '密码长度在6到30之间'
                        }
                    }
                },
                confirmpassword: {
                    message: '确认密码无效',
                    validators: {
                        notEmpty: {
                            message: '确认密码不能为空'
                        },
                        stringLength: {
                            min: 6,
                            max: 30,
                            message: '用户名长度必须在6到30之间'
                        },
                        identical: {
                            field: 'password',
                            message: '两次密码不一致'
                        },
                        different: {
                            field: 'username',
                            message: '不能和用户名相同'
                        }
                    }
                },
            }
        });
    });
</script>
<div style="display:none;"><script type="text/javascript">var cnzz_protocol = (("https:" == document.location.protocol) ? " https://" : " http://");document.write(unescape("%3Cspan id='cnzz_stat_icon_1261742514'%3E%3C/span%3E%3Cscript src='" + cnzz_protocol + "s11.cnzz.com/stat.php%3Fid%3D1261742514' type='text/javascript'%3E%3C/script%3E"));</script></div><!--统计代码，可删除-->
</body>
</html>