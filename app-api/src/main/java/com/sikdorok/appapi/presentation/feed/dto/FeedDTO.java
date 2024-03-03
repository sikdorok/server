package com.sikdorok.appapi.presentation.feed.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sikdorok.domaincore.model.feed.FeedCommand;
import com.sikdorok.domaincore.model.feed.FeedInfo;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.infrastructure.exception.BadRequestException;
import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import com.sikdorok.appapi.presentation.shared.request.PageAndSizeRequest;
import com.sikdorok.appapi.presentation.shared.request.Validator;
import com.sikdorok.appapi.presentation.shared.response.dto.PagingDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FeedDTO {


    public record FeedRegisterRequest (
        @NotNull
        DefinedCode tag,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime time,
        String memo,

        @NotNull
        DefinedCode icon,
        boolean isMain
    ) implements Validator {
        public FeedCommand.RegisterCommand toCommand() {
            return FeedCommand.RegisterCommand.toCommand(tag, time, memo, icon, isMain);
        }

        @Override
        public void verify() {
            if (!tag.getSectionCode().equals(DefinedCode.C0003.getSectionCode()))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "tag");
            if (!icon.getSectionCode().equals(DefinedCode.C0004.getSectionCode()))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "icon");
            if (time.toLocalDate().isAfter(LocalDateTime.now().toLocalDate()))
                throw new BadRequestException("당일 날짜만 등록 가능합니다.", "date");
        }
    }

    public record FeedInfoResponse (
        String nickname,
        FeedInfo.FeedInfoDTO feedInfo
    ) {

    }

    public record FeedInfoUpdateRequest(
        @NotNull
        UUID feedId,

        @NotNull
        DefinedCode tag,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime time,
        String memo,

        @NotNull
        DefinedCode icon,
        boolean isMain,
        Set<UUID> deletePhotoTokens
    ) implements Validator {
        public FeedCommand.InfoUpdateCommand toCommand() {
            return FeedCommand.InfoUpdateCommand.toCommand(feedId, tag, time, memo, icon, isMain, deletePhotoTokens);
        }

        @Override
        public void verify() {
            if (!tag.getSectionCode().equals(DefinedCode.C0003.getSectionCode()))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "tag");
            if (!icon.getSectionCode().equals(DefinedCode.C0004.getSectionCode()))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "icon");
            if (time.toLocalDate().isAfter(LocalDateTime.now().toLocalDate()))
                throw new BadRequestException("당일 날짜만 등록 가능합니다.", "date");
        }
    }

    public record MonthlyResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate date,
        List<FeedInfo.WeeklyCover> weeklyCovers
    ) {
    }

    public record ListResponse(
        PagingDTO paging,
        List<FeedInfo.HomeFeedItem> dailyFeeds,
        DefinedCode initTag,
        List<DefinedCode> tags
    ) {
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListRequest extends PageAndSizeRequest implements Validator {
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date;

        DefinedCode tag;

        public FeedCommand.ListCommand toCommand() {
            return FeedCommand.ListCommand.toCommand(getPage(), getSize(), date, tag);
        }

        @Override
        public void verify() {
            if (tag != null && !tag.getSectionCode().equals(DefinedCode.C0003.getSectionCode()))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG, "tag");
        }
    }

    public record ListViewResponse(
        boolean hasNext,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate cursorDate,
        List<FeedInfo.HomeListViewDTO> dailyFeeds
    ) {
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListViewRequest {
        @Min(1)
        int size;

        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate cursorDate;

        public FeedCommand.ListViewCommand toCommand() {
            return FeedCommand.ListViewCommand.toCommand(size, date, cursorDate);
        }
    }
}
