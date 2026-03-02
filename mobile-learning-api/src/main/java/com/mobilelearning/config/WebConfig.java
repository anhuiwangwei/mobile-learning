package com.mobilelearning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态文件访问
        String absolutePath = new File(uploadPath).getAbsolutePath();
        
        // 配置 videos 目录
        registry.addResourceHandler("/static/videos/**")
                .addResourceLocations("file:" + absolutePath + "/videos/");
        
        // 配置 pdfs 目录
        registry.addResourceHandler("/static/pdfs/**")
                .addResourceLocations("file:" + absolutePath + "/pdfs/");
        
        // 配置 images 目录
        registry.addResourceHandler("/static/images/**")
                .addResourceLocations("file:" + absolutePath + "/images/");
        
        // 配置 files 目录
        registry.addResourceHandler("/static/files/**")
                .addResourceLocations("file:" + absolutePath + "/files/");
    }
}
