<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>pay demo</title>
</head>
<body>
    <form id="payDemo" method="post" action="jhTest">
        <table>
            <tr>
                <td>服务类型:</td>
                <td><input type="text" id="service" name="service"></td>
            </tr>
            <tr>
                <td>版本:</td>
                <td><input type="text" id="version" name="version"></td>
            </tr>
            <tr>
                <td>商户编号:</td>
                <td><input type="text" id="merchant_no" name="merchant_no"></td>
            </tr>
            <tr>
                <td>密钥:</td>
                <td><input type="text" id="cipherCode" name="cipherCode"></td>
            </tr>
            <tr>
                <td>订单金额:</td>
                <td><input type="text" id="total" name="total"></td>
            </tr>
            <tr>
                <td>订单名称:</td>
                <td><input type="text" id="name" name="name"></td>
            </tr>
            <tr>
                <td>订单号:</td>
                <td><input type="text" id="out_trade_no" name="out_trade_no"></td>
            </tr>
            <tr>
                <td>ip:</td>
                <td><input type="text" id="create_ip" name="create_ip"></td>
            </tr>
            <tr>
                <td>微信openid:</td>
                <td><input type="text" id="sub_openid" name="sub_openid"></td>
            </tr>
            <tr>
                <td>支付宝买家ID:</td>
                <td><input type="text" id="buyer_id" name="buyer_id"></td>
            </tr>
            <tr>
                <td>支付授权码:</td>
                <td><input type="text" id="authcode" name="authcode"></td>
            </tr>
            <tr>
                <td>银行编码:</td>
                <td><input type="text" id="bank_code" name="bank_code"></td>
            </tr>

            <tr>
                <td><input type="submit" value="提交"></td>
            </tr>
        </table>
    </form>
</body>

</html>
