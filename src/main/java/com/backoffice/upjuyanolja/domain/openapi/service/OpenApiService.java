package com.backoffice.upjuyanolja.domain.openapi.service;

import com.backoffice.upjuyanolja.domain.accommodation.entity.Accommodation;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationImage;
import com.backoffice.upjuyanolja.domain.accommodation.entity.AccommodationOption;
import com.backoffice.upjuyanolja.domain.accommodation.entity.Address;
import com.backoffice.upjuyanolja.domain.accommodation.exception.WrongCategoryException;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationImageRepository;
import com.backoffice.upjuyanolja.domain.accommodation.repository.AccommodationRepository;
import com.backoffice.upjuyanolja.domain.openapi.exception.InvalidDataException;
import com.backoffice.upjuyanolja.domain.openapi.exception.OpenApiException;
import com.backoffice.upjuyanolja.domain.room.entity.Room;
import com.backoffice.upjuyanolja.domain.room.entity.RoomImage;
import com.backoffice.upjuyanolja.domain.room.entity.RoomOption;
import com.backoffice.upjuyanolja.domain.room.entity.RoomPrice;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStatus;
import com.backoffice.upjuyanolja.domain.room.entity.RoomStock;
import com.backoffice.upjuyanolja.domain.room.repository.RoomImageRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomRepository;
import com.backoffice.upjuyanolja.domain.room.repository.RoomStockRepository;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {

    @Value("${open-api.service-key}")
    private String SERVICE_KEY;

    private final String BASE_URL = "https://apis.data.go.kr/B551011/KorService1";
    private final String DEFAULT_QUERY_PARAMS = "&MobileOS=ETC&MobileApp=AppTest&_type=json";
    private final int CONTENT_TYPE_ID = 32;
    private final int DEFAULT_PRICE = 100000;

    private final AccommodationRepository accommodationRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final RoomStockRepository roomStockRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpEntity<String> httpEntity;

    @PostConstruct
    public void init() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        httpEntity = new HttpEntity<>(headers);
    }

    @Transactional
    public void getData(int pageSize, int pageNum) throws JSONException {
        try {
            JSONArray stayArr = getItems(getAccommodation(pageSize, pageNum));

            for (int j = 0; j < stayArr.length(); j++) {
                JSONObject stay = stayArr.getJSONObject(j);
                int contentId = stay.getInt("contentid");
                try {
                    JSONObject info = getInfo(contentId);
                    checkInfo(info);
                    JSONArray rooms = getItems(info);
                    checkRoom(rooms);
                    checkRoomImage(rooms);
                    JSONObject image = getImages(contentId);
                    checkAccommodationImage(image);
                    JSONArray images = getItems(image);
                    JSONObject common = getCommon(contentId);
                    checkCommon(common);
                    JSONObject commonItem = getItems(common).getJSONObject(0);
                    JSONObject intro = getIntro(contentId);
                    checkIntro(intro);
                    JSONObject introItem = getItems(intro).getJSONObject(0);
                    checkIntroItem(introItem);
                    checkStay(stay);

                    Accommodation accommodation = saveAccommodation(stay, commonItem, introItem);
                    saveAccommodationImages(accommodation, images);
                    saveRooms(accommodation, introItem, rooms);
                } catch (InvalidDataException | WrongCategoryException e) {
                    log.info("[OpenAPI] {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("[OpenAPI] {}", e.getMessage());
            throw new OpenApiException();
        }
    }


    private String makeBaseSearchUrl() {
        String STAY_SEARCH_URI = "/searchStay1";

        return BASE_URL + STAY_SEARCH_URI +
            "?serviceKey=" + URLEncoder.encode(SERVICE_KEY, StandardCharsets.UTF_8) +
            DEFAULT_QUERY_PARAMS;
    }

    private String makeUrl(String URL) {
        int numOfRows = 30;

        return BASE_URL + URL +
            "?serviceKey=" + URLEncoder.encode(SERVICE_KEY, StandardCharsets.UTF_8) +
            DEFAULT_QUERY_PARAMS +
            "&numOfRows=" + numOfRows;
    }

    private JSONObject getAccommodation(int pageSize, int pageNum) throws JSONException {
        URI uri = UriComponentsBuilder.fromHttpUrl(makeBaseSearchUrl())
            .queryParam("pageNo", pageNum)
            .queryParam("numOfRows", pageSize)
            .build(true).toUri();

        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,
            httpEntity, String.class);

        return getBody(response.getBody());
    }

    private JSONObject getCommon(long contentId) throws JSONException {
        URI uri = UriComponentsBuilder.fromHttpUrl(makeUrl("/detailCommon1"))
            .queryParam("pageNo", 1)
            .queryParam("contentId", contentId)
            .queryParam("defaultYN", "Y")
            .queryParam("firstImageYN", "Y")
            .queryParam("areacodeYN", "Y")
            .queryParam("catcodeYN", "Y")
            .queryParam("addrinfoYN", "Y")
            .queryParam("overviewYN", "Y")
            .build(true).toUri();

        ResponseEntity<String> commonResponse = restTemplate.exchange(uri, HttpMethod.GET,
            httpEntity, String.class);

        return getBody(commonResponse.getBody());
    }

    private JSONObject getIntro(long contentId) throws JSONException {
        URI uri = UriComponentsBuilder.fromHttpUrl(makeUrl("/detailIntro1"))
            .queryParam("pageNo", 1)
            .queryParam("contentId", contentId)
            .queryParam("contentTypeId", CONTENT_TYPE_ID)
            .build(true).toUri();

        ResponseEntity<String> introResponse = restTemplate.exchange(uri, HttpMethod.GET,
            httpEntity, String.class);

        return getBody(introResponse.getBody());
    }

    private JSONObject getInfo(long contentId) throws JSONException {
        URI uri = UriComponentsBuilder.fromHttpUrl(makeUrl("/detailInfo1"))
            .queryParam("pageNo", 1)
            .queryParam("contentId", contentId)
            .queryParam("contentTypeId", CONTENT_TYPE_ID)
            .build(true).toUri();

        ResponseEntity<String> infoResponse = restTemplate.exchange(uri, HttpMethod.GET,
            httpEntity, String.class);

        return getBody(infoResponse.getBody());
    }

    private JSONObject getImages(long contentId) throws JSONException {
        URI uri = UriComponentsBuilder.fromHttpUrl(makeUrl("/detailImage1"))
            .queryParam("pageNo", 1)
            .queryParam("contentId", contentId)
            .queryParam("imageYN", "Y")
            .queryParam("subImageYN", "Y")
            .build(true).toUri();

        ResponseEntity<String> imageResponse = restTemplate.exchange(uri, HttpMethod.GET,
            httpEntity, String.class);

        return getBody(imageResponse.getBody());
    }

    private JSONArray getItems(JSONObject jsonObject) {
        return jsonObject.getJSONObject("items").getJSONArray("item");
    }

    private JSONObject getBody(String source) {
        return new JSONObject(source).getJSONObject("response").getJSONObject("body");
    }

    private boolean hasRoom(JSONArray info) throws JSONException {
        boolean hasRoom = false;

        for (int i = 0; i < info.length(); i++) {
            if (Integer.parseInt(info.getJSONObject(i).getString("roomcount")) != 0) {
                hasRoom = true;
            }
        }

        return hasRoom;
    }

    private Accommodation saveAccommodation(
        JSONObject base,
        JSONObject common,
        JSONObject intro
    ) throws JSONException {
        AccommodationOption option = AccommodationOption.builder()
            .cooking(intro.get("chkcooking").equals("가능"))
            .parking(intro.get("parkinglodging").equals("가능"))
            .pickup(intro.get("pickup").equals("가능"))
            .barbecue(intro.get("barbecue").equals("1"))
            .fitness(intro.get("fitness").equals("1"))
            .karaoke(intro.get("karaoke").equals("1"))
            .sports(intro.get("sports").equals("1"))
            .seminar(intro.get("seminar").equals("1"))
            .build();

        Accommodation accommodation = Accommodation.builder()
            .name(base.getString("title"))
            .address(
                Address.builder()
                    .address(base.getString("addr1"))
                    .detailAddress(base.getString("addr2"))
                    .build()
            )
            .description(common.getString("overview"))
            .thumbnail(base.getString("firstimage"))
            .images(new ArrayList<>())
            .option(option)
            .build();

        return accommodationRepository.save(accommodation);
    }

    private void saveAccommodationImages(Accommodation accommodation, JSONArray images) {
        for (int k = 0; k < images.length(); k++) {
            accommodationImageRepository.save(AccommodationImage.builder()
                .accommodation(accommodation)
                .url(images.getJSONObject(k).getString("originimgurl"))
                .build());
        }
    }

    private void saveRooms(
        Accommodation accommodation,
        JSONObject intro,
        JSONArray rooms
    ) throws JSONException {
        for (int i = 0; i < rooms.length(); i++) {
            JSONObject roomJson = rooms.getJSONObject(i);

            if (roomJson.getInt("roombasecount") == 0 && roomJson.getInt("roommaxcount") == 0) {
                continue;
            }

            if (Integer.parseInt(roomJson.getString("roomcount")) != 0) {
                String[] stringCheckIn = intro.getString("checkintime").split(":|;|시");
                String[] stringCheckOut = intro.getString("checkouttime").split(":|;|시");
                LocalTime checkIn = getTimeFromString(stringCheckIn);
                LocalTime checkOut = getTimeFromString(stringCheckOut);

                int offWeekDaysMinFee = Integer.parseInt(
                    roomJson.getString("roomoffseasonminfee1")) == 0 ? DEFAULT_PRICE
                    : Integer.parseInt(
                        roomJson.getString("roomoffseasonminfee1"));
                int offWeekendMinFee = Math.max(Integer.parseInt(
                    roomJson.getString("roomoffseasonminfee2")) == 0 ? DEFAULT_PRICE
                    : Integer.parseInt(
                        roomJson.getString("roomoffseasonminfee2")), offWeekDaysMinFee);
                int peakWeekDaysMinFee = Math.max(Integer.parseInt(
                    roomJson.getString("roompeakseasonminfee1")) == 0 ? DEFAULT_PRICE
                    : Integer.parseInt(
                        roomJson.getString("roompeakseasonminfee1")), offWeekendMinFee);
                int peakWeekendMinFee = Math.max(Integer.parseInt(
                    roomJson.getString("roompeakseasonminfee2")) == 0 ? DEFAULT_PRICE
                    : Integer.parseInt(
                        roomJson.getString("roompeakseasonminfee2")), peakWeekDaysMinFee);

                RoomPrice price = RoomPrice.builder()
                    .offWeekDaysMinFee(offWeekDaysMinFee)
                    .offWeekendMinFee(offWeekendMinFee)
                    .peakWeekDaysMinFee(peakWeekDaysMinFee)
                    .peakWeekendMinFee(peakWeekendMinFee)
                    .build();
                RoomOption option = RoomOption.builder()
                    .airCondition(roomJson.get("roomaircondition").equals("Y"))
                    .tv(roomJson.get("roomtv").equals("Y"))
                    .internet(roomJson.get("roominternet").equals("Y"))
                    .build();

                Room room = roomRepository.save(Room.builder()
                    .accommodation(accommodation)
                    .name(roomJson.getString("roomtitle"))
                    .standard(
                        roomJson.getInt("roombasecount"))
                    .capacity(Math.max(roomJson.getInt("roombasecount"),
                        roomJson.getInt("roommaxcount")))
                    .checkInTime(checkIn)
                    .checkOutTime(checkOut)
                    .price(price)
                    .option(option)
                    .images(new ArrayList<>())
                    .amount(Integer.parseInt(roomJson.getString("roomcount")))
                    .status(RoomStatus.SELLING)
                    .build());

                for (int k = 0; k < 30; k++) {
                    roomStockRepository.save(RoomStock.builder()
                        .room(room)
                        .count(Integer.parseInt(roomJson.getString("roomcount")))
                        .date(LocalDate.now().plusDays(k))
                        .build());
                }

                for (int k = 1; k <= 5; k++) {
                    if (!roomJson.get("roomimg" + k).equals("")) {
                        roomImageRepository.save(RoomImage.builder()
                            .room(room)
                            .url(roomJson.getString("roomimg" + k))
                            .build());
                    }
                }
            }
        }
    }

    private boolean isEmpty(JSONObject body) throws JSONException {
        return body.getInt("totalCount") == 0;
    }

    private LocalTime getTimeFromString(String[] stringTime) {
        int hour = Integer.parseInt(
            stringTime[0].trim().substring(stringTime[0].trim().length() - 2));
        int minute =
            stringTime.length == 1 ? 0 : Integer.parseInt(stringTime[1].trim().substring(0, 2));

        return LocalTime.of(hour, minute);
    }

    private void checkInfo(JSONObject info) {
        if (isEmpty(info)) {
            log.info("[OpenAPI] 반복 정보 조회에 데이터가 없습니다. 다음 숙소를 조회합니다.");
            throw new InvalidDataException();
        }
    }

    private void checkRoom(JSONArray rooms) {
        if (!hasRoom(rooms)) {
            log.info("[OpenAPI] 숙소에 방이 없습니다. 다음 숙소를 조회합니다.");
            throw new InvalidDataException();
        }
    }

    private void checkAccommodationImage(JSONObject image) {
        if (isEmpty(image)) {
            log.info("[OpenAPI] 숙소 이미지가 없습니다. 다음 숙소를 조회합니다.");
            throw new InvalidDataException();
        }
    }

    private void checkRoomImage(JSONArray info) {
        for (int i = 0; i < info.length(); i++) {
            JSONObject roomJson = info.getJSONObject(i);

            if (roomJson.getInt("roombasecount") == 0 && roomJson.getInt("roommaxcount") == 0) {
                continue;
            }

            if (Integer.parseInt(roomJson.getString("roomcount")) != 0) {
                if (roomJson.get("roomimg1").equals("")) {
                    log.info("[OpenAPI] 객실 이미지가 없습니다. 다음 숙소를 조회합니다.");
                    throw new InvalidDataException();
                }
            }
        }
    }

    private void checkCommon(JSONObject common) {
        if (isEmpty(common)) {
            log.info("[OpenAPI] 공통 정보 조회에 데이터가 없습니다. 다음 숙소를 조회합니다.");
            throw new InvalidDataException();
        }
    }

    private void checkIntro(JSONObject intro) {
        if (isEmpty(intro)) {
            log.info("[OpenAPI] 소개 정보 조회에 데이터가 없습니다. 다음 숙소를 조회합니다.");
            throw new InvalidDataException();
        }
    }

    private void checkIntroItem(JSONObject introItem) {
        if (introItem.getString("checkintime").trim().isEmpty() || introItem.getString(
            "checkouttime").trim().isEmpty()) {
            log.info("[OpenAPI] 체크인 체크아웃 데이터가 없습니다. 다음 숙소를 조회합니다.");
            throw new InvalidDataException();
        }

        if (introItem.getString("checkintime").split(":|;|시").length != 2
            || introItem.getString("checkouttime").split(":|;|시").length != 2) {
            log.info("[OpenAPI] 체크인 체크아웃 데이터가 형식에 맞지 않습니다. 다음 숙소를 조회합니다.");
            throw new InvalidDataException();
        }
    }

    private void checkStay(JSONObject stay) {
        if (stay.getString("firstimage").isEmpty()) {
            log.info("[OpenAPI] 썸네일로 사용할 이미지가 없습니다. 다음 숙소를 조회합니다.");
            throw new InvalidDataException();
        }
    }
}
