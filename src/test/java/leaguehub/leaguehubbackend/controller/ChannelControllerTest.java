package leaguehub.leaguehubbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import leaguehub.leaguehubbackend.controller.channel.ChannelController;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.service.channel.ChannelService;
import leaguehub.leaguehubbackend.service.member.MemberService;
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

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ChannelController channelController;

    @Autowired
    ChannelService channelService;

    @Autowired
    MemberService memberService;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        memberRepository.save(UserFixture.createMember());
        UserFixture.setUpAuth();
    }

    @Test
    @DisplayName("채널 생성 테스트")
    public void testCreateChannel() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        String json = objectMapper.writeValueAsString(createChannelDto);
        System.out.println(json);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("채널 생성 테스트 - 실패")
    public void testFailCreateChannel() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.invalidatedTournamentData();
        String json = objectMapper.writeValueAsString(createChannelDto);
        System.out.println(json);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    @DisplayName("채널 바인딩 에러 테스트 - 컨트롤러")
    void createChannelControllerError() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.bindingResultCheck();
        String json = objectMapper.writeValueAsString(createChannelDto);
        System.out.println(json);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("채널 정보 가져오기 테스트")
    void getChannelTest() throws Exception {
        Long id = channelService.createChannel(ChannelFixture.createChannelDto());
        Optional<Channel> channel = channelRepository.findById(id);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/" + channel.get().getChannelLink()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.leagueTitle").value("test"))
                .andExpect(jsonPath("$.game").value("TFT"))
                .andExpect(jsonPath("$.hostName").value("test"));
    }

    @Test
    @DisplayName("채널 정보 가져오기 실패 테스트 - 유효하지 않은 채널 링크")
    void getChannelFailTest() throws Exception {
        Long id = channelService.createChannel(ChannelFixture.createChannelDto());
        Optional<Channel> channel = channelRepository.findById(id);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/" + "NoValid"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


}