package com.prizm.campaign.model;

import jakarta.persistence.*;
import com.prizm.campaign.repository.id.UserCampaignRedemptionId;

import java.util.Date;

@Entity
@Table(name = "user_campaign_redemption")
@IdClass(UserCampaignRedemptionId.class)
public class UserCampaignRedemption {

    @Id
    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "redemption_count", nullable = false)
    private int redemptionCount;

    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRedemptionCount() {
        return redemptionCount;
    }

    public void setRedemptionCount(int redemptionCount) {
        this.redemptionCount = redemptionCount;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
