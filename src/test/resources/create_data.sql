insert into member (id, name, password, email, phone, image_url, authority, created_at)
values (1, '김업주', 'vV1\92F$1j46s,', 'lmaybery0@sphinn.com', '568-137-2015',
        'http://google.ca/lobortis/vel.html', 'ROLE_USER', '2022-07-25'),
       (2, '이업주', 'hA7!NjlDFbAv9', 'qjachimak1@deliciousdays.com', '771-430-9565',
        'http://networksolutions.com/phasellus/sit/amet/erat/nulla.jpg', 'ROLE_USER', '2023-07-11'),
       (3, '공업주', 'fA7<h)%U,d10', 'dtethcote2@hhs.gov', '176-233-1059',
        'http://yahoo.co.jp/sagittis/sapien/cum/sociis/natoque.jpg', 'ROLE_USER', '2022-01-01'),
       (4, '박업주', 'gP1*mW"Cv*RFh', 'phutchence3@feedburner.com', '475-739-8894',
        'http://printfriendly.com/diam/erat/fermentum/justo/nec/condimentum/neque.json',
        'ROLE_ADMIN', '2023-08-05'),
       (5, '황업주', 'uS8#U8JWd\ZmQexJ', 'gjanikowski4@dell.com', '129-330-6844',
        'https://weibo.com/ut.json', 'ROLE_USER', '2022-01-14'),
       (6, '최업주', 'oY7+,8(.|R', 'jakers5@paginegialle.it', '183-488-4835',
        'http://virginia.edu/nulla/sed/vel/enim/sit.html', 'ROLE_USER', '2023-02-21'),
       (7, '남궁업주', 'yR8=YYkpk>gT''_', 'slinggood6@nbcnews.com', '424-665-6515',
        'https://amazon.co.jp/quisque/arcu.xml', 'ROLE_USER', '2023-11-18'),
       (8, '소업주', 'bK8<4lXrt', 'tmelan7@scribd.com', '671-629-7841',
        'https://artisteer.com/massa/id/nisl/venenatis/lacinia/aenean.aspx', 'ROLE_USER',
        '2022-06-26'),
       (9, '왕업주', 'kW2_|yJ#%##''L&', 'cbehling8@usda.gov', '598-754-8872',
        'http://sbwire.com/risus/auctor.html', 'ROLE_USER', '2022-06-23'),
       (10, '현업주', 'tJ8,H&GA?', 'dgreyes9@blogs.com', '393-320-6379',
        'http://lulu.com/praesent/blandit/nam/nulla/integer.json', 'ROLE_USER', '2023-01-30');

INSERT INTO `address` (`id`, `address`, `detail_address`, `zip_code`)
VALUES (1, '경기도 양평군 용문면 용문산로 337', '', '12510'),
       (2, '경상남도 김해시 대동면 대동로 536-15', '', '50809'),
       (3, '경상남도 거제시 일운면 거제대로 1988-10', '', '53329'),
       (4, '강원특별자치도 평창군 용평면 태기로 1034', '(백옥포리)', '25316'),
       (5, '전라남도 구례군 용방면 용산로 107-66', '', '57607'),
       (6, '경기도 양평군 강상면 독배길 34', '', '12571'),
       (7, '서울특별시 강남구 테헤란로 521', '', '06164'),
       (8, '제주특별자치도 제주시 노형동', '', '63080');

insert into `category` (`id`, `name`)
values (1, 'TOURIST_HOTEL'),
       (2, 'PENSION'),
       (3, 'MOTEL'),
       (4, 'CONDOMINIUM'),
       (5, 'HANOK'),
       (6, 'GUEST_HOUSE'),
       (7, 'BED_AND_BREAKFAST');

insert into `accommodation_option` (`id`, barbecue, cooking, fitness, karaoke, parking, pickup,
                                    sauna, seminar, sports)
values (1, 0, 1, 0, 0, 1, 0, 1, 0, 1),
       (2, 0, 1, 0, 0, 1, 0, 1, 0, 1),
       (3, 0, 1, 0, 0, 1, 0, 1, 0, 1),
       (4, 0, 1, 0, 0, 1, 0, 1, 0, 1),
       (5, 0, 1, 0, 0, 1, 0, 1, 0, 1),
       (6, 0, 0, 0, 0, 1, 0, 1, 0, 1),
       (7, 0, 1, 0, 0, 1, 0, 1, 0, 1),
       (8, 0, 1, 0, 0, 1, 0, 1, 0, 1),
       (9, 0, 1, 0, 0, 1, 0, 1, 0, 1),
       (10, 0, 0, 1, 0, 1, 1, 1, 1, 1);


INSERT INTO `accommodation` (`id`, `name`, `address_id`, `category_id`, `option_id`, `created_at`,
                             `description`, `thumbnail`)
VALUES (1, '양평 관광호텔', 1, 1, 1, now(), '묻지마 관광호텔',
        'http://tong.visitkorea.or.kr/cms/resource/36/2764536_image2_1.jpg'),
       (2, '김해 펜션', 2, 2, 2, now(), '묻지마 펜션',
        'http://tong.visitkorea.or.kr/cms/resource/30/1893430_image2_1.jpg'),
       (3, '거제 모텔 ', 3, 3, 3, now(), '묻지마 모텔',
        'http://tong.visitkorea.or.kr/cms/resource/72/2604472_image2_1.jpg'),
       (4, '평창 콘도', 4, 4, 4, now(), '묻지마 콘도',
        'http://tong.visitkorea.or.kr/cms/resource/26/1869226_image2_1.jpg'),
       (5, '제주 민박', 8, 7, 5, now(), '묻지마 민박',
        'http://tong.visitkorea.or.kr/cms/resource/83/2725983_image2_1.jpg'),
       (6, '구례 게스트하우스', 5, 6, 6, now(), '묻지마 게스트하우스',
        'http://tong.visitkorea.or.kr/cms/resource/45/2764645_image2_1.jpg'),
       (7, '남산 한옥', 7, 5, 7, now(), '묻지마 한옥',
        'http://tong.visitkorea.or.kr/cms/resource/01/1836401_image2_1.JPG');

insert into accommodation_ownership(`id`, `accommodation_id`, `member_id`)
values (1, 1, 1),
       (2, 2, 2),
       (3, 3, 3),
       (4, 4, 4),
       (5, 5, 5),
       (6, 6, 6),
       (7, 7, 7);

INSERT INTO `room_price` (`id`, `off_week_days_min_fee`, `off_weekend_min_fee`,
                          `peak_week_days_min_fee`, `peak_weekend_min_fee`)
VALUES (1, 100000, 100000, 100000, 100000),
       (2, 150000, 150000, 150000, 150000),
       (3, 200000, 200000, 200000, 200000),
       (4, 250000, 250000, 250000, 250000),
       (5, 300000, 300000, 300000, 300000),
       (6, 350000, 350000, 350000, 350000),
       (7, 400000, 400000, 400000, 400000),
       (8, 450000, 450000, 450000, 450000),
       (9, 500000, 500000, 500000, 500000),
       (10, 550000, 550000, 550000, 550000),
       (11, 600000, 600000, 600000, 600000);

INSERT INTO `room_option` (`id`, `air_condition`, `internet`, `tv`)
VALUES (1, 1, 1, 1),
       (2, 1, 1, 1),
       (3, 1, 1, 1),
       (4, 1, 1, 1),
       (5, 1, 1, 1),
       (6, 1, 1, 1),
       (7, 1, 1, 1),
       (8, 1, 1, 1),
       (9, 1, 1, 1),
       (10, 1, 1, 1),
       (11, 1, 1, 1);

insert into room (`id`, `accommodation_id`, `price_id`, `option_id`, `name`, `default_capacity`,
                  `max_capacity`, `check_in_time`, `check_out_time`, `amount`, `status`)
values (1, 1, 1, 1, '스탠다드', 1, 2, '15:00:00', '11:00:00', 30, 'SELLING'),
       (2, 1, 2, 2, '디럭스', 1, 2, '15:00:00', '11:00:00', 20, 'SELLING'),
       (3, 1, 3, 3, '스위트', 2, 3, '15:00:00', '11:00:00', 10, 'SELLING'),
       (4, 2, 4, 4, '커플룸', 2, 3, '14:00:00', '11:00:00', 50, 'SELLING'),
       (5, 2, 5, 5, '가족룸', 4, 5, '14:00:00', '11:00:00', 20, 'SELLING'),
       (6, 3, 6, 6, '디럭스 트윈', 2, 3, '14:00:00', '11:00:00', 20, 'SELLING'),
       (7, 3, 7, 7, '스위트 트윈 오션 뷰', 2, 3, '14:00:00', '11:00:00', 20, 'SELLING'),
       (8, 4, 8, 8, '일반실', 5, 6, '16:00:00', '12:00:00', 5, 'SELLING'),
       (9, 5, 9, 9, '사랑방', 5, 6, '17:00:00', '12:00:00', 2, 'SELLING'),
       (10, 6, 10, 10, '황토방', 4, 5, '15:00:00', '11:00:00', 10, 'SELLING'),
       (11, 7, 11, 11, '온돌방', 2, 3, '15:00:00', '11:00:00', 10, 'SELLING');

insert into `room_stock` (`id`, `room_id`, `count`, `date`)
values (1, 1, 30, curdate()),
       (2, 2, 20, curdate()),
       (3, 3, 10, curdate()),
       (4, 4, 50, curdate()),
       (5, 5, 20, curdate()),
       (6, 6, 20, curdate()),
       (7, 7, 20, curdate()),
       (8, 8, 5, curdate()),
       (9, 9, 2, curdate()),
       (10, 10, 10, curdate()),
       (11, 11, 10, curdate());
