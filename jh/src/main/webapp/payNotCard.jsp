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
	Date date = new Date();
	String tranDate = new SimpleDateFormat("yyyyMMdd").format(date);
	String tranTime = new SimpleDateFormat("HHmmss").format(date);
	String SSS = new SimpleDateFormat("SSS").format(date);
	
	String urserId = "U" + tranDate + tranTime + SSS;
%>
</head> 
<body> 
<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
  <div class="container">
  	<a class="navbar-brand" href="index.html"><strong>首页</strong></a>
  	<a class="navbar-brand"><strong>支付(不需要绑卡)（<s>ie1-9</s>）</strong></a>
  </div>
</nav>

<div class="container-fluid">
	<div class="row">
		<div class="col-md-6 col-md-offset-3">
			<form role="form" action="payNotCard.do" method="post"  name="bindForm">
				<div class="form-group">
					<label for="merchantNo">商户号</label>
					<input type="text" class="form-control" name="merchantNo" id="merchantNo" value="S20170907011890" required>
				</div>
				<div class="form-group">
					<label for="tranReqUrl">交易地址</label>
					<input type="text" class="form-control" name="tranReqUrl" id="tranReqUrl" value="http://paygate.hefupal.cn/paygate/v1/kjpay" required>
				</div>
				<div class="form-group">
					<label for="notifyUrl">通知地址</label>
					<input type="text" class="form-control" name="notifyUrl" id="notifyUrl" value="http://d42447dd.ngrok.io/quickpay/payNotCardNotify.do" required>
				</div>
				<div class="form-group">
					<label for="cardNum">银行账号</label>
					<input type="text" class="form-control" name="cardNum" id="cardNum" value='6230580000129694886' required>
				</div>
				<div class="form-group">
					<label for="userName">开户名</label>
					<input type="text" class="form-control" name="userName" id="userName" value='彭森林' required>
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
					<input type="text" class="form-control" name="certificateNum" id="certificateNum" value='421022199001103916' required>
				</div>
				<div class="form-group">
					<label for="mobile">手机号</label>
					<input type="text" class="form-control" name="mobile" id="mobile" value='18600973136' required>
				</div>
				<div class="form-group">
					<label for="userId">用户编号（USERID）</label>
					<input type="text" class="form-control" name="userId" id="userId" required value="<%=urserId %>">
				</div>
				<div class="form-group">
					<label for="goodsName">商品名称</label>
					<input type="text" class="form-control" name="goodsName"  max-length="100" value="Q测试商品" required>	
				</div>
				<div class="form-group">
					<label for="goodsInfo">商品信息</label>
					<input type="text" class="form-control" name="goodsInfo" value="Q测试商品信息"  max-length="100">	
				</div>
				<div class="form-group">
					<label for="amount">交易金额(单位分)</label>
					<input type="number" class="form-control" name="amount" min="1" value="20"  max="9999999999" required>	
				</div>
				<div class="form-group">
					<label for="bizType">业务代码</label>
					<select class="form-control" name="bizType">
						<option value="14900">其他费用</option>
						<option value="10101">家用电费</option>
						<option value="10102">生产用电费</option>
						<option value="10201">用水费</option>
						<option value="10202">排水费</option>
						<option value="10203">直饮水费</option>
						<option value="10204">污水处理费</option>
						<option value="10205">暖气费</option>
						<option value="10300">煤气费</option>
						<option value="10301">管道煤气费</option>
						<option value="10400">电话费</option>
						<option value="10401">市内电话费</option>
						<option value="10402">长途电话费</option>
						<option value="10403">移动电话费</option>
						<option value="10404">电话初装费</option>
						<option value="10405">IP电话费</option>
						<option value="10500">通讯费</option>
						<option value="10501">数据通讯费</option>
						<option value="10502">线路月租费</option>
						<option value="10503">代维费</option>
						<option value="10504">网络使用费</option>
						<option value="10505">信息服务费</option>
						<option value="10506">移动电子商务费</option>
						<option value="10507">网关业务费</option>
						<option value="10508">手机话费</option>
						<option value="10600">保险费</option>
						<option value="10601">续期寿险费</option>
						<option value="10602">社会保险费</option>
						<option value="10603">养老保险费</option>
						<option value="10604">医疗保险费</option>
						<option value="10605">车辆保险费</option>
						<option value="10700">房屋管理费</option>
						<option value="10701">房屋租赁费</option>
						<option value="10702">租赁服务费</option>
						<option value="10703">物业管理费</option>
						<option value="10704">清洁费</option>
						<option value="10705">保安服务费</option>
						<option value="10706">电梯维护保养费</option>
						<option value="10707">绿化费</option>
						<option value="10708">停车费</option>
						<option value="10800">代理服务费</option>
						<option value="10801">押运服务费</option>
						<option value="10802">票据传递费</option>
						<option value="10803">代理记账服务费</option>
						<option value="10900">学教费</option>
						<option value="10901">报考费</option>
						<option value="10902">学杂费</option>
						<option value="10903">保教费</option>
						<option value="11000">有线电视费</option>
						<option value="11001">有线电视租赁费</option>
						<option value="11002">移动电视费</option>
						<option value="11100">机构管理费用</option>
						<option value="11101">工商行政管理费</option>
						<option value="11102">商检费</option>
						<option value="14001">基金</option>
						<option value="14002">资管</option>
						<option value="14802">加油卡费</option>
						<option value="14901">还贷</option>
						<option value="14902">货款</option>
					</select>
				</div>
				<div class="form-group">
					<label for="buyerName">买家姓名</label>
					<input type="text" class="form-control" name="buyerName" value="彭森林" required>	
				</div>
				<div class="form-group">
					<label for="contact">买家联系方式</label>
					<input type="text" class="form-control" value="18600973136" name="contact">	
				</div>
				<button type="submit" class="btn btn-primary">提交</button>
			</form>
		</div>
	</div>
</div>

		
</body>
 
</html>