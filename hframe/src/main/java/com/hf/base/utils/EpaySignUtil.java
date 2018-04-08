package com.hf.base.utils;

import com.hf.base.exceptions.BizFailException;
import sun.misc.BASE64Decoder;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA签名公共类
 * @author kristain
 */
public class EpaySignUtil{

    private static EpaySignUtil instance;

    private EpaySignUtil()
    {

    }

    public static EpaySignUtil getInstance()
    {
        if (null == instance)
            return new EpaySignUtil();
        return instance;
    }

    /**
     * 签名处理
     * @param prikeyvalue：私钥文件
     * @param sign_str：签名源内容
     * @return
     */
    public static String sign(String prikeyvalue, String sign_str)
    {
        try
        {
			java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    Base64.getBytesBASE64(prikeyvalue));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey myprikey = keyf.generatePrivate(priPKCS8);
            // 用私钥对信息生成数字签名
            java.security.Signature signet = java.security.Signature
                    .getInstance("MD5withRSA");
            signet.initSign(myprikey);
            signet.update(sign_str.getBytes("UTF-8"));
            byte[] signed = signet.sign(); // 对信息的数字签名
            return new String(
                    org.apache.commons.codec.binary.Base64.encodeBase64(signed));
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new BizFailException(e.getMessage());
        }
    }

    /**
     * 签名验证
     * @param pubkeyvalue：公钥
     * @param oid_str：源串
     * @param signed_str：签名结果串
     * @return
     */
    public static boolean checksign(String pubkeyvalue, String oid_str,
            String signed_str)
    {
        try
        {
        	if(signed_str.contains(" ")){
        		signed_str = signed_str.replaceAll(" ", "+");
        	}
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    Base64.getBytesBASE64(pubkeyvalue));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(bobPubKeySpec);
            byte[] signed = Base64.getBytesBASE64(signed_str);// 这是SignatureData输出的数字签名
            java.security.Signature signetcheck = java.security.Signature
                    .getInstance("MD5withRSA");
            signetcheck.initVerify(pubKey);
            signetcheck.update(oid_str.getBytes("UTF-8"));
            return signetcheck.verify(signed);
        } catch (Exception e)
        {
        }
        return false;
    }
    
    /**
	 * 从字符串中加载公钥
	 * 
	 * @param publicKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载公钥时产生的异常
	 * @author zhangwl
	 */
	public static PublicKey getPublicKey(String publicKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
			return publicKey;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (IOException e) {
			throw new Exception("公钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}
	
	/**
	 * 从字符串中加载私钥
	 * 
	 * @param privateKeyStr
	 *            公钥数据字符串
	 * @throws Exception
	 *             加载私钥时产生的异常
	 * @author zhangwl
	 */
	public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
		try {
			BASE64Decoder base64Decoder = new BASE64Decoder();
			byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (IOException e) {
			throw new Exception("私钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}
	
	public static void main(String[] args) {
		//私钥
		String privateKey = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAm7H8eHHvTgovFU1J88Aa4FfFbwRIpzHJqz15VDHD5k4RhoCZGm6/Y0LNe9nJlfMmfDMPp4TWVsb66Qq7+f7UrwIDAQABAkAYtQX7lxkCqVsPZlR1+eZJ86PBGkztO1llczvtwHf18+2v0kjdSa9i9WghGMB4IsfEcj2LznYGNl0WrVnpdfMBAiEA4yB6yE0hdwwLtPD+7OAiS8CqAAZSWnlWyFEBX//bycsCIQCvfN3SyB5/KvyIoON8+9coiUVFPacEaiFY3qwA1LmULQIhAJ5WbkJlQwczJpYlzBJmzoHw9pK91XutS4qqrkK2pAqxAiA3EGM6RHjtRju/U1yOVyeIHKqTs2i4xeR40kX+bMFecQIhAMqwCplNKDUC/B3ac5Ewg7AnWE0eYOjDfcRMeFIcv/Ja";
		//待签名字符串
		String signStr = "callbackUrl=66666&isMoneyFilled=1&officeCode=20010001&orderNum=123456&payMoney=0.01&platformType=1";
		String sign = sign(privateKey, signStr);
		System.out.println(sign);
	}
}
