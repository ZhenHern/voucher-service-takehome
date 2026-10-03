package com.prizm.campaign.repository;

import com.prizm.campaign.model.UserCampaignRedemption;
import com.prizm.campaign.repository.id.UserCampaignRedemptionId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserCampaignRedemptionRepository
        extends JpaRepository<UserCampaignRedemption, UserCampaignRedemptionId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT u
        FROM UserCampaignRedemption u
        WHERE u.campaignId = :campaignId
          AND u.userId = :userId
        """)
    Optional<UserCampaignRedemption> findForUpdate(
            @Param("campaignId") Long campaignId,
            @Param("userId") String userId
    );
}
