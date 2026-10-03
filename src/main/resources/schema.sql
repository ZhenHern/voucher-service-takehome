DROP TABLE IF EXISTS redemption;
DROP TABLE IF EXISTS voucher;
DROP TABLE IF EXISTS campaign;

DROP TABLE IF EXISTS redemption;
DROP TABLE IF EXISTS voucher;
DROP TABLE IF EXISTS user_campaign_redemption;
DROP TABLE IF EXISTS campaign;

-- campaign table
CREATE TABLE campaign (
    id                    BIGINT PRIMARY KEY,
    name                  VARCHAR(200) NOT NULL,
    client_code           VARCHAR(50)  NOT NULL,
    total_stock           INT          NOT NULL,
    remaining_stock        INT          NOT NULL,
    active                BOOLEAN      NOT NULL DEFAULT TRUE,
    user_redemption_limit INT          NOT NULL DEFAULT 2,

    CONSTRAINT chk_campaign_stock
        CHECK (remaining_stock >= 0),

    CONSTRAINT chk_campaign_user_redemption_limit
        CHECK (user_redemption_limit > 0)
);

-- voucher table
CREATE TABLE voucher (
    id           BIGINT PRIMARY KEY,
    campaign_id  BIGINT       NOT NULL,
    code         VARCHAR(40)  NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    redeemed_by  VARCHAR(80),
    redeemed_at  TIMESTAMP,

    CONSTRAINT fk_voucher_campaign
        FOREIGN KEY (campaign_id)
        REFERENCES campaign(id),

    CONSTRAINT uq_voucher_code
        UNIQUE (code)
);

-- redemption table
CREATE TABLE redemption (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_id   BIGINT      NOT NULL,
    campaign_id  BIGINT      NOT NULL,
    user_id      VARCHAR(80) NOT NULL,
    created_at   TIMESTAMP   NOT NULL,

    CONSTRAINT fk_redemption_voucher
        FOREIGN KEY (voucher_id)
        REFERENCES voucher(id),

    CONSTRAINT fk_redemption_campaign
        FOREIGN KEY (campaign_id)
        REFERENCES campaign(id)
);

-- user_campaign_redemption table
CREATE TABLE user_campaign_redemption (
    campaign_id       BIGINT      NOT NULL,
    user_id            VARCHAR(80) NOT NULL,
    redemption_count  INT         NOT NULL DEFAULT 0,
    updated_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (campaign_id, user_id),

    CONSTRAINT fk_ucr_campaign
        FOREIGN KEY (campaign_id)
        REFERENCES campaign(id),

    CONSTRAINT chk_ucr_count
        CHECK (redemption_count >= 0)
);

CREATE INDEX idx_voucher_campaign
    ON voucher(campaign_id);

CREATE INDEX idx_redemption_voucher
    ON redemption(voucher_id);

CREATE INDEX idx_redemption_campaign
    ON redemption(campaign_id);