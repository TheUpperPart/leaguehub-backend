package leaguehub.leaguehubbackend.controller.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import leaguehub.leaguehubbackend.domain.channel.controller.ChannelController;
import leaguehub.leaguehubbackend.domain.channel.dto.ChannelRuleDto;
import leaguehub.leaguehubbackend.domain.channel.dto.CreateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRepository;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.channel.service.ChannelService;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelRuleControllerTest {

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
    @DisplayName("체널 룰 업데이트 컨트롤러 테스트")
    void updateChannelRule() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        ChannelRuleDto updateChannelRuleDto = new ChannelRuleDto();
        updateChannelRuleDto.setTier(true);
        updateChannelRuleDto.setTierMax(800);

        String json = objectMapper.writeValueAsString(updateChannelRuleDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/" + channel.get().getChannelLink() + "/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("체널 룰 업데이트 컨트롤러 테스트 - 실패(티어 유효성)")
    void updateChannelRule_tierValid() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        ChannelRuleDto updateChannelRuleDto = new ChannelRuleDto();
        updateChannelRuleDto.setTier(true);

        String json = objectMapper.writeValueAsString(updateChannelRuleDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/channel/" + channel.get().getChannelLink() + "/rule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("채널 룰 가져오기 테스트")
    void getChannelRule() throws Exception {
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/channel/" + channel.get().getChannelLink() + "/rule"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }


}