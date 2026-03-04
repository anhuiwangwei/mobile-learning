package com.mobilelearning.controller.admin;

import com.mobilelearning.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/file")
public class FileController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.base-url:http://localhost:8080}")
    private String baseUrl;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 验证文件类型
            if (!isAllowedFileType(extension)) {
                return Result.error("不支持的文件类型，视频只支持 MP4 格式，文档只支持 PDF 格式");
            }
            
            // 生成新文件名
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 根据文件类型确定存储目录
            String fileType = getFileType(extension);
            String dateDir = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            
            // 创建完整的目录路径
            Path uploadPathObj = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path typeDir = uploadPathObj.resolve(fileType);
            Path datePath = typeDir.resolve(dateDir);
            
            // 确保目录存在
            Files.createDirectories(datePath);
            
            // 保存文件
            Path filePath = datePath.resolve(newFilename);
            file.transferTo(filePath.toFile());
            
            // 生成访问 URL
            String url = baseUrl + "/static/" + fileType + "/" + dateDir + "/" + newFilename;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", originalFilename != null ? originalFilename : newFilename);
            result.put("newFilename", newFilename);
            result.put("size", String.valueOf(file.getSize()));
            result.put("type", fileType);
            
            return Result.success(result);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    private String getFileType(String extension) {
        if (extension == null) {
            return "files";
        }
        extension = extension.toLowerCase();
        if (extension.matches("\\.(jpg|jpeg|png|gif|webp|bmp)$")) {
            return "images";
        } else if (extension.matches("\\.(mp4)$")) {
            // 只允许 MP4 格式的视频
            return "videos";
        } else if (extension.matches("\\.(pdf)$")) {
            return "pdfs";
        } else {
            return "files";
        }
    }

    /**
     * 验证文件类型是否允许上传
     */
    private boolean isAllowedFileType(String extension) {
        if (extension == null) {
            return false;
        }
        extension = extension.toLowerCase();
        // 图片
        if (extension.matches("\\.(jpg|jpeg|png|gif|webp|bmp)$")) {
            return true;
        }
        // 视频（只允许 MP4）
        if (extension.matches("\\.(mp4)$")) {
            return true;
        }
        // PDF
        if (extension.matches("\\.(pdf)$")) {
            return true;
        }
        return false;
    }
}
