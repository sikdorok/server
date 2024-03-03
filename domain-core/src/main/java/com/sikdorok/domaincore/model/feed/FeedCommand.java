package com.sikdorok.domaincore.model.feed;

import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class FeedCommand {

    public record RegisterCommand(
        DefinedCode tag,
        LocalDateTime time,
        String memo,
        DefinedCode icon,
        boolean isMain
    ) {
        public static RegisterCommand toCommand(DefinedCode tag, LocalDateTime time, String memo, DefinedCode icon, boolean isMain) {
            return new RegisterCommand(tag, time, memo, icon, isMain);
        }

        public Feed toEntity(UUID usersId) {
            return Feed.builder()
                .usersId(usersId)
                .tag(tag)
                .time(time)
                .memo(memo)
                .icon(icon)
                .isMain(isMain)
                .build();
        }
    }

    public record InfoUpdateCommand(
        UUID feedId,
        DefinedCode tag,
        LocalDateTime time,
        String memo,
        DefinedCode icon,
        boolean isMain,
        Set<UUID> deletePhotoTokens
    ) {
        public static InfoUpdateCommand toCommand(UUID feedId, DefinedCode tag, LocalDateTime time, String memo, DefinedCode icon, boolean isMain, Set<UUID> deletePhotoTokens) {
            return new InfoUpdateCommand(feedId, tag, time, memo, icon, isMain, deletePhotoTokens);
        }
    }

    public record ListCommand(
        int page,
        int size,
        LocalDate date,
        DefinedCode tag
    ) {
        public static ListCommand toCommand(int page, int size, LocalDate date, DefinedCode tag) {
            return new ListCommand(page, size, date, tag);
        }
    }

    public record ListViewCommand(
        int size,
        LocalDate date,
        LocalDate cursorDate
    ) {
        public static ListViewCommand toCommand(int size, LocalDate date, LocalDate cursorDate) {
            return new ListViewCommand(size, date, cursorDate);
        }
    }

    public record RevokeUsers(
        UUID usersId
    ) {
        public static RevokeUsers toCommand(UUID usersId) {
            return new RevokeUsers(usersId);
        }
    }
}
