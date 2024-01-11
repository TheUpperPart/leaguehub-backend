package leaguehub.leaguehubbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import leaguehub.leaguehubbackend.dto.notice.Notice;
import leaguehub.leaguehubbackend.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Notice-Controller", description = "공지사항 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "TFT 공지사항 가져오기", description = "TFT 공지사항을 가져온다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TFT 공지사항 가져오기", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Notice.class))),
    })
    @GetMapping("/notice/{target}")
    public List<Notice> findTargetNotice(@PathVariable("target") String target) {
        return noticeService.getNotice(target);
    }

}