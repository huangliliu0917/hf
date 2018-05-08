<%@ page import="org.apache.commons.lang.math.RandomUtils" %>
<%@ page import="java.net.InetAddress" %>
<%@ page import="com.hf.base.utils.Utils" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head> 
<meta http-equiv="Content-Type"	content="text/html; charset=utf-8" />
<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script src="http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<title>慧富支付-提现</title>
<style type="text/css">
	body { padding-top: 70px; }
</style>
</head> 
<body> 
<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
  <div class="container">
  	<a class="navbar-brand"><strong>慧富提现demo（<s>ie1-9</s>）</strong></a>
  </div>
</nav>

<%
	String outTradeNo = String.valueOf(RandomUtils.nextLong());
%>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-6 col-md-offset-3">
			<form role="form" action="settle/agentPay" method="post"  name="payForm" >
				<div class="form-group">
					<label for="orderNum">订单号<span><font color="red">*</font><font size="1">需保证唯一</font></span></label>
					<input type="text" class="form-control" name="orderNum" id="orderNum" value="<%=outTradeNo%>" required>
				</div>
				<div class="form-group">
					<label for="payMoney">代付金额<span><font color="red">*</font><font size="1">单位:分</font></span></label>
					<input type="text" class="form-control" name="payMoney" id="payMoney" value="" required>
				</div>
				<div class="form-group">
					<label for="bankCode">银行</label>
					<select name="bankCode" id="bankCode">
						<option value="">选择支付银行</option>
						<option value="ICBC">中国工商银行</option>
						<option value="ABC">中国农业银行</option>
						<option value="BOC">中国银行</option>
						<option value="CCB">中国建设银行</option>
						<option value="BOCOM">交通银行</option>
						<option value="CMB">招商银行</option>
						<option value="CEB">光大银行</option>
						<option value="CMBC">民生银行</option>
						<option value="PSBC">中国邮政储蓄银行</option>
						<option value="SPDB">浦发银行</option>
						<option value="CNCB">中信银行</option>
						<option value="PAB">平安银行</option>
						<option value="HXB">华夏银行</option>
						<option value="CIB">兴业银行</option>
						<option value="BOHC">渤海银行</option>
						<option value="BCCB">北京银行</option>
						<option value="GDB">广发银行</option>
						<option value="BOS">上海银行</option>
						<option value="ZSBC">浙商银行</option>
						<option value="NBBC">宁波银行</option>
						<option value="NJBC">南京银行</option>
						<option value="BRCB">北京农村商业银行</option>
						<option value="ZHTLCB">浙江泰隆商业银行</option>
						<option value="BEA">东亚银行</option>
						<option value="HZB">杭州银行</option>
					</select>
				</div>
				<div class="form-group">
					<label for="bankAccount">银行卡号<span><font color="red">*</font></span></label>
					<input type="text" class="form-control" name="bankAccount" id="bankAccount" required>
				</div>
				<div class="form-group">
					<label for="accountName">开户名<span><font color="red">*</font></span></label>
					<input type="text" class="form-control" name="accountName" id="accountName" required>
				</div>
				<div class="form-group">
					<label for="certNo">身份证号<span><font color="red">*</font></span></label>
					<input type="text" class="form-control" name="certNo" id="certNo" required>
				</div>
				<div class="form-group">
					<label for="tel">手机号<span><font color="red">*</font></span></label>
					<input type="text" class="form-control" name="tel" id="tel" required>
				</div>
				<div class="form-group">
					<label for="password">密码<span><font color="red">*</font></span></label>
					<input type="password" class="form-control" name="password" id="password" value="" required>
				</div>
				<button type="submit" class="btn btn-primary">提交</button>
			</form>
		</div>
	</div>
</div>
</body>
 
</html>