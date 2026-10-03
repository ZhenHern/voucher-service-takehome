package com.prizm.campaign;

import com.prizm.campaign.dto.RedeemResponse;
import com.prizm.campaign.model.UserCampaignRedemption;
import com.prizm.campaign.repository.UserCampaignRedemptionRepository;
import com.prizm.campaign.repository.id.UserCampaignRedemptionId;
import com.prizm.campaign.service.VoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private UserCampaignRedemptionRepository userCampaignRedemptionRepository;

    @Test
    void redeemActiveVoucherSucceeds() {

        RedeemResponse res =
                voucherService.redeem(
                        "RAYA-0001",
                        "user-1"
                );

        assertEquals("OK", res.getResult());
    }

    @Test
    void redeemAlreadyRedeemedVoucherFails() {

        RedeemResponse res =
                voucherService.redeem(
                        "RAYA-0004",
                        "user-2"
                );

        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemUnknownCodeFails() {

        RedeemResponse res =
                voucherService.redeem(
                        "NOPE-9999",
                        "user-3"
                );

        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemVoidVoucherFails() {

        RedeemResponse res =
                voucherService.redeem(
                        "RAYA-0005",
                        "user-4"
                );

        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemVoucherFromInactiveCampaignFails() {

        RedeemResponse res =
                voucherService.redeem(
                        "EXPD-0001",
                        "user-5"
                );

        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemVoucherWhenCampaignOutOfStockFails() {

        RedeemResponse res =
                voucherService.redeem(
                        "OOS-0001",
                        "user-6"
                );

        assertEquals("FAILED", res.getResult());
    }

    @Test
    void redeemCustomerBeyondLimitFails() {

        RedeemResponse first =
                voucherService.redeem(
                        "RAYA-0006",
                        "cap-user"
                );

        RedeemResponse second =
                voucherService.redeem(
                        "RAYA-0007",
                        "cap-user"
                );

        RedeemResponse third =
                voucherService.redeem(
                        "RAYA-0008",
                        "cap-user"
                );

        assertEquals("OK", first.getResult());
        assertEquals("OK", second.getResult());
        assertEquals("FAILED", third.getResult());
    }

    @Test
    void concurrentFirstRedemptionsForSameUserRespectLimit() throws Exception {

        String userId = "concurrent-first-user";

        ExecutorService executor = Executors.newFixedThreadPool(3);

        CountDownLatch startLatch = new CountDownLatch(1);

        List<Future<RedeemResponse>> futures = new ArrayList<>();

        futures.add(executor.submit(() -> {
            startLatch.await();
            return voucherService.redeem(
                    "RAYA-CONCURRENT-01",
                    userId
            );
        }));

        futures.add(executor.submit(() -> {
            startLatch.await();
            return voucherService.redeem(
                    "RAYA-CONCURRENT-02",
                    userId
            );
        }));

        futures.add(executor.submit(() -> {
            startLatch.await();
            return voucherService.redeem(
                    "RAYA-CONCURRENT-03",
                    userId
            );
        }));

        // Release all three threads at approximately the same time
        startLatch.countDown();

        int successCount = 0;
        int failedCount = 0;

        for (Future<RedeemResponse> future : futures) {

            RedeemResponse response = future.get();

            if ("OK".equals(response.getResult())) {
                successCount++;
            } else if ("FAILED".equals(response.getResult())) {
                failedCount++;
            }
        }

        executor.shutdown();

        assertEquals(2, successCount);
        assertEquals(1, failedCount);

        UserCampaignRedemption counter =
                userCampaignRedemptionRepository
                        .findById(
                                new UserCampaignRedemptionId(
                                        1L,
                                        userId
                                )
                        )
                        .orElseThrow();

        assertEquals(2, counter.getRedemptionCount());
    }
}
