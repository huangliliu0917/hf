<?php
$key = 'XyEe2dK7QmRFDFsJeRAZmwfHXBzziNmk';
$money  = $_GET['money'] * 100;
$obj = array();
$obj['version'] = "1.0";
$obj['service'] = "09"; //09网银
$obj['merchant_no'] = "13588"; //商户编号
$obj['total'] =  '100'; //金额 分
$obj['out_trade_no'] = "1234542156235216";//$_GET['orderid']; //交易号，唯一
$obj['bank_code'] = "ICBC";
#$obj['name'] = $_GET['orderid']; //交易号，唯一
$obj['create_ip'] = "127.0.0.1";
$obj['nonce_str'] = (string)mt_rand(); //随机数
$obj['sign_type'] = "MD5";
$obj['sign'] = get_sign( $obj, $key ); //签名 签名算法参考文档

$url = "http://huifufu.cn/openapi/unifiedorder";
$data_string = json_encode($obj);
$wait = 30;
$ch = curl_init($url);
curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, $wait);
curl_setopt($ch, CURLOPT_TIMEOUT, 0);
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
curl_setopt($ch, CURLOPT_POSTFIELDS, $data_string);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, array(
    'Content-Type: application/json',
    'Content-Length: ' . strlen($data_string)
));

$result = curl_exec($ch);
curl_close($ch);
print_r($result);

function get_sign( $data, $key  )
{
    ksort( $data );
    $str = '';
    foreach( $data as $k => $v )
    {
        $str .= ( $k.'='.$v.'&');
    }
    $str .= 'key='.$key;

    return strtoupper( md5(  $str ) );
}

?>