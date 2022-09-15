package com.cpy.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 文件处理器
 */
@Slf4j
@Component  // 注入容器
public class FileHandler {

    @Value("${reggie.path}")
    private String basePath;
    /**
     * 删除指定文件
     * @param imageName
     */
    public void removeImage(String imageName){
        String path = basePath + imageName;
        File file = new File(path);
        file.delete();
    }
}

