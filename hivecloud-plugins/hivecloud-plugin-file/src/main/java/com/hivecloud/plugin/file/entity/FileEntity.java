package com.hivecloud.plugin.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息实体类
 * 用于存储文件上传记录、文件元数据等信息
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@TableName("t_file")
public class FileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件主键 ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 文件名称（原始文件名）
     */
    private String fileName;

    /**
     * 文件存储路径（相对路径或对象存储 key）
     */
    private String filePath;

    /**
     * 文件访问 URL
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME Type）
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExt;

    /**
     * 存储策略：local-本地、oss-阿里云 OSS、cos-腾讯云 COS、minio-MinIO
     */
    private String storageType;

    /**
     * 文件哈希值（MD5 或 SHA256）
     */
    private String fileHash;

    /**
     * 上传者 ID
     */
    private Long uploadUserId;

    /**
     * 上传者姓名
     */
    private String uploadUserName;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 最后下载时间
     */
    private LocalDateTime lastDownloadTime;

    /**
     * 文件状态：0-正常，1-禁用，2-已删除
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
