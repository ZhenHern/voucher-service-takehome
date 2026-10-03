package com.prizm.campaign.repository.id;

import java.io.Serializable;
import java.util.Objects;

public class UserCampaignRedemptionId implements Serializable {

    private Long campaignId;
    private String userId;

    public UserCampaignRedemptionId() {
    }

    public UserCampaignRedemptionId(Long campaignId, String userId) {
        this.campaignId = campaignId;
        this.userId = userId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCampaignRedemptionId)) return false;

        UserCampaignRedemptionId that = (UserCampaignRedemptionId) o;

        return Objects.equals(campaignId, that.campaignId)
                && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(campaignId, userId);
    }
}