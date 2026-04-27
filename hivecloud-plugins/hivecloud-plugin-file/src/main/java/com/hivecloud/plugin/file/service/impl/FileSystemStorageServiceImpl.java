package com.hivecloud.plugin.file.service.impl;

import com.hivecloud.plugin.file.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 本地文件存储服务实现
 * 将文件存储到服务器本地文件系统
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Service
public class FileSystemStorageServiceImpl implements StorageService {

    /**
     * 本地存储根路径
     */
    @Value("${file.storage.local.base-path:./uploads}")
    private String basePath;

    /**
     * 文件访问基础 URL
     */
    @Value("${file.storage.local.base-url:/files}")
    private String baseUrl;

    /**
     * 上传文件
     *
     * @param file 文件对象
     * @param path 存储路径（目录）
     * @return 文件访问 URL
     * @throws Exception 上传失败时抛出异常
     */
    @Override
    public String upload(MultipartFile file, String path) throws Exception {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 生成唯一文件名
        String filename = generateUniqueFilename(originalFilename);
        return upload(file, path, filename);
    }

    /**
     * 上传文件（指定文件名）
     *
     * @param file 文件对象
     * @param path 存储路径（目录）
     * @param filename 文件名
     * @return 文件访问 URL
     * @throws Exception 上传失败时抛出异常
     */
    @Override
    public String upload(MultipartFile file, String path, String filename) throws Exception {
        log.info("开始上传文件到本地存储，path:{}, filename:{}, size:{}", path, filename, file.getSize());

        // 验证文件
        validateFile(file);

        // 构建完整路径
        String fullPath = buildFullPath(path, filename);

        // 创建目录
        Path targetPath = Paths.get(fullPath);
        Files.createDirectories(targetPath.getParent());

        // 保存文件
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 生成访问 URL
        String url = buildFileUrl(path, filename);

        log.info("文件上传成功，url:{}", url);
        return url;
    }

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @param outputStream 输出流
     * @throws Exception 下载失败时抛出异常
     */
    @Override
    public void download(String filePath, OutputStream outputStream) throws Exception {
        log.info("开始下载本地文件，filePath:{}", filePath);

        Path filePathObj = Paths.get(basePath, filePath);
        if (!Files.exists(filePathObj)) {
            throw new FileNotFoundException("文件不存在：" + filePath);
        }

        Files.copy(filePathObj, outputStream);
        log.info("文件下载成功");
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @throws Exception 删除失败时抛出异常
     */
    @Override
    public void delete(String filePath) throws Exception {
        log.info("开始删除本地文件，filePath:{}", filePath);

        Path filePathObj = Paths.get(basePath, filePath);
        if (Files.exists(filePathObj)) {
            Files.delete(filePathObj);
            log.info("文件删除成功");
        } else {
            log.warn("文件不存在，无需删除：{}", filePath);
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return true-存在 false-不存在
     */
    @Override
    public boolean exists(String filePath) {
        Path filePathObj = Paths.get(basePath, filePath);
        return Files.exists(filePathObj);
    }

    /**
     * 获取文件输入流
     *
     * @param filePath 文件路径
     * @return 文件输入流
     * @throws Exception 获取失败时抛出异常
     */
    @Override
    public InputStream getInputStream(String filePath) throws Exception {
        Path filePathObj = Paths.get(basePath, filePath);
        if (!Files.exists(filePathObj)) {
            throw new FileNotFoundException("文件不存在：" + filePath);
        }
        return Files.newInputStream(filePathObj);
    }

    /**
     * 获取文件访问 URL
     *
     * @param filePath 文件路径
     * @return 文件访问 URL
     */
    @Override
    public String getUrl(String filePath) {
        return baseUrl + "/" + filePath;
    }

    /**
     * 存储策略类型
     *
     * @return 存储策略标识
     */
    @Override
    public String getType() {
        return "local";
    }

    /**
     * 验证文件
     *
     * @param file 文件对象
     * @throws Exception 验证失败时抛出异常
     */
    private void validateFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 检查文件大小（默认最大 10MB）
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小不能超过 10MB");
        }

        // 检查文件类型（可根据需要扩展）
        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            throw new IllegalArgumentException("无法识别的文件类型");
        }
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFilename(String originalFilename) {
        String ext = getFileExtension(originalFilename);
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "_" + uuid + ext;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名（包含点）
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot).toLowerCase();
        }
        return "";
    }

    /**
     * 构建完整路径
     *
     * @param path 相对路径
     * @param filename 文件名
     * @return 完整路径
     */
    private String buildFullPath(String path, String filename) {
        return basePath + "/" + path + "/" + filename;
    }

    /**
     * 构建文件访问 URL
     *
     * @param path 相对路径
     * @param filename 文件名
     * @return 访问 URL
     */
    private String buildFileUrl(String path, String filename) {
        return baseUrl + "/" + path + "/" + filename;
    }
}
