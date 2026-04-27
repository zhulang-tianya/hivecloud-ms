package com.hivecloud.plugin.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 文件上传请求 DTO
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadRequest {

    /**
     * 文件存储路径（目录）
     */
    @Schema(description = "存储路径", example = "/uploads/2026/04/")
    @NotBlank(message = "存储路径不能为空")
    private String path;

    /**
     * 自定义文件名（可选，不填则使用原始文件名）
     */
    @Schema(description = "自定义文件名", example = "my_file.txt")
    private String filename;

    /**
     * 存储策略（local/oss/cos/minio）
     */
    @Schema(description = "存储策略", example = "local")
    @NotBlank(message = "存储策略不能为空")
    private String storageType;

    /**
     * 文件所属业务类型
     */
    @Schema(description = "业务类型", example = "avatar")
    private String bizType;

    /**
     * 文件备注
     */
    @Schema(description = "备注", example = "用户头像")
    private String remark;
}
