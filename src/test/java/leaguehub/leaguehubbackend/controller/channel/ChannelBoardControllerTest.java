package leaguehub.leaguehubbackend.controller.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import leaguehub.leaguehubbackend.domain.channel.controller.ChannelBoardController;
import leaguehub.leaguehubbackend.domain.channel.dto.*;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelBoard;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelBoardRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelBoardService;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelBoardControllerTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ChannelBoardController channelBoardController;
    @Autowired
    ChannelService channelService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    ChannelBoardRepository channelBoardRepository;
    @Autowired
    ChannelBoardService channelBoardService;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ChannelRuleRepository channelRuleRepository;
    Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(UserFixture.createMember());
        UserFixture.setUpAuth();
    }

    Channel createCustomChannel(Boolean tier, Boolean playCount, Integer tierMax, Integer tierMin, int playCountMin) throws Exception {
        Member ironMember = memberRepository.save(UserFixture.createCustomeMember("썹맹구"));
        Member unrankedMember = memberRepository.save(UserFixture.createCustomeMember("서초임"));
        Member platinumMember = memberRepository.save(UserFixture.createCustomeMember("손성한"));
        Member masterMember = memberRepository.save(UserFixture.createCustomeMember("채수채수밭"));
        Member alreadyMember = memberRepository.save(UserFixture.createCustomeMember("요청한사람"));
        Member rejectedMember = memberRepository.save(UserFixture.createCustomeMember("거절된사람"));
        Member doneMember1 = memberRepository.save(UserFixture.createCustomeMember("참가된사람1"));
        Member doneMember2 = memberRepository.save(UserFixture.createCustomeMember("참가된사람2"));
        Member observer1 = memberRepository.save(UserFixture.createCustomeMember("관전자1"));
        Member observer2 = memberRepository.save(UserFixture.createCustomeMember("관전자2"));

        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesCustomChannelDto(tier, playCount, tierMax, tierMin, playCountMin);
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl());
        ChannelRule channelRule = ChannelRule.createChannelRule(channel, channelDto.getTier(), channelDto.getTierMax(), channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelRuleRepository.save(channelRule);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));
        participantRepository.save(Participant.participateChannel(unrankedMember, channel));
        participantRepository.save(Participant.participateChannel(ironMember, channel));
        participantRepository.save(Participant.participateChannel(platinumMember, channel));
        participantRepository.save(Participant.participateChannel(masterMember, channel));
        participantRepository.save(Participant.participateChannel(observer1, channel));
        participantRepository.save(Participant.participateChannel(observer2, channel));

        Participant alreadyParticipant = participantRepository.save(Participant.participateChannel(alreadyMember, channel));
        Participant rejectedParticipant = participantRepository.save(Participant.participateChannel(rejectedMember, channel));
        Participant doneParticipant1 = participantRepository.save(Participant.participateChannel(doneMember1, channel));
        Participant doneParticipant2 = participantRepository.save(Participant.participateChannel(doneMember2, channel));

        alreadyParticipant.updateParticipantStatus("participantGameId1", "bronze ii", "participantNickname1", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        rejectedParticipant.rejectParticipantRequest();
        doneParticipant1.updateParticipantStatus("participantGameId2", "platinum ii", "participantNickname2", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        doneParticipant2.updateParticipantStatus("participantGameId3", "iron ii", "participantNickname3", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        doneParticipant1.approveParticipantMatch();
        doneParticipant2.approveParticipantMatch();

        return channel;
    }

    @Test
    @DisplayName("게시판 만들기 - 성공")
    void createChannelBoard() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        ChannelBoardDto channelBoardDto = ChannelFixture.createChannelBoardDto();
        String json = objectMapper.writeValueAsString(channelBoardDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/" + channel.get().getChannelLink() + "/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.boardIndex").value(4))
                .andDo(print());
    }

    @Test
    @DisplayName("게시판 만들기 - 실패(권한없음)")
    void 트FailCreateChannelBoard() throws Exception {
        Channel customChannel = createCustomChannel(false, false, 1100, null, 100);
        Channel findChannel = channelRepository.save(customChannel);

        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");

        participantRepository.save(Participant.participateChannel(test, findChannel));

        ChannelBoardDto channelBoardDto = ChannelFixture.createChannelBoardDto();
        String json = objectMapper.writeValueAsString(channelBoardDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/" + findChannel.getChannelLink() + "/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("게시판 불러오기(제목 + 내용) - 성공")
    void loadChannelBoard() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/"
                        + channel.get().getChannelLink() + "/" + channelBoards.get(0).getId())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content").value("공지사항을 작성해주세요."));

    }

    @Test
    @DisplayName("게시판 불러오기(제목 + 내용) - 실패(채널에 존재하지 않는 게시판 ID)")
    void FailLoadChannelBoard() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/"
                        + channel.get().getChannelLink() + "/1231145d")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("게시판 업데이트 - 성공")
    void updateChannelBoard() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());
        ChannelBoardDto channelBoardDto = ChannelFixture.updateChannelBoardDto();
        String json = objectMapper.writeValueAsString(channelBoardDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/"
                                + channel.get().getChannelLink() + "/" + channelBoards.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/"
                        + channel.get().getChannelLink() + "/" + channelBoards.get(0).getId())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content").value("test1"));
    }

    @Test
    @DisplayName("게시판 업데이트 - 실패(권한없음)")
    void failUpdateChannelBoard_NoAuth() throws Exception {
        Channel customChannel = createCustomChannel(false, false, 1100, null, 100);
        Channel findChannel = channelRepository.save(customChannel);

        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");
        participantRepository.save(Participant.participateChannel(test, findChannel));
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(findChannel.getId());
        ChannelBoardDto channelBoardDto = ChannelFixture.createChannelBoardDto();
        String json = objectMapper.writeValueAsString(channelBoardDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/" + findChannel.getChannelLink() +
                                "/" + channelBoards.get(0).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("게시판 업데이트 - 실패(채널에 존재하지 않는 게시판 ID)")
    void failUpdateChannelBoard_Invalid_board_id() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());
        ChannelBoardDto channelBoardDto = ChannelFixture.updateChannelBoardDto();
        String json = objectMapper.writeValueAsString(channelBoardDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/"
                                + channel.get().getChannelLink() + "/123141515")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("게시판 삭제 - 성공")
    void deleteChannelBoard() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/channel/"
                        + channel.get().getChannelLink() + "/" + channelBoards.get(0).getId())
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("게시판 삭제 - 실패(권한없음)")
    void failDeleteChannelBoard_NoAuth() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());
        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");
        participantRepository.save(Participant.participateChannel(test, channel.get()));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/channel/"
                        + channel.get().getChannelLink() + "/" + channelBoards.get(0).getId())
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("게시판 삭제 - 실패(채널에 존재하지 않는 게시판 ID)")
    void failDeleteChannelBoard_Invalid_board_id() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/channel/"
                        + channel.get().getChannelLink() + "/" + "No_valid_id")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("게시판 인덱스 업데이트 - 성공")
    void updateIndex() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoardService.loadChannelBoards(channel.get().getChannelLink()).getChannelBoardLoadDtoList();
        channelBoardLoadDtoList.get(0).setBoardIndex(3);
        channelBoardLoadDtoList.get(2).setBoardIndex(1);
        ChannelBoardIndexListDto channelBoardIndexListDto = new ChannelBoardIndexListDto();
        channelBoardIndexListDto.setChannelBoardLoadDtoList(channelBoardLoadDtoList);

        String json = objectMapper.writeValueAsString(channelBoardIndexListDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/"
                                + channel.get().getChannelLink() + "/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("게시판 인덱스 업데이트 - 실패(권한 없음)")
    void failUpdateIndex() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoardService.loadChannelBoards(channel.get().getChannelLink()).getChannelBoardLoadDtoList();
        channelBoardLoadDtoList.get(0).setBoardIndex(3);
        channelBoardLoadDtoList.get(2).setBoardIndex(1);

        ChannelBoardIndexListDto channelBoardIndexListDto = new ChannelBoardIndexListDto();
        channelBoardIndexListDto.setChannelBoardLoadDtoList(channelBoardLoadDtoList);

        String json = objectMapper.writeValueAsString(channelBoardIndexListDto);

        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");
        participantRepository.save(Participant.participateChannel(test, channel.get()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/"
                                + channel.get().getChannelLink() + "/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

    @Test
    @DisplayName("채널 보드 불러오기 테스트 - 화면 구성")
    void loadChannelBoards() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/"
                                + channel.get().getChannelLink() + "/boards")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }


}