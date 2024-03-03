package com.sikdorok.appapi.infrastructure.util;

import com.sikdorok.appapi.infrastructure.exception.FileGetFailedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {

    public static String getFileNameByAgent(String userAgent, String fileName) {
        if (userAgent.contains("Edge")){
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) { // IE 11버전부터 Trident로 변경되었기때문에 추가해준다.
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } else if (userAgent.contains("Chrome")) {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } else if (userAgent.contains("Opera")) {
            return new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        } else if (userAgent.contains("Firefox")) {
            return new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        } else {
            return new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
    }

    public static HttpHeaders getHeader(String fileName, long contentLength) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(contentLength);
        httpHeaders.setContentDispositionFormData("attachment", fileName);
        httpHeaders.setPragma("no-cache;");
        httpHeaders.setExpires(-1);
        return httpHeaders;
    }

    public static long getResourceContentLength(Resource resource) {
        try {
            return resource.contentLength();
        } catch (IOException e) {
            throw new FileGetFailedException();
        }
    }

    public static List<MultipartFile> filterEmptyFiles(List<MultipartFile> files) {
        return files == null ? null :
            files.stream()
                .filter(file -> StringUtils.isNotBlank(file.getOriginalFilename()) && file.getSize() > 0)
                .collect(Collectors.toList());
    }

    public static String getFileExt(MultipartFile file) {
        return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }

}
