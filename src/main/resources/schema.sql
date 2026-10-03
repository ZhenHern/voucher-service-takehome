DROP TABLE IF EXISTS redemption;
DROP TABLE IF EXISTS voucher;
DROP TABLE IF EXISTS campaign;

CREATE TABLE campaign (
    id              BIGINT PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    client_code     VARCHAR(50)  NOT NULL,
    total_stock     INT          NOT NULL,
    remaining_stock INT          NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE
);

ALTER TABLE campaign
ADD COLUMN user_redemption_limit INT NOT NULL DEFAULT 2;

CREATE TABLE voucher (
    id          BIGINT PRIMARY KEY,
    campaign_id BIGINT       NOT NULL,
    code        VARCHAR(40)  NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    redeemed_by VARCHAR(80),
    redeemed_at TIMESTAMP
);

CREATE TABLE redemption (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_id  BIGINT      NOT NULL,
    campaign_id BIGINT      NOT NULL,
    user_id     VARCHAR(80) NOT NULL,
    created_at  TIMESTAMP   NOT NULL
);

CREATE TABLE user_campaign_redemption (
    campaign_id       BIGINT      NOT NULL,
    user_id           VARCHAR(80) NOT NULL,
    redemption_count  INT         NOT NULL DEFAULT 0,
    updated_at        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
                                  ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (campaign_id, user_id),

    CONSTRAINT fk_ucr_campaign
            FOREIGN KEY (campaign_id)
        REFERENCES campaign(id),

    CONSTRAINT chk_ucr_count
        CHECK (redemption_count >= 0)
);