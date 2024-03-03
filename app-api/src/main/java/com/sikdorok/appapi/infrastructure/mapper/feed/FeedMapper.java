package com.sikdorok.appapi.infrastructure.mapper.feed;

import com.sikdorok.domaincore.model.feed.FeedInfo;
import com.sikdorok.domaincore.model.photos.PhotosInfo;
import com.sikdorok.appapi.presentation.shared.response.dto.PagingDTO;
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
        @Mapping(source = "photosInfoList", target = "photosInfoList", qualifiedByName = "nullCheckList")
    })
    List<FeedInfo.HomeFeedItem> toConvertDTO(List<FeedInfo.HomeFeedItemDTO> content);

    @Mappings({
        @Mapping(source = "time", target = "time", qualifiedByName = "convertTime"),
        @Mapping(source = "photosInfoList", target = "photosInfoList", qualifiedByName = "nullCheckList")
    })
    FeedInfo.HomeFeedItem toListItemConvertDTO(FeedInfo.HomeFeedItemDTO dto);

    @Named("nullCheckList")
    default List<PhotosInfo.Info> nullCheckList(List<PhotosInfo.Info> list) {
        list.removeIf(info -> info.token() == null);
        return list;
    }

    @Named("convertTime")
    default String convertTime(LocalDateTime time) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN);

        String convertTime = time.format(timeFormat);
        if (convertTime.startsWith("AM")) convertTime = convertTime.replace("AM", "오전");
        else convertTime = convertTime.replace("PM", "오후");

        return convertTime;
    }

    @Named("convertDate")
    default String convertDate(LocalDateTime time) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd일 E요일", Locale.KOREAN);

        String format = time.format(timeFormat);
        return format.startsWith("0") ? format.replace("0", "") : format;
    }

    @Mappings({
        @Mapping(source = "time", target = "originTime"),
        @Mapping(source = "time", target = "time", qualifiedByName = "convertTime"),
        @Mapping(source = "time", target = "date", qualifiedByName = "convertDate"),
        @Mapping(source = "photosInfoList", target = "photosInfoList", qualifiedByName = "nullCheckList")
    })
    List<FeedInfo.HomeListViewFeedItem> toConvertDTOWithCursorBased(List<FeedInfo.HomeListViewFeedItemDTO> content);

    @Mappings({
        @Mapping(source = "time", target = "originTime"),
        @Mapping(source = "time", target = "time", qualifiedByName = "convertTime"),
        @Mapping(source = "time", target = "date", qualifiedByName = "convertDate"),
        @Mapping(source = "photosInfoList", target = "photosInfoList", qualifiedByName = "nullCheckList")
    })
    FeedInfo.HomeListViewFeedItem toListItemConvertDTOWithCursorBased(FeedInfo.HomeListViewFeedItemDTO dto);

}
