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
<script type="text/javascript">
	var countdown=10; 
	function settime(val) { 
		if (countdown == 0) { 
			val.removeAttribute("disabled");    
			val.innerText="获取验证码"; 
			countdown = 10; 
			return;
		} else { 
			val.setAttribute("disabled", true); 
			val.innerText="重新发送(" + countdown + ")"; 
			countdown--; 
		} 
		
		setTimeout(function() { 
			settime(val); 
		},1000); 
	} 
	
	function resend(node){
		$.post("phoneCode.do", { "merchantNo": $("#merchantNo").val(), "tranReqUrl": $("#tranReqUrl").val(), "tranSerialNum": $("#tranSerialNum").val(), "oriTranCode": $("#oriTranCode").val() },
		   function(data){
		     if(data == '0000'){
		    	 settime(node);
		     }else{
		    	 alert("重发失败，请稍后再试！");
		     }
		   }
		);
	}
</script>
</head> 
<body> 
<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
  <div class="container">
  	<a class="navbar-brand" href="index.html"><strong>首页</strong></a>
  	<a class="navbar-brand"><strong>签约绑卡验证（<s>ie1-9</s>）</strong></a>
  </div>
</nav>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-6 col-md-offset-3">
			<form role="form" action="verify.do" method="post"  name="payForm">
				<div class="form-group">
					<label for="merchantNo">商户号</label>
					<input type="text" class="form-control" name="merchantNo" id="merchantNo" value="S20170907011890" required>
				</div>
				<div class="form-group">
					<label for="tranReqUrl">交易地址</label>
					<input type="text" class="form-control" name="tranReqUrl" id="tranReqUrl" value="http://paygate.hefupal.cn/paygate/v1/kjpay" required>
				</div>
				<div class="form-group">
					<label>绑定银行账号</label>
					<label><%=request.getAttribute("cardNum")%></label>
				</div>
				<div class="form-group">
					<label for="phoneCode">手机短信验证码（测试环境使用999999验证）</label>
					<div class="input-group">
						<input type="text" class="form-control" name="phoneCode" id="phoneCode" value = '999999' required maxlength=6>
				    	<span class="input-group-btn">
				        	<button class="btn btn-default" type="button" onclick="resend(this)">获取验证码</button>
				      	</span>
				    </div>
				</div>
				<input type="hidden" name="tranSerialNum" id="tranSerialNum" value='<%=request.getAttribute("tranSerialNum")%>'>
				<input type="hidden" name="oriTranCode" id="oriTranCode" value='<%=request.getAttribute("oriTranCode")%>'>
				<button type="submit" class="btn btn-primary">开通快捷支付</button>
			</form>
		</div>
	</div>
</div>

		
</body>
 
</html>