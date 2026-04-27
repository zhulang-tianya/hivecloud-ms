package com.hivecloud.plugin.crypto.service;

/**
 * 加密服务接口
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
public interface CryptoService {

    /**
     * AES 加密
     *
     * @param data 原始数据
     * @param key 密钥
     * @return 加密后的数据（Base64）
     */
    String aesEncrypt(String data, String key);

    /**
     * AES 解密
     *
     * @param encryptedData 加密后的数据（Base64）
     * @param key 密钥
     * @return 解密后的数据
     */
    String aesDecrypt(String encryptedData, String key);

    /**
     * MD5 加密
     *
     * @param data 原始数据
     * @return MD5 值（32 位十六进制）
     */
    String md5(String data);

    /**
     * SHA256 加密
     *
     * @param data 原始数据
     * @return SHA256 值（64 位十六进制）
     */
    String sha256(String data);
}
