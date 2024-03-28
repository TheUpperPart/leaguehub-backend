package leaguehub.leaguehubbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.domain.channel.dto.CreateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelBoard;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.service.ParticipantManagementService;
import leaguehub.leaguehubbackend.domain.participant.service.ParticipantRoleAndPermissionService;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.ParticipantFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelBoardRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import leaguehub.leaguehubbackend.domain.participant.service.ParticipantService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    ChannelRuleRepository channelRuleRepository;

    @Autowired
    ParticipantManagementService participantManagementService;
    @Autowired
    ParticipantRoleAndPermissionService participantRoleAndPermissionService;

    @Autowired
    ObjectMapper mapper;

    Channel createCustomChannel(Boolean tier, Boolean playCount, Integer tierMax, Integer tierMin, int playCountMin){
        Member member = memberRepository.save(UserFixture.createMember());
        Member ironMember = memberRepository.save(UserFixture.createCustomeMember("썹맹구"));
        Member unrankedMember = memberRepository.save(UserFixture.createCustomeMember("서초임"));
        Member platinumMember = memberRepository.save(UserFixture.createCustomeMember("연습용아이디가됨"));
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

    @NotNull
    private Participant getParticipant(String participantDummyName, Channel channel, String participantDummyGameId, String participantDummyNickname) {
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember(participantDummyName));
        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        dummy1.updateParticipantStatus(participantDummyGameId, "platinum", participantDummyNickname, "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        return dummy1;
    }

    @Test
    @DisplayName("티어 조회 테스트 (참여 x) - 성공")
    void searchTierSuccessTest() throws Exception {

        mockMvc.perform(get("/api/participant/stat?gameid=서초임&gamecategory=0"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.tier").value("UNRANKED"))
                .andExpect(jsonPath("$.playCount").value(0));

    }

    @Test
    @DisplayName("티어 조회 테스트 (참여 x) - 실패")
    void searchTierFailTest() throws Exception {

        mockMvc.perform(get("/api/participant/stat?gameid=saovkovsk&gamecategory=0"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }
    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어, 판수 제한 x) - 성공")
    void participateDefaultMatchSuccessTest() throws Exception {

        Channel channel = createCustomChannel(false, false, 800, null, 100);
        UserFixture.setUpCustomAuth("썹맹구");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("썹맹구");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (판수 제한 o) - 성공")
    void participateLimitedPlayCountMatchSuccessTest() throws Exception {

        Channel channel = createCustomChannel(false, true, 2400, null, 1);
        UserFixture.setUpCustomAuth("연습용아이디가됨");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("연습용아이디가됨");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (마스터 10000점 이하, 판수 제한 o) - 성공")
    void participateLimitedMasterMatchSuccessTest() throws Exception {

        Channel channel = createCustomChannel(true, true, 2400, null, 1);
        UserFixture.setUpCustomAuth("연습용아이디가됨");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("연습용아이디가됨");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (중복) - 실패")
    public void participateDuplicatedMatchFailTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 800, null, 100);
        UserFixture.setUpCustomAuth("서초임");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("participantGameId3");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (참가된사람) - 실패")
    public void participantDoneMatchFailTest() throws Exception {
        //given
        Channel channel = createCustomChannel(true, false, 800, null, 100);
        UserFixture.setUpCustomAuth("참가된사람1");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("참가된사람1");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (이미참가요청한사람) - 실패")
    public void participantAlreadyMatchFailTest() throws Exception {
        //given
        Channel channel = createCustomChannel(true, false, 800, null, 100);
        UserFixture.setUpCustomAuth("요청한사람");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("요청한사람");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (거절된사람) - 실패")
    public void participantRejectedMatchFailTest() throws Exception {
        //given
        Channel channel = createCustomChannel(true, false, 800, null, 100);
        UserFixture.setUpCustomAuth("거절된사람");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("거절된사람");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 제한) - 실패")
    void participateLimitedTierMatchFailTest() throws Exception {

        Channel channel = createCustomChannel(true, false, 400, null, 1);
        UserFixture.setUpCustomAuth("연습용아이디가됨");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("연습용아이디가됨");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (횟수 제한) - 실패")
    void participateLimitedPlayCountMatchFailTest() throws Exception {

        Channel channel = createCustomChannel(true, true, 800, null, 100);
        UserFixture.setUpCustomAuth("서초임");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("서초임");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (마스터 100점 이하, 횟수 제한) - 실패")
    void participateLimitedMasterMatchFailTest() throws Exception {

        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        UserFixture.setUpCustomAuth("채수채수밭");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("채수채수밭");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (챌린저, 횟수 제한) - 실패")
    void participateLimitedMinMasterMatchFailTest() throws Exception {

        Channel channel = createCustomChannel(true, true, null,
                3200, 20);
        UserFixture.setUpCustomAuth("채수채수밭");

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("채수채수밭");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/"+ channel.getChannelLink()+"/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("채널 경기 참여자 조회 테스트")
    void loadPlayerTest() throws Exception {

        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("id");

        mockMvc.perform(get("/api/" + channel.getChannelLink() + "/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].nickname").value("participantNickname2"))
                .andExpect(jsonPath("[0].gameId").value("participantGameId2"))
                .andExpect(jsonPath("[1].nickname").value("participantNickname3"))
                .andExpect(jsonPath("[1].gameId").value("participantGameId3"))
                .andDo(print());

    }


    @Test
    @DisplayName("요청된사람 조회 테스트 (관리자 x) - 실패")
    public void loadRequestStatusPlayerListFailTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("참가된사람1");

        mockMvc.perform(get("/api/" + channel.getChannelLink() + "/player/requests"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("요청된사람 조회 테스트 (관리자 o) - 성공")
    public void loadRequestStatusPlayerListSuccessTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("id");

        mockMvc.perform(get("/api/" + channel.getChannelLink() + "/player/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].nickname").value("participantNickname1"))
                .andExpect(jsonPath("[0].gameId").value("participantGameId1"));

    }


    @Test
    @DisplayName("요청한사람 승인 테스트 (관리자 o) - 성공")
    public void approveParticipantSuccessTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("id");

        Participant dummy1 = getParticipant("DummyName", channel, "DummyGameId", "DummyNickname");


        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/" + dummy1.getId() +"/player"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("요청한사람 승인 테스트 (관리자 x) - 실패")
    public void approveParticipantFailTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("참가된사람1");

        Participant dummy1 = getParticipant("DummyName", channel, "DummyGameId", "DummyNickname");

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/" + dummy1.getId() +"/player"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("요청한사람 거절 테스트 (관리자 o) - 성공")
    public void rejectedParticipantSuccessTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("id");

        Participant dummy1 = getParticipant("DummyName", channel, "DummyGameId", "DummyNickname");

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/" + dummy1.getId() +"/observer"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("요청한사람 거절 테스트 (관리자 x) - 실패")
    public void rejectedParticipantFailTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("참가된사람1");

        Participant dummy1 = getParticipant("DummyName", channel, "DummyGameId", "DummyNickname");

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/" + dummy1.getId() +"/observer"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("참가한사람 거절 테스트 (관리자 o) - 성공")
    public void rejectedPlayerSuccessTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("id");

        Participant dummy1 = getParticipant("DummyName", channel, "DummyGameId", "DummyNickname");
        dummy1.approveParticipantMatch();

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/" + dummy1.getId() +"/observer"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("참가한사람 거절 테스트 (관리자 x) - 실패")
    public void rejectedPlayerFailTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("참가된사람1");

        Participant dummy1 = getParticipant("DummyName", channel, "DummyGameId", "DummyNickname");
        dummy1.approveParticipantMatch();

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/" + dummy1.getId() +"/observer"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("관리자 부여 테스트 (관리자 o) - 성공")
    public void updateHostSuccessTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("id");

        Participant dummy1 = getParticipant("DummyName", channel, "DummyGameId", "DummyNickname");

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/" + dummy1.getId() +"/host"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("관리자 부여 테스트 (관리자 x) - 실패")
    public void updateHostFailTest() throws Exception {
        //given
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("참가된사람1");

        Participant dummy1 = getParticipant("DummyName", channel, "DummyGameId", "DummyNickname");

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/" + dummy1.getId() +"/host"))
                .andExpect(status().isUnauthorized());

    }


    @Test
    @DisplayName("채널 관전자 조회 테스트 (관전자 o) - 성공")
    void loadObserverSuccessTest() throws Exception {

        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("id");

        mockMvc.perform(get("/api/" + channel.getChannelLink() +"/observers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].nickname").value("관전자1"))
                .andExpect(jsonPath("[1].nickname").value("관전자2"))
                .andDo(print());

    }

    @Test
    @DisplayName("채널 관전자 조회 테스트 (관전자 x) - 실패")
    void loadObserverFailTest() throws Exception {

        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        UserFixture.setUpCustomAuth("참가된사람1");

        mockMvc.perform(get("/api/" + channel.getChannelLink() +"/observers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("요청된사람 승인 테스트 (최대 인원수 초과) - 실패")
    public void approveParticipantCountFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(false, false, 2400, null, 20);
        String[] nickName = new String[13];

        for (int i = 0; i < nickName.length; i++) {
            nickName[i] = "더미" + i;
            Participant dummyParticipant = getParticipant(nickName[i], channel, nickName[i], nickName[i]);
            dummyParticipant.approveParticipantMatch();
        }
        Participant dummy = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");
        participantRoleAndPermissionService.approveParticipantRequest(channel.getChannelLink(), dummy.getId());

        Participant dummy1 = getParticipant("DummyName2", channel, "DummyGameId2", "DummyNickname2");

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/" + dummy1.getId() +"/player"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("채널 참여 - 성공")
    void participantChannelSuccess() throws Exception {
        //given
        memberRepository.save(UserFixture.createCustomeMember("참가할사람"));
        UserFixture.setUpCustomAuth("참가할사람");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/participant/observer"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("채널 중복 참여 - 실패")
    void participantChannelDuplicateFail() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);

        //then
        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/participant/observer"))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (이메일 미인증) - 실패")
    void participateUnAuthMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Member guestMember = memberRepository.save(UserFixture.createGuestMember());
        UserFixture.setUpCustomGuest("Guest");

        Channel channel = createCustomChannel(false, false, 800, null, 100);

        ParticipantDto participantResponseDto = ParticipantFixture.createParticipantResponseDto("연습용아이디가됨");
        String dtoToJson = mapper.writeValueAsString(participantResponseDto);

        mockMvc.perform(post("/api/" + channel.getChannelLink() + "/participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoToJson))
                .andExpect(status().isUnauthorized());


    }

    @Test
    @DisplayName("채널 커스텀 인덱스 API 테스트")
    void channelCustomIndexAPI() throws Exception {
        Member findMember = memberRepository.save(UserFixture.createCustomeMember("test"));
        UserFixture.setUpCustomAuth("test");

        List<Channel> channels = IntStream.range(0, 3)
                .mapToObj(i -> createCustomChannel(false, false, 800, null, 100))
                .peek(channel -> {
                    channelRepository.save(channel);
                    participantManagementService.participateChannel(channel.getChannelLink());
                })
                .collect(Collectors.toList());

        List<ParticipantChannelDto> participantChannelDtos = IntStream.range(0, 3)
                .mapToObj(i -> {
                    ParticipantChannelDto dto = new ParticipantChannelDto();
                    Channel channel = channels.get(i);
                    dto.setChannelLink(channel.getChannelLink());
                    dto.setImgSrc(channel.getChannelImageUrl());
                    dto.setTitle(channel.getTitle());
                    dto.setGameCategory(channel.getGameCategory().getNum());
                    dto.setCustomChannelIndex(2 - i);  // Reversed index
                    return dto;
                })
                .collect(Collectors.toList());
        String json = mapper.writeValueAsString(participantChannelDtos);

        mockMvc.perform(post("/api/channels/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());
    }


}