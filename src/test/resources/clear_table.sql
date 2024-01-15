SET REFERENTIAL_INTEGRITY FALSE;

truncate table accommodation_option;
truncate table accommodation_ownership;
truncate table category;
truncate table accommodation;
truncate table room_price;
truncate table room_option;
truncate table room_stock;
TRUNCATE TABLE room;
truncate table coupon_issuance;
truncate table coupon_redeem;
truncate table coupon;
truncate table point_usage;
truncate table point_charges;
truncate table point_refunds;
truncate table point;
truncate table payment;
truncate table reservation_room;
truncate table reservation;
truncate table member;

ALTER TABLE accommodation_option ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE accommodation_ownership ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE category ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE accommodation ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE room_price ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE room_option ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE room_stock ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE room ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE coupon_issuance ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE coupon_redeem ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE coupon ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE point_usage ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE coupon ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE point_usage ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE point_charges ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE point_refunds ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE point ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE payment ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE reservation_room ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE reservation ALTER COLUMN `id` RESTART WITH 1;
ALTER TABLE member ALTER COLUMN `id` RESTART WITH 1;

SET REFERENTIAL_INTEGRITY TRUE;



