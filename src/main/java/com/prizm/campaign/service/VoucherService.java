package com.prizm.campaign.service;

import com.prizm.campaign.dto.RedeemResponse;
import com.prizm.campaign.model.Campaign;
import com.prizm.campaign.model.Redemption;
import com.prizm.campaign.model.UserCampaignRedemption;
import com.prizm.campaign.model.Voucher;
import com.prizm.campaign.repository.CampaignRepository;
import com.prizm.campaign.repository.RedemptionRepository;
import com.prizm.campaign.repository.UserCampaignRedemptionRepository;
import com.prizm.campaign.repository.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class VoucherService {

    private static final Logger log = LoggerFactory.getLogger(VoucherService.class);

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private RedemptionRepository redemptionRepository;

    @Autowired
    private UserCampaignRedemptionRepository userCampaignRedemptionRepository;

    @Autowired
    private AuditClient auditClient;

    @Transactional
    public RedeemResponse redeem(String code, String userId) {

        Voucher voucher = voucherRepository.findByCode(code);
        if (voucher == null) {
            return RedeemResponse.fail("Voucher not found");
        }

        if ("REDEEMED".equals(voucher.getStatus())) {
            return RedeemResponse.fail("Voucher already redeemed");
        }

        if ("VOID".equals(voucher.getStatus())) {
            return RedeemResponse.fail("Voucher is void");
        }

        Campaign campaign = voucher.getCampaign();

        if (campaign == null) {
            return RedeemResponse.fail("Campaign not found");
        }

        if (!campaign.isActive()) {
            return RedeemResponse.fail("Campaign is not active");
        }

        if (campaign.getRemainingStock() <= 0) {
            return RedeemResponse.fail("Campaign out of stock");
        }

        // =========================================================
        // USER CAMPAIGN REDEMPTION LIMIT
        // =========================================================

        Optional<UserCampaignRedemption> existing =
                userCampaignRedemptionRepository.findForUpdate(
                        campaign.getId(),
                        userId
                );

        if (existing.isPresent()) {

            UserCampaignRedemption counter = existing.get();

            if (counter.getRedemptionCount()
                    >= campaign.getUserRedemptionLimit()) {

                return RedeemResponse.fail(
                        "User has reached the redemption limit for this campaign"
                );
            }

            counter.setRedemptionCount(
                    counter.getRedemptionCount() + 1
            );

            userCampaignRedemptionRepository.save(counter);

        } else {

            // No counter row exists yet.
            // Lock the campaign row so only one pod can initialize
            // the counter for this campaign at a time.
            campaignRepository.findWithLockById(campaign.getId());

            // Re-check after acquiring the campaign lock.
            // Another request may have created the row while we were waiting.
            existing =
                    userCampaignRedemptionRepository.findForUpdate(
                            campaign.getId(),
                            userId
                    );

            if (existing.isPresent()) {

                UserCampaignRedemption counter = existing.get();

                if (counter.getRedemptionCount()
                        >= campaign.getUserRedemptionLimit()) {

                    return RedeemResponse.fail(
                            "User has reached the redemption limit for this campaign"
                    );
                }

                counter.setRedemptionCount(
                        counter.getRedemptionCount() + 1
                );

                userCampaignRedemptionRepository.save(counter);

            } else {

                UserCampaignRedemption counter =
                        new UserCampaignRedemption();

                counter.setCampaignId(campaign.getId());
                counter.setUserId(userId);
                counter.setRedemptionCount(1);
                counter.setUpdatedAt(new Date());

                userCampaignRedemptionRepository.save(counter);
            }
        }

        // =========================================================
        // REDEEM VOUCHER
        // =========================================================

        voucher.setStatus("REDEEMED");
        voucher.setRedeemedBy(userId);
        voucher.setRedeemedAt(new Date());

        voucherRepository.save(voucher);

        // =========================================================
        // DECREASE CAMPAIGN STOCK
        // =========================================================

        campaign.setRemainingStock(
                campaign.getRemainingStock() - 1
        );

        campaignRepository.save(campaign);

        // =========================================================
        // CREATE REDEMPTION HISTORY
        // =========================================================

        Redemption redemption = new Redemption(
                voucher,
                campaign,
                userId,
                new Date()
        );

        redemptionRepository.save(redemption);

        // =========================================================
        // AUDIT
        // =========================================================

        try {
            auditClient.recordRedemption(
                    campaign.getClientCode(),
                    voucher.getCode(),
                    userId
            );
        } catch (Exception e) {
            log.error("audit call failed", e);
        }

        return RedeemResponse.ok(
                voucher.getCode(),
                campaign.getRemainingStock()
        );
    }

    public RedeemResponse voidVoucher(String code) {
        Voucher voucher = voucherRepository.findByCode(code);
        if (voucher == null) {
            return RedeemResponse.fail("Voucher not found");
        }
        voucher.setStatus("VOID");
        voucherRepository.save(voucher);
        return RedeemResponse.ok(voucher.getCode(), null);
    }
}
