package com.hivecloud.plugin.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息 VO
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@Schema(description = "文件信息")
public class FileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件主键 ID
     */
    @Schema(description = "文件 ID", example = "1234567890")
    private Long id;

    /**
     * 文件名称
     */
    @Schema(description = "文件名", example = "test.txt")
    private String fileName;

    /**
     * 文件访问 URL
     */
    @Schema(description = "访问 URL", example = "http://example.com/files/test.txt")
    private String fileUrl;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小（字节）", example = "1024")
    private Long fileSize;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型", example = "text/plain")
    private String fileType;

    /**
     * 文件扩展名
     */
    @Schema(description = "扩展名", example = "txt")
    private String fileExt;

    /**
     * 存储策略
     */
    @Schema(description = "存储策略", example = "local")
    private String storageType;

    /**
     * 上传者 ID
     */
    @Schema(description = "上传者 ID", example = "1001")
    private Long uploadUserId;

    /**
     * 上传者姓名
     */
    @Schema(description = "上传者姓名", example = "张三")
    private String uploadUserName;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间", example = "2026-04-27 10:00:00")
    private LocalDateTime uploadTime;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数", example = "10")
    private Integer downloadCount;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "用户头像")
    private String remark;
}
