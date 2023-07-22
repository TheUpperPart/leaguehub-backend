package leaguehub.leaguehubbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.Controller.participant.ParticipantController;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.participant.ParticipantResponseDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.ParticipantFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.participant.ParticipantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ParticipantControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ParticipantController participantController;

    @Autowired
    ParticipantService participantService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    ChannelBoardRepository channelBoardRepository;

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    ObjectMapper mapper;

    Channel createCustomChannel(Boolean tier, Boolean playCount, String tierMax, String gradeMax, int playCountMin) throws Exception{
        Member member = memberRepository.save(UserFixture.createMember());
        Member member1 = memberRepository.save(UserFixture.createCustomeMember("더미1"));
        Member member2 = memberRepository.save(UserFixture.createCustomeMember("더미2"));
        Member ironMember = memberRepository.save(UserFixture.createCustomeMember("썹맹구"));
        Member unrankedMember = memberRepository.save(UserFixture.createCustomeMember("서초임"));
        Member platinumMember = memberRepository.save(UserFixture.createCustomeMember("손성한"));
        Member masterMember = memberRepository.save(UserFixture.createCustomeMember("채수채수밭"));
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
        Participant save1 = participantRepository.save(Participant.participateChannel(member1, channel));
        Participant save2 = participantRepository.save(Participant.participateChannel(member2, channel));
        participantRepository.save(Participant.participateChannel(unrankedMember, channel));;
        participantRepository.save(Participant.participateChannel(ironMember, channel));
        participantRepository.save(Participant.participateChannel(platinumMember, channel));
        participantRepository.save(Participant.participateChannel(masterMember, channel));

        save1.updateParticipantStatus("dummy1", "platinum");
        save2.updateParticipantStatus("dummy2", "iron");

        return channel;
    }

    @Test
    @DisplayName("티어 조회 테스트 (참여 x) - 성공")
    void searchTierSuccessTest() throws Exception {

        mockMvc.perform(get("/api/stat?gameid=서초임&gamecategory=0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.tier").value("UNRANKED"))
                .andExpect(jsonPath("$.grade").value("NONE"))
                .andExpect(jsonPath("$.playCount").value(0));

    }

    @Test
    @DisplayName("티어 조회 테스트 (참여 x) - 실패")
    void searchTierFailTest() throws Exception {

        mockMvc.perform(get("/api/stat?gameid=saovkovsk&gamecategory=0"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("참여 여부 테스트 - 성공")
    void participateMatchSuccessTest() throws Exception {
        Channel channel = createCustomChannel(false, false, "Silver", "iv",100);
        UserFixture.setUpCustomAuth("서초임");
        mockMvc.perform(get(("/api/participant/") + channel.getChannelLink()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("참여 여부 테스트 - 실패")
    void participateMatchFailTest() throws Exception {
        Channel channel = createCustomChannel(false, false, "Silver", "iv",100);
        UserFixture.setUpCustomAuth("id");

        mockMvc.perform(get(("/api/participant/") + channel.getChannelLink()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 참가하였거나 경기 관리자입니다."));

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어, 판수 제한 x) - 성공")
    void participateDefaultMatchSuccessTest() throws Exception {

        Channel channel = createCustomChannel(false, false, "Silver", "iv",100);
        UserFixture.setUpCustomAuth("썹맹구");

        ParticipantResponseDto participantResponseDto = ParticipantFixture.createParticipantResponseDto(channel.getChannelLink(), "썹맹구");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/participant/match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoToJson))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (판수 제한 o) - 성공")
    void participateLimitedPlayCountMatchSuccessTest() throws Exception {

        Channel channel = createCustomChannel(false, true, "Silver", "iv",20);
        UserFixture.setUpCustomAuth("손성한");

        ParticipantResponseDto participantResponseDto = ParticipantFixture.createParticipantResponseDto(channel.getChannelLink(), "손성한");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/participant/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (마스터 10000점 이하, 판수 제한 o) - 성공")
    void participateLimitedMasterMatchSuccessTest() throws Exception {

        Channel channel = createCustomChannel(true, true, "master", "10000",20);
        UserFixture.setUpCustomAuth("채수채수밭");

        ParticipantResponseDto participantResponseDto = ParticipantFixture.createParticipantResponseDto(channel.getChannelLink(), "채수채수밭");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/participant/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 제한) - 실패")
    void participateLimitedTierMatchFailTest() throws Exception {

        Channel channel = createCustomChannel(true, false, "Silver", "iv",20);
        UserFixture.setUpCustomAuth("손성한");

        ParticipantResponseDto participantResponseDto = ParticipantFixture.createParticipantResponseDto(channel.getChannelLink(), "손성한");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/participant/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (횟수 제한) - 실패")
    void participateLimitedPlayCountMatchFailTest() throws Exception {

        Channel channel = createCustomChannel(true, true, "Silver", "iv",100);
        UserFixture.setUpCustomAuth("서초임");

        ParticipantResponseDto participantResponseDto = ParticipantFixture.createParticipantResponseDto(channel.getChannelLink(), "서초임");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/participant/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (마스터 100점 이하, 횟수 제한) - 실패")
    void participateLimitedMasterMatchFailTest() throws Exception {

        Channel channel = createCustomChannel(true, true, "master", "100",20);
        UserFixture.setUpCustomAuth("채수채수밭");

        ParticipantResponseDto participantResponseDto = ParticipantFixture.createParticipantResponseDto(channel.getChannelLink(), "채수채수밭");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/participant/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("채널 경기 참여자 조회 테스트")
    void loadPlayerTest() throws Exception {

        Channel channel = createCustomChannel(false, false, "master", "100",20);

        mockMvc.perform(get("/api/player?channelLink=" + channel.getChannelLink()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].name").value("더미1"))
                .andExpect(jsonPath("[0].gameId").value("dummy1"))
                .andExpect(jsonPath("[1].name").value("더미2"))
                .andExpect(jsonPath("[1].gameId").value("dummy2"))
                .andDo(print());

    }


}