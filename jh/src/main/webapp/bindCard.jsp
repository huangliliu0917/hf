<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
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

<%
%>
</head> 
<body> 
<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
  <div class="container">
  	<a class="navbar-brand" href="index.html"><strong>首页</strong></a>
  	<a class="navbar-brand"><strong>签约绑卡（<s>IE1-9</s>）</strong></a>
  </div>
</nav>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-6 col-md-offset-3">
			<form role="form" action="bindCard.do" method="post"  name="bindForm">
				<div class="form-group">
					<label for="cardNum">银行账号</label>
					<input type="text" class="form-control" name="cardNum" id="cardNum" required>
				</div>
				<div class="form-group">
					<label for="userName">开户名</label>
					<input type="text" class="form-control" name="userName" id="userName" required>
				</div>
				<div class="form-group">
					<label for="certificateType">证件类型</label>
					<select class="form-control" name="certificateType" id="certificateType">
						<option value="ZR01">身份证</option>	
						<option value="ZR02">临时身份证</option>	
						<option value="ZR03">户口簿</option>	
						<option value="ZR04">军官证</option>	
						<option value="ZR05">警官证</option>	
						<option value="ZR06">士兵证</option>	
						<option value="ZR07">文职干部证</option>	
						<option value="ZR08">外国护照</option>	
						<option value="ZR09">香港通行证</option>	
						<option value="ZR10">澳门通行证</option>	
						<option value="ZR11">台湾通行证或有效旅行证件</option>	
						<option value="ZR12">军官退休证</option>	
						<option value="ZR13">中国护照</option>	
						<option value="ZR14">外国人永久居留证</option>	
						<option value="ZR15">军事学员证</option>	
						<option value="ZR16">离休干部荣誉证</option>	
						<option value="ZR17">边民出入境通行证</option>	
						<option value="ZR18">村民委员会证明</option>	
						<option value="ZR19">学生证</option>	
						<option value="ZR20">其它</option>	
						<option value="ZR21">护照</option>	
						<option value="ZR22">香港居民来往内地通行证</option>	
						<option value="ZR23">澳门居民来往内地通行证</option>	
						<option value="ZR24">台湾同胞来往内地通行证</option>	
					</select>
				</div>
				<div class="form-group">
					<label for="certificateNum">证件号码</label>
					<input type="text" class="form-control" name="certificateNum" id="certificateNum" required>
				</div>
				<div class="form-group">
					<label for="mobile">手机号</label>
					<input type="text" class="form-control" name="mobile" id="mobile" required>
				</div>
				<button type="submit" class="btn btn-primary">提交</button>
			</form>
		</div>
	</div>
</div>

		
</body>
 
</html>