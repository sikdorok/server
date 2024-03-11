package com.sikdorok.appapi.infrastructure.aws;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface FileProvider {
    Resource getResource(String filePath);
    FileInfoDTO uploadFile(String path, MultipartFile uploadFile);
    List<FileInfoDTO> uploadFiles(String path, List<MultipartFile> uploadFiles);
    void deleteFile(String filePath);
    void deleteFiles(Set<String> filePath);
}
