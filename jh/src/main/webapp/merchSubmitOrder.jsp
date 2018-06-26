<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>一键快捷产品商户提交支付</title>
    <style type="text/css">
        input, select, textarea {
            width: 500px;
            position: absolute;
            left: 300px;
        }
    </style>
    <script type="text/JavaScript" src="${pageContext.request.contextPath}/scripts/jquery-1.7.2.min.js"></script>
    <script type="text/JavaScript" src="${pageContext.request.contextPath}/scripts/DateFormatter.js"></script>
</head>
<body style="width:1200px">
<div style="width:1000px">

    <div>
        <form id="orderSignForm" method="post">
            <div>
                <h3>报文头</h3>
                <div>
                    <label>[version]版本号:</label>
                    <input type="text" name="version" id="version" value="1.0"/>
                </div>
                <div>
                    <label>[method]接口名称:</label>
                    <input type="text" name="method" id="method" value="sandPay.fastPay.quickPay.index"/>
                </div>
                <div>
                    <label>[productId]产品编码:</label>
                    <select name="productId" id="productId">
                        <option value="00000016">00000016-一键快捷产品</option>
                    </select>
                </div>
                <div>
                    <label>[accessType]接入类型:</label>
                    <select name="accessType" id="accessType">
                        <option value="1">1-普通商户接入（目前支持1）</option>
                        <option value="2">2-平台商户接入</option>
                    </select>
                </div>
                <div>
                    <label>[mid]商户号:</label>
                    <input type="text" name="mid" id="mid" value="10020025"/>
                </div>
                <div>
                    <label>[plMid]平台商户号:</label>
                    <input type="text" name="plMid" id="plMid" value=""/>
                </div>
                <div>
                    <label>[channelType]渠道类型:</label>
                    <select name="channelType" id="channelType">
                        <option value="07">07-互联网</option>
                        <option value="08">08-移动端</option>
                    </select>
                </div>
                <div>
                    <label>[reqTime]请求时间:</label>
                    <input type="text" name="reqTime" id="reqTime" value=""/>
                </div>
            </div>

            <div>
                <h3>报文体</h3>
                <div>
                    <label>[userId]用户ID:</label>
                    <input type="text" name="userId" id="userId" value="000004"/>
                </div>
                <div>
                    <label>[orderCode]商户订单号:</label>
                    <input type="text" name="orderCode" id="orderCode" value="SH000001"/>
                </div>
                <div>
                    <label>[orderTime]商户订单时间:</label>
                    <input type="text" name="orderTime" id="orderTime" value=""/>
                </div>
                <div>
                    <label>[totalAmount]订单金额:</label>
                    <input type="text" name="totalAmount" id="totalAmount" value="000000000001"/>
                </div>
                <div>
                    <label>[subject]订单标题:</label>
                    <input type="text" name="subject" id="subject" value="test01Title"/>
                </div>
                <div>
                    <label>[body]订单描述:</label>
                    <input type="text" name="body" id="body" value="test01Desc"/>
                </div>
                <div>
                    <label>[currencyCode]币种:</label>

                    <input type="text" name="currencyCode" id="currencyCode" value="156"/>
                </div>
                <div>
                    <label>[notifyUrl]异步通知地址:</label>
                    <input type="text" name="notifyUrl" id="notifyUrl"
                           value="http://192.168.22.133:8088/moon-servlet01/test02"/>
                </div>
                <div>
                    <label>[frontUrl]前台通知地址:</label>
                    <input type="text" name="frontUrl" id="frontUrl"
                           value="http://192.168.22.133:8088/moon-servlet01/test01"/>
                </div>
                <div>
                    <label>[clearCycle]清算模式:</label>
                    <select name="clearCycle" id="clearCycle">
                        <option value="0">0：T1 (默认)</option>
                        <option value="1">1：T0</option>
                        <option value="2">2：D0</option>
                    </select>
                </div>
                <div>
                    <label>[extend]扩展域:</label>
                    <input type="text" name="extend" id="extend" value=""/>
                </div>
            </div>
        </form>
    </div>
    <hr/>

    <div>
        <form id="orderPayForm" method="post">
            <h3>请求整体报文</h3>
            <div>
                <label>[charset]编码:</label>
                <input type="text" name="charset" id="charset" value="UTF-8"/></br>
                <label>[signType]签名类型:</label>
                <input type="text" name="signType" id="signType" value="01"/></br>
                <label>[data]交易报文:</label>
                <textarea name="data" id="data"></textarea></br>
                <label>[sign]签名:</label>
                <textarea name="sign" id="sign"></textarea></br>
                <label>[extend]扩展域:</label>
                <input type="text" name="extend" id="extend" value=""/>
            </div>
        </form>
    </div>

    <hr/>

    <div>
        <div>
            <label>产品接入地址:</label>
            <select name="productAddr" id="productAddr">
                <option value="http://61.129.71.103:8003/">测试</option>
                <option value="http://localhost:8080/smp-pay-client/">本地</option>
            </select>        </div>
        <div>
            <input type="button" value="签名" id="buttonSign" onclick="javascript:forSign()"/>
            <br>
            <input type="button" value="去支付" id="buttonPay" onclick="javascript:forPay()"/>
        </div>
    </div>

</div>
</body>

<script type="text/javascript">

    // 填充默认的时间字段
    $(function () {
        var nowDate = new Date().format("yyyyMMddhhmmss");
        $("#reqTime").val(nowDate);
        $("#orderTime").val(nowDate);
        $("#orderCode").val("D" + nowDate);
    });

    // 异步获取签名
    function forSign() {
        $.ajax({
            cache: false,
            type: "POST",
            url: '/sandpay-fastpay-demo/fastPay',
            data: $('#orderSignForm').serialize(),// 你的formid
            async: false,
            error: function (request) {
                alert("Connection error");
            },
            success: function (r) {
                $("#data").val($.parseJSON(r).data);
                $("#sign").val($.parseJSON(r).sign);
            }
        });
    }

    // 发送支付请求
    function forPay() {
        $("#orderPayForm").attr("action", $("#productAddr").val() + 'fastPay/quickPay/index');
        $("#orderPayForm").submit();
    }

</script>
</html>