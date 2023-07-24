package leaguehub.leaguehubbackend.controller.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.channel.RequestChannelBoardDto;
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
        Long channelId = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findById(channelId);

        RequestChannelBoardDto channelBoardDto = ChannelFixture.createChannelBoardDto();
        String json = objectMapper.writeValueAsString(channelBoardDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/" + channel.get().getChannelLink() + "/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("게시판 만들기 - 실패(권한없음)")
    void FailCreateChannelBoard() throws Exception {
        Channel customChannel = createCustomChannel(false, false, "Silver", "i", 100);
        Channel findChannel = channelRepository.save(customChannel);

        memberRepository.save(UserFixture.createCustomeMember("test231"));
        UserFixture.setUpCustomAuth("test231");

        RequestChannelBoardDto channelBoardDto = ChannelFixture.createChannelBoardDto();
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
        Long channelId = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findById(channelId);
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel.get());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/"
                        + channel.get().getChannelLink() + "/" + channelBoards.get(0).getId())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.detail").value("공지사항을 작성해주세요."));

    }

    @Test
    @DisplayName("게시판 불러오기(제목 + 내용) - 실패(채널에 존재하지 않는 게시판 ID)")
    void FailLoadChannelBoard() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        Long channelId = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findById(channelId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/"
                        + channel.get().getChannelLink() + "/1231145d")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("게시판 업데이트 - 성공")
    void updateChannelBoard() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        Long channelId = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findById(channelId);
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel.get());
        RequestChannelBoardDto requestChannelBoardDto = ChannelFixture.updateChannelDto();
        String json = objectMapper.writeValueAsString(requestChannelBoardDto);
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
                .andExpect(jsonPath("$.detail").value("test1"));
    }

    @Test
    @DisplayName("게시판 업데이트 - 실패(권한없음)")
    void failUpdateChannelBoard_NoAuth() throws Exception {
        Channel customChannel = createCustomChannel(false, false, "Silver", "i", 100);
        Channel findChannel = channelRepository.save(customChannel);

        memberRepository.save(UserFixture.createCustomeMember("test231"));
        UserFixture.setUpCustomAuth("test231");
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(findChannel);
        RequestChannelBoardDto channelBoardDto = ChannelFixture.createChannelBoardDto();
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
        Long channelId = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findById(channelId);
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel.get());
        RequestChannelBoardDto requestChannelBoardDto = ChannelFixture.updateChannelDto();
        String json = objectMapper.writeValueAsString(requestChannelBoardDto);
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
        Long channelId = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findById(channelId);
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel.get());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/channel/"
                                + channel.get().getChannelLink() + "/" + channelBoards.get(0).getId())
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("게시판 삭제 - 실패(권한없음)")
    void failDeleteChannelBoard_NoAuth() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        Long channelId = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findById(channelId);
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel(channel.get());
        memberRepository.save(UserFixture.createCustomeMember("test1"));
        UserFixture.setUpCustomAuth("test1");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/channel/"
                        + channel.get().getChannelLink() + "/" + channelBoards.get(0).getId())
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("게시판 삭제 - 실패(채널에 존재하지 않는 게시판 ID)")
    void failDeleteChannelBoard_Invalid_board_id() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        Long channelId = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findById(channelId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/channel/"
                        + channel.get().getChannelLink() + "/" + "No_valid_id")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


}