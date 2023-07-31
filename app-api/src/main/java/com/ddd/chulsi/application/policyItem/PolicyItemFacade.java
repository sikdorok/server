package com.ddd.chulsi.application.policyItem;

import com.ddd.chulsi.domainCore.model.photos.Photos;
import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;
import com.ddd.chulsi.domainCore.model.photos.PhotosService;
import com.ddd.chulsi.domainCore.model.policyItem.PolicyItem;
import com.ddd.chulsi.domainCore.model.policyItem.PolicyItemCommand;
import com.ddd.chulsi.domainCore.model.policyItem.PolicyItemService;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.infrastructure.aws.FileProvider;
import com.ddd.chulsi.infrastructure.exception.NotFoundException;
import com.ddd.chulsi.infrastructure.jwt.JWTProperties;
import com.ddd.chulsi.infrastructure.jwt.JwtTokenUtil;
import com.ddd.chulsi.infrastructure.util.CollectionUtils;
import com.ddd.chulsi.presentation.policyItem.dto.PolicyItemDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyItemFacade {

    private final JwtTokenUtil jwtTokenUtil;
    private final JWTProperties properties;

    private final FileProvider fileProvider;
    private final PhotosService photosService;
    private final PolicyItemService policyItemService;

    @Transactional(rollbackFor = Exception.class)
    public void register(String token, PolicyItemCommand.RegisterCommand registerCommand, MultipartFile file) throws JsonProcessingException {
        jwtTokenUtil.checkAuthIsManager(token, properties);

        PolicyItem newPolicyItem = policyItemService.register(registerCommand.toEntity());

        Optional.ofNullable(file)
            .map(fileItem -> fileProvider.uploadFile("policy", fileItem))
            .ifPresent(fileInfoDTO -> {
                Photos photos = fileInfoDTO.toPhotos(DefinedCode.C000600002, registerCommand.type(), newPolicyItem.getPolicyItemId(), fileInfoDTO);
                photosService.register(photos);
            });
    }

    @Transactional(rollbackFor = Exception.class)
    public void infoUpdate(String token, PolicyItemCommand.InfoUpdateCommand infoUpdateCommand, MultipartFile file) {
        jwtTokenUtil.checkAuthIsManager(token, properties);

        PolicyItem policyItem = policyItemService.findByPolicyItemId(infoUpdateCommand.policyItemId());
        if (policyItem == null) throw new NotFoundException();

        // 파일 삭제
        if (!CollectionUtils.isEmpty(infoUpdateCommand.deleteUploadToken())) {
            Optional.of(infoUpdateCommand.deleteUploadToken()).ifPresent(uploadTokens -> uploadTokens.forEach(photosToken -> {
                Photos photos = photosService.findByToken(photosToken);
                if (photos != null) {
                    fileProvider.deleteFile(photos.getUploadPath() + "/" + photos.getUploadFileName());
                    photosService.delete(photos);
                }
            }));
        }

        Optional.ofNullable(file)
            .map(fileItem -> fileProvider.uploadFile("policy", fileItem))
            .ifPresent(fileInfoDTO -> {
                Photos photos = fileInfoDTO.toPhotos(DefinedCode.C000600002, infoUpdateCommand.type(), policyItem.getPolicyItemId(), fileInfoDTO);
                photosService.register(photos);
            });
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String token, UUID policyItemId) {
        jwtTokenUtil.checkAuthIsManager(token, properties);

        PolicyItem policyItem = policyItemService.findByPolicyItemId(policyItemId);
        if (policyItem == null) throw new NotFoundException();

        List<Photos> photosList = photosService.findByTypeAndSubType(DefinedCode.C000600002, policyItem.getType());
        for (Photos photos : photosList) {
            fileProvider.deleteFile(photos.getUploadPath() + "/" + photos.getUploadFileName());
            photosService.delete(photos);
        }

        policyItemService.delete(policyItem);
    }

    @Transactional(readOnly = true)
    public PolicyItemDTO.PolicyItemInfoResponse info(String token, UUID policyItemId) {
        jwtTokenUtil.checkAuthIsManager(token, properties);

        PolicyItem policyItem = policyItemService.findByPolicyItemId(policyItemId);
        if (policyItem == null) throw new NotFoundException();

        List<Photos> photosList = photosService.findByTypeAndSubType(DefinedCode.C000600002, policyItem.getType());
        List<PhotosInfo.Info> photosInfoList = null;
        if (!CollectionUtils.isEmpty(photosList))
            photosInfoList = photosList.stream().map(photos -> new PhotosInfo.Info(photos.getPhotosId(), photos.getToken(), photos.getUploadFullPath())).collect(Collectors.toList());

        return new PolicyItemDTO.PolicyItemInfoResponse(
            policyItem.getPolicyItemId(),
            policyItem.getType(),
            policyItem.getData(),
            policyItem.getSort(),
            photosInfoList
        );
    }

    @Transactional(readOnly = true)
    public PolicyItemDTO.PolicyItemListResponse list(String token, DefinedCode type) {
        jwtTokenUtil.checkAuthIsManager(token, properties);
        return new PolicyItemDTO.PolicyItemListResponse(policyItemService.getList(type));
    }
}
