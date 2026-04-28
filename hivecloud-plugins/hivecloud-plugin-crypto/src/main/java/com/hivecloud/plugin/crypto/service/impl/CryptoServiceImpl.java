package com.hivecloud.plugin.crypto.service.impl;

import com.hivecloud.plugin.crypto.service.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 加密服务实现类
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
public class CryptoServiceImpl implements CryptoService {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_MODE = "AES/ECB/PKCS5Padding";

    @Override
    public String aesEncrypt(String data, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("AES 加密失败", e);
            throw new RuntimeException("AES 加密失败", e);
        }
    }

    @Override
    public String aesDecrypt(String encryptedData, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            log.error("AES 解密失败", e);
            throw new RuntimeException("AES 解密失败", e);
        }
    }

    @Override
    public String md5(String data) {
        return DigestUtils.md5Hex(data);
    }

    @Override
    public String sha256(String data) {
        return DigestUtils.sha256Hex(data);
    }
}
