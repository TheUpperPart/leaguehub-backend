package leaguehub.leaguehubbackend.controller.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardLoadDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.channel.ResponseCreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.channel.ChannelBoardService;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
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

    @BeforeEach
    void setUp() {
        memberRepository.save(UserFixture.createMember());
        UserFixture.setUpAuth();

    }

    Channel createCustomChannel(Boolean tier, Boolean playCount, String tierMax, String gradeMax, int playCountMin) throws Exception {
        Member member = memberRepository.save(UserFixture.createMember());
        Member ironMember = memberRepository.save(UserFixture.createCustomeMember("1"));
        Member unrankedMember = memberRepository.save(UserFixture.createCustomeMember("2"));
        Member platinumMember = memberRepository.save(UserFixture.createCustomeMember("3"));
        Member masterMember = memberRepository.save(UserFixture.createCustomeMember("4"));
        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesCustomChannelDto(tier, playCount, tierMax, gradeMax, playCountMin);
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGame(), channelDto.getParticipationNum(),
                channelDto.getTournament(), channelDto.getChannelImageUrl(),
                channelDto.getTier(), channelDto.getTierMax(), channelDto.getGradeMax(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));
        participantRepository.save(Participant.participateChannel(unrankedMember, channel));
        participantRepository.save(Participant.participateChannel(ironMember, channel));
        participantRepository.save(Participant.participateChannel(platinumMember, channel));
        participantRepository.save(Participant.participateChannel(masterMember, channel));

        return channel;
    }

    @Test
    @DisplayName("게시판 만들기 - 성공")
    void createChannelBoard() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());

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
        Channel customChannel = createCustomChannel(false, false, "Silver", "i", 100);
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
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_Id(channel.get().getId());

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
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/"
                        + channel.get().getChannelLink() + "/1231145d")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("게시판 업데이트 - 성공")
    void updateChannelBoard() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_Id(channel.get().getId());
        ChannelBoardDto channelBoardDto = ChannelFixture.updateChannelDto();
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
        Channel customChannel = createCustomChannel(false, false, "Silver", "i", 100);
        Channel findChannel = channelRepository.save(customChannel);

        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");
        participantRepository.save(Participant.participateChannel(test, findChannel));
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_Id(findChannel.getId());
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
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_Id(channel.get().getId());
        ChannelBoardDto channelBoardDto = ChannelFixture.updateChannelDto();
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
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_Id(channel.get().getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/channel/"
                        + channel.get().getChannelLink() + "/" + channelBoards.get(0).getId())
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("게시판 삭제 - 실패(권한없음)")
    void failDeleteChannelBoard_NoAuth() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_Id(channel.get().getId());
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
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/channel/"
                        + channel.get().getChannelLink() + "/" + "No_valid_id")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("게시판 인덱스 업데이트 - 성공")
    void updateIndex() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());
        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoardService.loadChannelBoards(channel.get().getChannelLink());
        channelBoardLoadDtoList.get(0).setBoardIndex(3);
        channelBoardLoadDtoList.get(2).setBoardIndex(1);

        String json = objectMapper.writeValueAsString(channelBoardLoadDtoList);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/"
                                + channel.get().getChannelLink() + "/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("게시판 인덱스 업데이트 - 실패(권한 없음)")
    void failUpdateIndex() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ResponseCreateChannelDto responseCreateChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(responseCreateChannelDto.getChannelLink());
        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoardService.loadChannelBoards(channel.get().getChannelLink());
        channelBoardLoadDtoList.get(0).setBoardIndex(3);
        channelBoardLoadDtoList.get(2).setBoardIndex(1);

        String json = objectMapper.writeValueAsString(channelBoardLoadDtoList);

        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");
        participantRepository.save(Participant.participateChannel(test, channel.get()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/"
                                + channel.get().getChannelLink() + "/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }

}