package com.ddd.chulsi.infrastructure.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.ddd.chulsi.infrastructure.exception.FileGetFailedException;
import com.ddd.chulsi.infrastructure.exception.FileUploadException;
import com.ddd.chulsi.infrastructure.util.DateUtil;
import com.ddd.chulsi.infrastructure.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class S3Provider implements FileProvider {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.endpoint}")
    private String endpoint;

    private final AmazonS3 amazonS3;

    private final static String DEFAULT_UPLOAD_PATH = "upload";

    private String createUploadFileName(MultipartFile file) {
        return String.format("%s_%s.%s",
            UUID.randomUUID().toString().replaceAll("-", ""),
            DateUtil.getNowDateTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            FileUtil.getFileExt(file)
        );
    }

    @Override
    public Resource getResource(String filePath) {
        try {
            final S3Object s3Object = amazonS3.getObject(bucketName, filePath);
            final S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
            final byte[] bytes = IOUtils.toByteArray(s3ObjectInputStream);
            return new ByteArrayResource(bytes);
        } catch (IOException e) {
            throw new FileGetFailedException();
        }
    }

    @Override
    public FileInfoDTO uploadFile(String path, MultipartFile uploadFile) {
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(uploadFile.getContentType());
        objectMetadata.setContentLength(uploadFile.getSize());
        try {
            final String uploadPath = DEFAULT_UPLOAD_PATH + "/" + path;
            final String uploadFileName = createUploadFileName(uploadFile);
            final String originFileName = uploadFile.getOriginalFilename();
            final String contentType = uploadFile.getContentType();
            final String ext = FileUtil.getFileExt(uploadFile);
            final long size = uploadFile.getSize();
            amazonS3.putObject(new PutObjectRequest(bucketName, uploadPath + "/" + uploadFileName, uploadFile.getInputStream(), objectMetadata));
            final String uploadFullPath = endpoint + uploadPath + File.separator + uploadFileName;

            return new FileInfoDTO(uploadPath, uploadFileName, originFileName, uploadFullPath, contentType, ext, size);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileUploadException();
        }
    }

    @Override
    public List<FileInfoDTO> uploadFiles(String path, List<MultipartFile> uploadFiles) {
        return uploadFiles.stream()
            .map(file -> uploadFile(path, file))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath != null) {
            final DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
            deleteObjectsRequest.setKeys(List.of(new DeleteObjectsRequest.KeyVersion(filePath)));
            amazonS3.deleteObjects(deleteObjectsRequest);
        }
    }

    @Override
    public void deleteFiles(Set<String> filePaths) {
        if (filePaths != null && filePaths.size() > 0) {
            final DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
            deleteObjectsRequest.setKeys(filePaths.stream()
                .map(DeleteObjectsRequest.KeyVersion::new)
                .collect(Collectors.toList()));
            amazonS3.deleteObjects(deleteObjectsRequest);
        }
    }

}
