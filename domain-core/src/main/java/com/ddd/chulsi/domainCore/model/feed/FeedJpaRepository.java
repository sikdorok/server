package com.ddd.chulsi.domainCore.model.feed;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedJpaRepository extends JpaRepository<Feed, UUID> {

    Optional<Feed> findByFeedId(UUID feedId);

    @Query(value = "select (WEEK(B.checkDate) - WEEK(:startDate) + 1) as 'week', B.checkDate as 'time', IFNULL(A.icon, 'C000400001') as `icon` from (select checkDate from (select :endDate - INTERVAL (a.a + (10 * b.a) + (100 * c.a) + (1000 * d.a) ) DAY as checkDate from (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as a cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as b cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as c cross join (select 0 as a union all select 1 union all select 2 union all select 3 union all select 4 union all select 5 union all select 6 union all select 7 union all select 8 union all select 9) as d ) a where a.checkDate between :startDate and :endDate order by a.checkDate desc ) B left join (select DATE_FORMAT(f.time, '%Y%-%m-%d') as `time`, case when exists(select f2.feedId from feed f2 where f2.isMain = 1 and DATE_FORMAT(f2.time, '%Y%-%m-%d') = DATE_FORMAT(f.time, '%Y%-%m-%d') order by f2.createdAt desc limit 1) then (select f2.icon from feed f2 where f2.isMain = 1 and DATE_FORMAT(f2.time, '%Y%-%m-%d') = DATE_FORMAT(f.time, '%Y%-%m-%d') order by f2.createdAt desc limit 1) else f.icon end as `icon` from users u left outer join feed f on u.usersId = f.usersId where BIN_TO_UUID(u.usersId) = :#{#usersId.toString()} and f.`time` BETWEEN :startDate and :endDate group by DAY(f.time) ) A on A.time = B.checkDate order by B.checkDate asc", nativeQuery = true)
    List<FeedInfo.Weekly> weeklyList(UUID usersId, LocalDate startDate, LocalDate endDate);

    @Modifying
    @Query(value = "DELETE FROM feed WHERE usersId = :usersId", nativeQuery = true)
    void revokeUsers(UUID usersId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Feed> findByTimeAndTagAndIconAndMemo(LocalDateTime time, DefinedCode tag, DefinedCode icon, String memo);

}
