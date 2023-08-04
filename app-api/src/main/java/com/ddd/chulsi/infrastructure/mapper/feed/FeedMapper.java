package com.ddd.chulsi.infrastructure.mapper.feed;

import com.ddd.chulsi.domainCore.model.feed.FeedInfo;
import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.presentation.shared.response.dto.PagingDTO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface FeedMapper {

    PagingDTO toPagingDTO(Page<FeedInfo.HomeFeedItemDTO> feedList);

    @Mappings({
        @Mapping(source = "time", target = "time", qualifiedByName = "convertTime"),
        @Mapping(source = "tag", target = "tag", qualifiedByName = "convertDefinedCode"),
        @Mapping(source = "photosInfoList", target = "photosInfoList", qualifiedByName = "nullCheckList")
    })
    List<FeedInfo.HomeFeedItem> toConvertDTO(List<FeedInfo.HomeFeedItemDTO> content);

    @Mappings({
        @Mapping(source = "time", target = "time", qualifiedByName = "convertTime"),
        @Mapping(source = "tag", target = "tag", qualifiedByName = "convertDefinedCode"),
        @Mapping(source = "photosInfoList", target = "photosInfoList", qualifiedByName = "nullCheckList")
    })
    FeedInfo.HomeFeedItem toListItemConvertDTO(FeedInfo.HomeFeedItemDTO dto);

    @Named("nullCheckList")
    default List<PhotosInfo.Info> nullCheckList(List<PhotosInfo.Info> list) {
        list.removeIf(info -> info.token() == null);
        return list;
    }

    @Named("convertDefinedCode")
    default DefinedCode convertDefinedCode(String value) {
        if (StringUtils.isBlank(value)) return null;

        return DefinedCode.valueOf(value);
    }

    @Named("convertTime")
    default String convertTime(LocalDateTime time) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("a HH:mm", Locale.KOREAN);

        String convertTime = time.format(timeFormat);
        if (convertTime.contains("AM")) convertTime = convertTime.replace("AM", "오전");
        else convertTime = convertTime.replace("PM", "오후");

        return convertTime;
    }

}
