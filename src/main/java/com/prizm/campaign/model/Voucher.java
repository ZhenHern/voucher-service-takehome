package com.prizm.campaign.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "voucher")
public class Voucher {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    private String code;

    // ACTIVE / REDEEMED / VOID
    private String status;

    @Column(name = "redeemed_by")
    private String redeemedBy;

    @Column(name = "redeemed_at")
    private Date redeemedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Campaign getCampaign() {  return campaign; }
    public void setCampaign(Campaign campaign) { this.campaign = campaign; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRedeemedBy() { return redeemedBy; }
    public void setRedeemedBy(String redeemedBy) { this.redeemedBy = redeemedBy; }
    public Date getRedeemedAt() { return redeemedAt; }
    public void setRedeemedAt(Date redeemedAt) { this.redeemedAt = redeemedAt; }
}
