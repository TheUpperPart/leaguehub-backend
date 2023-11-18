package leaguehub.leaguehubbackend.service.notice;


import java.util.ArrayList;
import java.util.List;
import leaguehub.leaguehubbackend.dto.notice.TftNotice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


@Service
@RequiredArgsConstructor
public class NoticeService {

    public List<TftNotice> scrapeTftNotice() {
        List<TftNotice> notices = new ArrayList<>();

        try {
            Document doc = Jsoup.connect("https://www.leagueoflegends.com/ko-kr/news/game-updates/").get();

            Elements newsItems = doc.select("#gatsby-focus-wrapper > div > div.style__Wrapper-sc-1ynvx8h-0.style__ResponsiveWrapper-sc-1ynvx8h-6.bNRNtU.dzWqHp > div > div.style__Wrapper-sc-106zuld-0.style__ResponsiveWrapper-sc-106zuld-4.enQqER.jYHLfd.style__List-sc-1ynvx8h-3.qfKFn > div > ol > li");

            for (Element item : newsItems) {
                String newsLink = item.select("a").attr("abs:href");
                String title = item.select("a > article > div.style__Info-sc-1h41bzo-6.eBtwVi > div > h2").text();
                String metaData = item.select("a > article > div.style__Info-sc-1h41bzo-6.eBtwVi > div > div.style__Meta-sc-1h41bzo-10.hGstqB").text();

                TftNotice notice = TftNotice.builder()
                        .noticeLink(newsLink)
                        .noticeTitle(title)
                        .noticeInfo(metaData)
                        .build();

                notices.add(notice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notices;
    }
}
