package com.prizm.campaign.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "redemption")
public class Redemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "created_at")
    private Date createdAt;

    public Redemption() {}

    public Redemption(Voucher voucher, Campaign campaign, String userId, Date createdAt) {
        this.voucher = voucher;
        this.campaign = campaign;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Voucher getVoucher() { return voucher; }
    public void setVoucher(Voucher voucher) { this.voucher = voucher; }
    public Campaign getCampaign() { return campaign; }
    public void setCampaign(Campaign campaign) { this.campaign = campaign; }
    public String getUserId() { return userId; }
    public Date getCreatedAt() { return createdAt; }
}
