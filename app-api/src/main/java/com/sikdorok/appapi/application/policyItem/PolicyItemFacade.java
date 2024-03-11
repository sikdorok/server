package com.sikdorok.appapi.application.policyItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sikdorok.domaincore.model.photos.Photos;
import com.sikdorok.domaincore.model.photos.PhotosInfo;
import com.sikdorok.domaincore.model.photos.PhotosService;
import com.sikdorok.domaincore.model.policyItem.PolicyItem;
import com.sikdorok.domaincore.model.policyItem.PolicyItemCommand;
import com.sikdorok.domaincore.model.policyItem.PolicyItemService;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.infrastructure.aws.FileProvider;
import com.sikdorok.appapi.infrastructure.exception.NotFoundException;
import com.sikdorok.appapi.infrastructure.jwt.JWTProperties;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import com.sikdorok.system.CollectionUtils;
import com.sikdorok.appapi.presentation.policyItem.dto.PolicyItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
                Photos photos = fileInfoDTO.toPhotos(DefinedCode.C000600002, registerCommand.type(), newPolicyItem.getPolicyItemId());
                photosService.register(photos);
            });
    }

    @Transactional(rollbackFor = Exception.class)
    public void infoUpdate(String token, PolicyItemCommand.InfoUpdateCommand infoUpdateCommand, MultipartFile file) {
        jwtTokenUtil.checkAuthIsManager(token, properties);

        PolicyItem policyItem = policyItemService.findByPolicyItemId(infoUpdateCommand.policyItemId());
        if (policyItem == null) throw new NotFoundException();

        // 파일 삭제
        if (!CollectionUtils.isEmpty(infoUpdateCommand.deletePhotoTokens())) {
            Optional.of(infoUpdateCommand.deletePhotoTokens()).ifPresent(uploadTokens -> uploadTokens.forEach(photosToken -> {
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
                Photos photos = fileInfoDTO.toPhotos(DefinedCode.C000600002, infoUpdateCommand.type(), policyItem.getPolicyItemId());
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
    public PolicyItemDTO.PolicyItemInfoResponse info(UUID policyItemId) {
        PolicyItem policyItem = policyItemService.findByPolicyItemId(policyItemId);
        if (policyItem == null) throw new NotFoundException();

        List<Photos> photosList = photosService.findByTypeAndSubType(DefinedCode.C000600002, policyItem.getType());
        List<PhotosInfo.Info> photosInfoList = null;
        if (!CollectionUtils.isEmpty(photosList))
            photosInfoList = photosList.stream().map(photos -> new PhotosInfo.Info(photos.getToken(), photos.getUploadFullPath())).collect(Collectors.toList());

        return new PolicyItemDTO.PolicyItemInfoResponse(
            policyItem.getPolicyItemId(),
            policyItem.getType(),
            policyItem.getData(),
            policyItem.getSort(),
            photosInfoList
        );
    }

    @Transactional(readOnly = true)
    public PolicyItemDTO.PolicyItemListResponse list(DefinedCode type) {
        return new PolicyItemDTO.PolicyItemListResponse(policyItemService.getList(type));
    }
}
