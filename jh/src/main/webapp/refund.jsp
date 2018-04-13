 <%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
  	<a class="navbar-brand"><strong>退款交易（<s>ie1-9</s>）</strong></a>
  </div>
</nav>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-6 col-md-offset-3">
			<form role="form" action="refund.do" method="post"  name="payForm" >
				<div class="form-group">
					<label for="merchantNo">商户号</label>
					<input type="text" class="form-control" name="merchantNo" id="merchantNo" value='S20170907011890' required>
				</div>
				<div class="form-group">
					<label for="tranReqUrl">交易地址</label>
					<input type="text" class="form-control" name="tranReqUrl" id="tranReqUrl" value='http://paygate.hefupal.cn/paygate/v1/kjpay' required>
				</div>
				<div class="form-group">
					<label for="oldTranSerialNum">原交易流水号</label>
					<input type="text" class="form-control" name="oldTranSerialNum" value="20180326104550951" required>	
				</div>
				<div class="form-group">
					<label for="amount">退款金额(单位分)</label>
					<input type="text" class="form-control" name="amount"  max-length="100" value="20" required>	
				</div>
				<div class="form-group">
					<label for="refundReason">退款原因</label>
					<input type="text" class="form-control" name="refundReason" value="测试退款"  max-length="100">	
				</div>
				<button type="submit" class="btn btn-primary">提交</button>
			</form>
		</div>
	</div>
</div>

		
</body>
 
</html>