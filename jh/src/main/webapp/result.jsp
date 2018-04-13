<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head> 
<meta http-equiv="Content-Type"	content="text/html; charset=utf-8" />
<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script src="http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<title>合付宝支付-商户demo</title> 
<style type="text/css">
	body { padding-top: 70px; }
</style>

</head> 
<body> 
<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
  <div class="container">
  	<a class="navbar-brand" href="index.html"><strong>首页</strong></a>
  	<a class="navbar-brand"><strong>操作结果（<s>ie1-9</s>）</strong></a>
  </div>
</nav>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-6 col-md-offset-3">
			<div class="panel panel-info">
				<div class="panel-heading">操作结果</div>
				<div class="panel-body">
				    <p style="word-break: break-word;"><%=request.getAttribute("errorMsg")%></p>
				</div>
			</div>
			<div class="panel panel-info">
				<div class="panel-heading">返回具体参数</div>
				<div class="panel-body">
					<%
						Map<String, String> resultMap = (Map<String, String>)request.getAttribute("resultMap");
						for(String key : resultMap.keySet()) {
					%>
						    <p style="word-break: break-word;"><%=key %> : <%=resultMap.get(key) %></p>
					<%
						}
					%>	  
				</div>
			</div>
		</div>
	</div>
</div>

		
</body>
 
</html>