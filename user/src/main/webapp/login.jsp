<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8"  %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="shortcut icon" href="logo2.png" type="image/x-icon">
    <title>慧富宝[支付技术服务商]</title>
    <meta name="keywords" content="慧富宝,支付接口,移动支付,应用内支付,微信支付,银联接口,支付宝支付,接口,SP,短代,网银接口">
    <meta name="description" content="慧富宝支付接口服务平台，提供一站式的支付接口接入服务。让接入支付渠道变的无比简单">
    <meta name="baidu-site-verification" content="akHk5pDgS4" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <!--[if IE 8]><meta http-equiv="X-UA-Compatible" content="IE=8"><![endif]-->
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <meta naem="format-detection" content="telephone=no">
    <link href="web/bootstrap/css/bootstrap.css" type="text/css" rel="stylesheet" media="screen">
    <!-- <link href="web/css/base2.css?s=2" type="text/css" rel="stylesheet" media="screen"> -->
    <link href="web/css/home.css" type="text/css" rel="stylesheet" media="screen">
    <script src="web/js/jquery.min.js"></script>
    <script src="web/bootstrap/js/bootstrap.js"></script>
</head>
<body>
<!-- 导航开始 -->
<nav class="navbar navbar-default navbar-home">
    <div class="container container-60">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand logo" href="#"></a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li class="navbar-nav-li"><a  class="active" href="/">首页</a></li>
                <!-- <li class="navbar-nav-li"><a href="introduce.html">产品中心</a></li> -->
                <!-- <li class="navbar-nav-li"><a href="#">帮助中心</a></li> -->
                <%--<li class="navbar-nav-li"><a href="web/demo/php/demo.rar">demo下载</a></li>--%>
                <%--<li class="navbar-nav-li"><a href="/Ex_index.html">在线体验</a></li>--%>
                <li class="navbar-nav-li"><a href="/agent">代理登陆</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li class="navbar-nav-li hidden-xs"><a href="#" class="fa-qq">客服QQ : 253579401</a></li>
                <li class="navbar-nav-li"><a class="login" href="javascript:;">登陆</a></li>
                <li class="navbar-nav-li"><a href="register.jsp" class="register">注册</a></li>
            </ul>
        </div><!-- /.navbar-collapse -->
    </div><!-- /.container-fluid -->
    <script>
       $(".login").click(function(){
             $(".login-zz").css("display","block");
             $(".login-box").css("display","block");
           });
    </script>
</nav>
<!-- 导航结束 -->
<!-- banner图片轮播开始 -->
<section id="banner" style="overflow:hidden">
    <div class="container-fuild">
        <div class="bigimg">
            <div class="banner1" ><img class="smallimg" data-src="/web/css/images/banner1.jpg" src=""/></div>
            <div class="banner2"><img class="smallimg" data-src="/web/css/images/banner2.jpg" src=""/></div>
        </div>
        <div class="banner-menu">
            <div class="banner-menu-li active"></div>
            <div class="banner-menu-li"></div>
        </div>
        <div class="clean"></div>
    </div>
    <script>
        var imgli=$(".bigimg div");
        var menu=$(".banner-menu-li");
        var width=$("body").width();
        var bigimg=$(".bigimg");
        var bigimgwidth=parseInt(width)*imgli.length;
        imgli.css("width",width);
        bigimg.css("width",bigimgwidth);

        var moveleft=-width;
        var index=1;
        if(width<="768"){
            var smallimg=$(".smallimg");
            for(var i=0;i<smallimg.length;i++){
                smallimg.eq(i).attr("src",smallimg.eq(i).data("src"));
            }
        }

        var t=setInterval(move,3000);

        function move(){
            $(".banner-menu-li").removeClass("active");
            if(moveleft == -bigimgwidth){
                moveleft="0";
                index=0;
            }
            $(".banner-menu-li").eq(index).addClass("active");
            bigimg.animate({left:parseInt(moveleft)+"px"},"slow");
            moveleft=parseInt(moveleft)+parseInt(-width);
            index++;
        }
    </script>
</section>
<!-- banner图片轮播结束 -->
<!-- 产品与服务开始 -->
<section id="server">
    <div class="container-fuild">
        <div class="server-head">
            <div class="server-head-title">产品与服务</div>
            <div class="server-head-desc">我们不停的在接入更多可能成为支付的服务商和产品,包括虚拟币</div>
        </div>
        <div class="server-body">
            <div class="server-body-list">
                <div class="server-body-list-img server1"></div>
                <div class="server-body-list-text title">聚合支付</div>
                <div class="server-body-list-text">全支付场景覆盖</div>
                <div class="server-body-list-text">主流支付通道支持</div>
            </div>
            <div class="server-body-list">
                <div class="server-body-list-img server2"></div>
                <div class="server-body-list-text title">微信支付</div>
                <div class="server-body-list-text">安全便捷实时到账</div>
                <div class="server-body-list-text">移动支付的主流市场</div>
            </div>
            <div class="server-body-list">
                <div class="server-body-list-img server3"></div>
                <div class="server-body-list-text title">银联支付</div>
                <div class="server-body-list-text">支持多家银行</div>
                <div class="server-body-list-text">安全支付随心选择</div>
            </div>
            <div class="server-body-list">
                <div class="server-body-list-img server4"></div>
                <div class="server-body-list-text title">支付宝支付</div>
                <div class="server-body-list-text">支付便捷灵活</div>
                <div class="server-body-list-text">线下支付经营必备</div>
            </div>
        </div>
    </div>
    <script>
        $(".server-body-list").mouseover(function(){
            var index=$(this).index();
            $(".server-body-list-img").eq(index).css("background-image","url(/web/css/images/server_color"+(index+1)+".png)");
        });
        $(".server-body-list").mouseout(function(){
            var index=$(this).index();
            $(".server-body-list-img").eq(index).css("background-image","url(/web/css/images/server"+(index+1)+".png)");
        });
    </script>
</section>
<!-- 产品与服务结束 -->
<!-- 优势开始 -->
<section id="advan">
    <div class="container-fuild">
        <div class="advan-head">
            <div class="advan-head-title">我们的优势</div>
            <div class="advan-head-desc">灵活便捷的产品服务组合，稳定可靠的运维管理，便捷简单的接入方式</div>
        </div>
        <div class="advan-body">
            <div class="container">
                <div class="row ">
                    <div class="hidden-xs hidden-sm col-md-6 col-lg-6 col-sm-6 advan-body-list advan-body-img"></div>
                    <div class="col-xs-12 col-md-6 col-lg-6 col-sm-12 advan-body-list">
                        <div class='row advan-body-item'>
                            <div class="col-xs-2"></div>
                            <div class="col-xs-8">
                                <div class="advan-body-li">
                                    <div class="num one"></div>
                                    <div class="advan-body-li-config">
                                        <div class="advan-body-li-config-title">安全便利</div>
                                        <div class="advan-body-li-config-desc">多种方式随心选择,满足您的不同需求</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xs-2"></div>
                        </div>
                        <div class="row advan-body-item">
                            <div class="col-xs-2"></div>
                            <div class="col-xs-8">
                                <div class="advan-body-li">
                                    <div class="num two"></div>
                                    <div class="advan-body-li-config">
                                        <div class="advan-body-li-config-title">稳定可靠</div>
                                        <div class="advan-body-li-config-desc">两地三中心容灾系统,确保服务稳定,最快完成交易</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xs-2"></div>
                        </div>
                        <div class="row advan-body-item">
                            <div class="col-xs-2"></div>
                            <div class="col-xs-8">
                                <div class="advan-body-li">
                                    <div class="num three"></div>
                                    <div class="advan-body-li-config">
                                        <div class="advan-body-li-config-title">不介入资金流</div>
                                        <div class="advan-body-li-config-desc">慧富宝,只负责交易处理,不参与资金结算</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xs-2"></div>
                        </div>
                    </div>
                </div>
                <div class="row nummores">
                    <div class="col-xs-4 col-md-4 col-sm-4 col-lg-4">
                        <div class="row">
                            <div class="col-xs-3 col-md-5 col-sm-3 col-lg-5 nummore">50+</div>
                            <div class="col-xs-9 col-md-7 col-sm-9 col-lg-7">支付接口测试</div>
                        </div>
                    </div>
                    <div class="col-xs-4 col-md-4 col-sm-4 col-lg-4 ">
                        <div class="row">
                            <div class="col-xs-3 col-md-5 col-sm-3 col-lg-5 nummore">10+</div>
                            <div class="col-xs-9 col-md-7 col-sm-9 col-lg-7">常见支付渠道</div>
                        </div>
                    </div>
                    <div class="col-xs-4 col-md-4 col-sm-4 col-lg-4 ">
                        <div class="row">
                            <div class="col-xs-3 col-md-5 col-sm-3 col-lg-5 nummore">200M+</div>
                            <div class="col-xs-9 col-md-7 col-sm-9 col-lg-7">流水统计</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<!-- 优势结束 -->
<!-- 合作伙伴开始 -->
<section id="partner">
    <div class="container">
        <!-- <div class="partner-head">
          <div class="partner-head-title">合作伙伴</div>
        </div> -->
        <div class="partner-body">

        </div>
    </div>
</section>
<!-- 合作伙伴结束 -->
<!-- 底部开始 -->
<section id="footer">
    <div class='container-fuild'>
        <div class='bgimg'>
            <a href="register.jsp">立即注册</a>
            <div class="copy">
                侯维科技有限公司旗下产品慧富宝&nbsp;&nbsp;&nbsp;渝IPC备16015651-3
            </div>
        </div>
    </div>
</section>
<!-- 底部结束 -->
<!-- 登陆框 -->
<section id="login">
    <div class="login-zz"></div>
    <form class="form-horizontal" id="formlogin" method="post" role="form" action="/user/user/login">
        <div class="login-box">
            <div class="login-title">商户登陆</div>
            <div class="login-name">
                <input type="text" class="form-control uname" id="loginId" name="loginId" placeholder="用户名" required="" minlength="2" aria-required="true"/>
            </div>
            <div class="login-password">
                <input type="password" class="form-control pword m-b" id="password" name="password" placeholder="密码" required="" aria-required="true"/>
            </div>
            <div class="login-btn">
                <button class="btn btn-success btn-block">登录</button>
            </div>
            <p>还没有账号?<a href="register.jsp">立即注册</a>
            <div class="colse">×</div>
            <script>
                $(".colse").click(function(){
                    $(".login-zz").css("display","none");
                    $(".login-box").css("display","none");
                });
            </script>
        </div>
    </form>
</section>
<script src="Public/Front/js/jquery.min.js?v=2.1.4"></script>
<script src="Public/Front/js/bootstrap.min.js?v=3.3.6"></script>
<script src="Public/Front/bootstrapvalidator/js/bootstrapValidator.min.js"></script>
<script src="Public/Front/js/plugins/layer/layer.min.js"></script>
<script src="Public/Front/js/login.js" type="text/javascript"></script>

</body>
</html>
