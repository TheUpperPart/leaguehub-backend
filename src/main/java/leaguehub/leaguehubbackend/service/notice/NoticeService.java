package leaguehub.leaguehubbackend.service.notice;

import leaguehub.leaguehubbackend.dto.notice.Notice;
import leaguehub.leaguehubbackend.exception.notice.exception.NoticeUnsupportedException;
import leaguehub.leaguehubbackend.exception.notice.exception.WebScrapingException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class NoticeService {
    private static final String TFT_URL = "https://www.leagueoflegends.com/ko-kr/news/game-updates/";
    private static final String TFT_SELECTOR = "#gatsby-focus-wrapper > div > div.style__Wrapper-sc-1ynvx8h-0.style__ResponsiveWrapper-sc-1ynvx8h-6.bNRNtU.dzWqHp > div > div.style__Wrapper-sc-106zuld-0.style__ResponsiveWrapper-sc-106zuld-4.enQqER.jYHLfd.style__List-sc-1ynvx8h-3.qfKFn > div > ol > li";
    private static final String TFT_TITLE_SELECTOR = "a > article > div.style__Info-sc-1h41bzo-6.eBtwVi > div > h2";
    private static final String LOL_URL = "https://www.leagueoflegends.com/ko-kr/news/notices/";
    private static final String LOL_SELECTOR = "#gatsby-focus-wrapper > div > div.style__Wrapper-sc-1ynvx8h-0.style__ResponsiveWrapper-sc-1ynvx8h-6.bNRNtU.dzWqHp > div > div.style__Wrapper-sc-106zuld-0.style__ResponsiveWrapper-sc-106zuld-4.enQqER.jYHLfd.style__List-sc-1ynvx8h-3.qfKFn > div > ol > li";
    private static final String LOL_TITLE_SELECTOR = "a > article > div.style__Info-sc-1h41bzo-6.eBtwVi > div > h2";
    private static final String FC_URL = "https://fconline.nexon.com/news/notice/list";
    private static final String FC_SELECTOR = "#divListPart > div.board_list > div.content > div.list_wrap > div.tbody > div:nth-child(%d) > a";
    private static final String FC_TITLE_SELECTOR = "a > span.td.subject";
    private static final String HOS_URL = "https://news.blizzard.com/ko-kr/hearthstone";
    private static final String HOS_SELECTOR = "#recent-articles > li:nth-child(%d) > article > a";

    public List<Notice> getNotice(String target) throws NoticeUnsupportedException {
        return switch (target) {
            case "tft" -> scrapeRiotNotice(TFT_URL, TFT_SELECTOR, TFT_TITLE_SELECTOR);
            case "lol" -> scrapeRiotNotice(LOL_URL, LOL_SELECTOR, LOL_TITLE_SELECTOR);
            case "fc" -> scrapeNotices(FC_URL, FC_SELECTOR, FC_TITLE_SELECTOR, 4, 9);
            case "hos" -> scrapeNotices(HOS_URL, HOS_SELECTOR, null, 1, 6);
            case "main" -> getLeagueHubNotice();
            default -> throw new NoticeUnsupportedException();
        };
    }

    private List<Notice> scrapeNotices(String url, String itemSelector, String titleSelector, Integer start, Integer end) throws WebScrapingException {
        List<Notice> notices = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();

            int startIndex = start != null ? start : 1;
            int endIndex = end != null ? end : doc.select(itemSelector).size();

            return IntStream.rangeClosed(startIndex, endIndex)
                    .mapToObj(i -> doc.select(String.format(itemSelector, i)))
                    .filter(elements -> !elements.isEmpty())
                    .map(Elements::first).filter(Objects::nonNull)
                    .map(newsItem ->
                            createNoticeFromElement(newsItem, titleSelector))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new WebScrapingException();
        }
    }

    private List<Notice> scrapeRiotNotice(String url, String itemSelector, String titleSelector) {
        List<Notice> notices = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();

            Elements newsItems = doc.select(
                    "#gatsby-focus-wrapper > div > div.style__Wrapper-sc-1ynvx8h-0.style__ResponsiveWrapper-sc-1ynvx8h-6.bNRNtU.dzWqHp > div > div.style__Wrapper-sc-106zuld-0.style__ResponsiveWrapper-sc-106zuld-4.enQqER.jYHLfd.style__List-sc-1ynvx8h-3.qfKFn > div > ol > li");

            for (Element item : newsItems) {
                String newsLink = item.select("a").attr("abs:href");
                String title = item.select(itemSelector).text();
                String metaData = item.select(
                                titleSelector)
                        .text();

                Notice notice = Notice.builder()
                        .noticeLink(newsLink)
                        .noticeTitle(title)
                        .noticeInfo(metaData)
                        .build();

                notices.add(notice);
            }
        } catch (Exception e) {
            throw new WebScrapingException();
        }
        return notices;
    }

    private Notice createNoticeFromElement(Element element, String titleSelector) {
        String newsLink = element.select("a").attr("abs:href");
        String title = titleSelector != null ? element.select(titleSelector).text() : element.text();

        return Notice.builder()
                .noticeLink(newsLink)
                .noticeTitle(title)
                .build();
    }

    private List<Notice> getLeagueHubNotice() {
        List<Notice> notices = new ArrayList<>();

        notices.add(createNotice("리그허브 서비스 오픈", "리그허브 서비스를 오픈 했습니다."));
        notices.add(createNotice("리그허브 서비스 안정화", "리그허브 서비스를 안정화했습니다."));
        notices.add(createNotice("리그허브 이벤트 안내", "리뷰하고 치킨을 받자"));

        return notices;
    }

    private Notice createNotice(String title, String info) {
        return Notice.builder()
                .noticeTitle(title)
                .noticeInfo(info)
                .build();
    }
}