package leaguehub.leaguehubbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import leaguehub.leaguehubbackend.dto.notice.TftNotice;
import leaguehub.leaguehubbackend.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Notice-Controller", description = "공지사항 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NoticeController {

    private final NoticeService noticeService;


    @Operation(summary = "TFT 공지사항 가져오기", description = "TFT 공지사항을 가져온다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TFT 공지사항 가져오기", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TftNotice.class))),
    })
    @GetMapping("/notice/tft")
    public List<TftNotice> assignmentMatches() {

        return noticeService.scrapeTftNotice();
    }
}
