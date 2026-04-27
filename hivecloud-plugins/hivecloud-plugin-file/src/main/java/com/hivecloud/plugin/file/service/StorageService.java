package com.hivecloud.plugin.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件存储服务接口
 * 统一文件上传、下载、删除等操作，支持多种存储策略
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see FileSystemStorageServiceImpl
 * @see OssStorageServiceImpl
 * @see MinioStorageServiceImpl
 */
public interface StorageService {

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @param path 存储路径（目录）
     * @return 文件访问 URL
     * @throws Exception 上传失败时抛出异常
     */
    String upload(MultipartFile file, String path) throws Exception;

    /**
     * 上传文件（指定文件名）
     *
     * @param file 文件对象
     * @param path 存储路径（目录）
     * @param filename 文件名
     * @return 文件访问 URL
     * @throws Exception 上传失败时抛出异常
     */
    String upload(MultipartFile file, String path, String filename) throws Exception;

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @param outputStream 输出流
     * @throws Exception 下载失败时抛出异常
     */
    void download(String filePath, OutputStream outputStream) throws Exception;

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @throws Exception 删除失败时抛出异常
     */
    void delete(String filePath) throws Exception;

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return true-存在 false-不存在
     */
    boolean exists(String filePath);

    /**
     * 获取文件输入流
     *
     * @param filePath 文件路径
     * @return 文件输入流
     * @throws Exception 获取失败时抛出异常
     */
    InputStream getInputStream(String filePath) throws Exception;

    /**
     * 获取文件访问 URL
     *
     * @param filePath 文件路径
     * @return 文件访问 URL
     */
    String getUrl(String filePath);

    /**
     * 存储策略类型
     *
     * @return 存储策略标识
     */
    String getType();
}
