package com.prizm.campaign;

import com.prizm.campaign.dto.RedeemResponse;
import com.prizm.campaign.service.VoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

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
}
