package leaguehub.leaguehubbackend.service.participant;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.participant.ParticipantDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseStatusPlayerDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserGameInfoDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.RequestStatus;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.email.exception.UnauthorizedEmailException;
import leaguehub.leaguehubbackend.exception.participant.exception.*;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.repository.member.MemberRepository;
import leaguehub.leaguehubbackend.repository.particiapnt.ParticipantRepository;
import leaguehub.leaguehubbackend.service.member.MemberService;
import leaguehub.leaguehubbackend.util.SecurityUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class ParticipantServiceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

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
    private ChannelRuleRepository channelRuleRepository;

    Member getMemberId() throws Exception {

        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        String personalId = userDetails.getUsername();

        Member member = memberService.validateMember(personalId);

        return member;
    }


    Channel createCustomChannel(Boolean tier, Boolean playCount, String tierMax, String tierMin, int playCountMin) throws Exception {
        Member member = memberRepository.save(UserFixture.createMember());
        Member ironMember = memberRepository.save(UserFixture.createCustomeMember("썹맹구"));
        Member unrankedMember = memberRepository.save(UserFixture.createCustomeMember("서초임"));
        Member platinumMember = memberRepository.save(UserFixture.createCustomeMember("손성한"));
        Member masterMember = memberRepository.save(UserFixture.createCustomeMember("채수채수밭"));
        Member alreadyMember = memberRepository.save(UserFixture.createCustomeMember("요청한사람"));
        Member rejectedMember = memberRepository.save(UserFixture.createCustomeMember("거절된사람"));
        Member doneMember1 = memberRepository.save(UserFixture.createCustomeMember("참가된사람1"));
        Member doneMember2 = memberRepository.save(UserFixture.createCustomeMember("참가된사람2"));

        CreateChannelDto channelDto = ChannelFixture.createAllPropertiesCustomChannelDto(tier, playCount, tierMax, tierMin, playCountMin);
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGame(), channelDto.getParticipationNum(),
                channelDto.getTournament(), channelDto.getChannelImageUrl(),
                channelDto.getTier(), channelDto.getTierMax(),
                channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));
        participantRepository.save(Participant.participateChannel(unrankedMember, channel));
        participantRepository.save(Participant.participateChannel(ironMember, channel));
        participantRepository.save(Participant.participateChannel(platinumMember, channel));
        participantRepository.save(Participant.participateChannel(masterMember, channel));

        Participant alreadyParticipant = participantRepository.save(Participant.participateChannel(alreadyMember, channel));
        Participant rejectedParticipant = participantRepository.save(Participant.participateChannel(rejectedMember, channel));
        Participant doneParticipant1 = participantRepository.save(Participant.participateChannel(doneMember1, channel));
        Participant doneParticipant2 = participantRepository.save(Participant.participateChannel(doneMember2, channel));

        alreadyParticipant.updateParticipantStatus("participantGameId1", "bronze", "participantNickname1");
        rejectedParticipant.rejectParticipantRequest();
        doneParticipant1.updateParticipantStatus("participantGameId2", "platinum", "participantNickname2");
        doneParticipant2.updateParticipantStatus("participantGameId3", "iron", "participantNickname3");
        doneParticipant1.approveParticipantMatch();
        doneParticipant2.approveParticipantMatch();

        return channel;
    }

    @NotNull
    private Participant getParticipant(String DummyName1, Channel channel, String DummyGameId1, String DummyNickname1) {
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember(DummyName1));
        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        dummy1.updateParticipantStatus(DummyGameId1, "platinum", DummyNickname1);
        return dummy1;
    }


    @Test
    @DisplayName("티어, 플레이 횟수 검색 테스트 - 성공")
    void getDetailSuccessTest() throws Exception {

        ResponseUserGameInfoDto testDto1 = participantService.getTierAndPlayCount("손성한");
        ResponseUserGameInfoDto testDto2 = participantService.getTierAndPlayCount("서초임");
        ResponseUserGameInfoDto testDto3 = participantService.getTierAndPlayCount("채수채수밭");


        assertThat(testDto2.getTier()).isEqualTo("UNRANKED");

        assertThat(testDto2.getPlayCount()).isEqualTo(0);

        System.out.println(testDto3.getTier() + testDto3.getGrade());
    }

    @Test
    @DisplayName("티어, 플레이 횟수 검색 테스트 - 실패")
    void getDetailFailTest() throws Exception {

        assertThatThrownBy(() -> participantService.getTierAndPlayCount("saovkovsk"))
                .isInstanceOf(ParticipantGameIdNotFoundException.class);
    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어, 판수 제한 x) - 성공")
    void participateDefaultMatchSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, "Silver iv", null, 100);
        UserFixture.setUpCustomAuth("서초임");

        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("서초임");

        participantService.participateMatch(responseDto);

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        responseDto.getChannelLink()).get();

        //then
        assertThat(participant.getGameTier()).isEqualToIgnoringCase("unranked none");
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);
    }

    @Test
    @DisplayName("해당 채널의  경기 참가 테스트 (티어, 판수 제한 o) - 성공")
    void participatelimitedMatchSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, "diamond iii", null, 100);
        UserFixture.setUpCustomAuth("손성한");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("손성한");

        participantService.participateMatch(responseDto);

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        responseDto.getChannelLink()).get();

        //then
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);
    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 마스터 20000점 이하, 판수 제한 o) - 성공")
    void participatelimitedMatchMasterSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, "master 20000", null, 20);
        UserFixture.setUpCustomAuth("채수채수밭");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("채수채수밭");

        participantService.participateMatch(responseDto);

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        responseDto.getChannelLink()).get();

        //then
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);
    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (중복) - 실패")
    void participateDuplicatedMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, "Silver iv", null, 100);
        UserFixture.setUpCustomAuth("서초임");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("participantGameId2");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantDuplicatedGameIdException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (참가된사람) - 실패")
    void participateDoneMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, "Silver iv", null, 100);
        UserFixture.setUpCustomAuth("참가된사람1");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("participantGameId2");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidRoleException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (이미참가요청한사람) - 실패")
    void participateAlreadyMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, "Silver iv", null, 100);
        UserFixture.setUpCustomAuth("요청한사람");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("participantGameId1");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantAlreadyRequestedException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (거절된사람) - 실패")
    void participateRejectedMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, "Silver iv", null, 100);
        UserFixture.setUpCustomAuth("거절된사람");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("participantGameId4");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantRejectedRequestedException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 제한 o) - 실패")
    void participatelimitedTierMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, false, "Silver iv", null, 100);
        UserFixture.setUpCustomAuth("손성한");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("손성한");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidRankException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 최소제한 o) - 실패")
    void participatelimitedTierMatchFailTest_tierMin() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, false, null, "Master 2000", 100);
        UserFixture.setUpCustomAuth("손성한");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("손성한");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidRankException.class);

    }


    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (판수 제한 o) - 실패")
    void participateLimitedPlayCountMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, true, "Silver iv", null, 100);
        UserFixture.setUpCustomAuth("서초임");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("서초임");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidPlayCountException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 마스터 100점 이하, 판수 제한 o) - 실패")
    void participatelimitedMatchMasterFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        UserFixture.setUpCustomAuth("채수채수밭");
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("채수채수밭");

        //when
        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidRankException.class);
    }

    @Test
    @DisplayName("채널 경기 참여자 조회 테스트")
    void loadPlayerTest() throws Exception {
        //given
        Member member = memberRepository.save(UserFixture.createMember());
        Member ironMember = memberRepository.save(UserFixture.createCustomeMember("썹맹구"));
        Member unrankedMember = memberRepository.save(UserFixture.createCustomeMember("서초임"));
        Member platinumMember = memberRepository.save(UserFixture.createCustomeMember("손성한"));
        CreateChannelDto channelDto = ChannelFixture.createChannelDto();
        Channel channel = Channel.createChannel(channelDto.getTitle(),
                channelDto.getGame(), channelDto.getParticipationNum(),
                channelDto.getTournament(), channelDto.getChannelImageUrl(),
                channelDto.getTier(), channelDto.getTierMax(),
                channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));
        participantRepository.save(Participant.participateChannel(unrankedMember, channel));
        Participant part2 = participantRepository.save(Participant.participateChannel(platinumMember, channel));
        Participant part3 = participantRepository.save(Participant.participateChannel(ironMember, channel));

        part2.updateParticipantStatus("손성한", "platinum III", "손성한");
        part3.updateParticipantStatus("썹맹구", "iron III", "썹맹구");

        part2.approveParticipantMatch();
        part3.approveParticipantMatch();


        //when
        List<ResponseStatusPlayerDto> requestPlayerDto = participantService.loadPlayers(channel.getChannelLink());

        //then
        assertThat(part2.getNickname()).isEqualTo(requestPlayerDto.get(0).getNickname());
        assertThat(part2.getGameId()).isEqualTo(requestPlayerDto.get(0).getGameId());

        assertThat(part3.getNickname()).isEqualTo(requestPlayerDto.get(1).getNickname());
        assertThat(part3.getGameId()).isEqualTo(requestPlayerDto.get(1).getGameId());


        assertThat(part2.getRequestStatus()).isEqualTo(RequestStatus.DONE);
        assertThat(part3.getRequestStatus()).isEqualTo(RequestStatus.DONE);

    }

    @Test
    @DisplayName("요청된사람 조회 테스트 (관리자 x) - 실패")
    public void loadRequestStatusPlayerListFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));

        dummy1.updateParticipantStatus("DummyGameId1", "platinum III", "DummyNickname1");
        dummy2.updateParticipantStatus("DummyGameId2", "iron III", "DummyNickname2");

        //when
        assertThatThrownBy(() -> participantService.loadRequestStatusPlayerList(channel.getChannelLink()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("요청된사람 조회 테스트 (관리자 o) - 성공")
    public void loadRequestStatusPlayerListTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));

        dummy1.updateParticipantStatus("DummyGameId1", "platinum III", "DummyNickname1");

        //when
        List<ResponseStatusPlayerDto> DtoList = participantService.loadRequestStatusPlayerList(channel.getChannelLink());


        //then
        assertThat(dummy1.getId()).isEqualTo(DtoList.get(0).getPk());
        assertThat(dummy1.getNickname()).isEqualTo(DtoList.get(0).getNickname());
        assertThat(dummy1.getGameId()).isEqualTo(DtoList.get(0).getGameId());
        assertThat(dummy1.getGameTier()).isEqualTo(DtoList.get(0).getTier());
        assertThat(dummy1.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);

    }


    @Test
    @DisplayName("요청된사람 승인 테스트 (관리자 o) - 성공")
    public void approveParticipantSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);

        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        //when
        participantService.approveParticipantRequest(channel.getChannelLink(), dummy1.getId());

        Participant updateDummy = participantRepository.findParticipantByIdAndChannel_ChannelLink(dummy1.getId(), channel.getChannelLink()).get();
        Optional<Channel> channel1 = channelRepository.findByChannelLink(channel.getChannelLink());
        int updateRealPlayerCount = 3;

        //then
        assertThat(updateDummy.getId()).isEqualTo(dummy1.getId());
        assertThat(updateDummy.getRole().getNum()).isEqualTo(Role.PLAYER.getNum());
        assertThat(updateDummy.getRequestStatus().getNum()).isEqualTo(RequestStatus.DONE.getNum());
        assertThat(channel1.get().getRealPlayer()).isEqualTo(updateRealPlayerCount);

    }

    @Test
    @DisplayName("요청된사람 승인 테스트 (관리자 x) - 실패")
    public void approveParticipantFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        assertThatThrownBy(() -> participantService.approveParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("요청된사람 거절 테스트 (관리자 o) - 성공")
    public void rejectedParticipantSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        //when
        participantService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId());

        Participant updateDummy = participantRepository.findParticipantByIdAndChannel_ChannelLink(dummy1.getId(), channel.getChannelLink()).get();

        //then
        assertThat(updateDummy.getId()).isEqualTo(dummy1.getId());
        assertThat(updateDummy.getRole().getNum()).isEqualTo(Role.OBSERVER.getNum());
        assertThat(updateDummy.getRequestStatus().getNum()).isEqualTo(RequestStatus.REJECT.getNum());

    }

    @Test
    @DisplayName("요청된사람 거절 테스트 (관리자 x) - 실패")
    public void rejectedParticipantFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");


        assertThatThrownBy(() -> participantService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(InvalidParticipantAuthException.class);


    }

    @Test
    @DisplayName("참가한사람 거절 테스트 (관리자 o) - 성공")
    public void rejectedPlayerSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");
        dummy1.approveParticipantMatch();

        //when
        participantService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId());

        Participant updateDummy = participantRepository.findParticipantByIdAndChannel_ChannelLink(dummy1.getId(), channel.getChannelLink()).get();

        //then
        assertThat(updateDummy.getId()).isEqualTo(dummy1.getId());
        assertThat(updateDummy.getRole().getNum()).isEqualTo(Role.OBSERVER.getNum());
        assertThat(updateDummy.getRequestStatus().getNum()).isEqualTo(RequestStatus.REJECT.getNum());

    }

    @Test
    @DisplayName("참가한사람 거절 테스트 (관리자 x) - 실패")
    public void rejectedPlayerFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        assertThatThrownBy(() -> participantService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("관전자 조회 테스트 (관리자 o) - 성공")
    public void loadObserverListTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));


        //when
        List<ResponseStatusPlayerDto> DtoList = participantService.loadObserverPlayerList(channel.getChannelLink());

        //then
        assertThat(dummy1.getId()).isEqualTo(DtoList.get(0).getPk());
        assertThat(dummy1.getNickname()).isEqualTo(DtoList.get(0).getNickname());
        assertThat(dummy1.getGameId()).isEqualTo(DtoList.get(0).getGameId());
        assertThat(dummy1.getGameTier()).isEqualTo(DtoList.get(0).getTier());
        assertThat(dummy1.getRequestStatus()).isEqualTo(RequestStatus.NO_REQUEST);

        assertThat(dummy2.getId()).isEqualTo(DtoList.get(1).getPk());
        assertThat(dummy2.getNickname()).isEqualTo(DtoList.get(1).getNickname());
        assertThat(dummy2.getGameId()).isEqualTo(DtoList.get(1).getGameId());
        assertThat(dummy2.getGameTier()).isEqualTo(DtoList.get(1).getTier());
        assertThat(dummy2.getRequestStatus()).isEqualTo(RequestStatus.NO_REQUEST);

    }

    @Test
    @DisplayName("관전자 조회 테스트 (관리자 x) - 실패")
    public void loadObserverListFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));

        //when
        assertThatThrownBy(() -> participantService.loadObserverPlayerList(channel.getChannelLink()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("관리자 권한 부여 테스트 (관리자 o) - 성공")
    public void updateHostParticipantSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));
        dummy1.updateParticipantStatus("DummyGameId1", "platinum", "DummyNickname1");

        //when
        participantService.updateHostRole(channel.getChannelLink(), dummy1.getId());
        participantService.updateHostRole(channel.getChannelLink(), dummy2.getId());

        Participant updateDummy1 = participantRepository.findParticipantByIdAndChannel_ChannelLink(dummy1.getId(), channel.getChannelLink()).get();
        Participant updateDummy2 = participantRepository.findParticipantByIdAndChannel_ChannelLink(dummy2.getId(), channel.getChannelLink()).get();

        //then
        assertThat(updateDummy1.getId()).isEqualTo(dummy1.getId());
        assertThat(updateDummy1.getRole().getNum()).isEqualTo(Role.HOST.getNum());
        assertThat(updateDummy1.getRequestStatus().getNum()).isEqualTo(RequestStatus.NO_REQUEST.getNum());

        assertThat(updateDummy2.getId()).isEqualTo(dummy2.getId());
        assertThat(updateDummy2.getRole().getNum()).isEqualTo(Role.HOST.getNum());
        assertThat(updateDummy2.getRequestStatus().getNum()).isEqualTo(RequestStatus.NO_REQUEST.getNum());

    }

    @Test
    @DisplayName("관리자권한 부여 테스트 (관리자 x) - 실패")
    public void updateHostParticipantFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        assertThatThrownBy(() -> participantService.updateHostRole(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("요청된사람 승인 테스트 (최대 인원수 초과) - 실패")
    public void approveParticipantCountFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);
        String[] nickName = new String[13];

        for (int i = 0; i < nickName.length; i++) {
            nickName[i] = "더미" + i;
            Participant dummyParticipant = getParticipant(nickName[i], channel, nickName[i], nickName[i]);
            dummyParticipant.approveParticipantMatch();
        }

        Participant dummy = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");
        participantService.approveParticipantRequest(channel.getChannelLink(), dummy.getId());

        Participant dummy1 = getParticipant("DummyName2", channel, "DummyGameId2", "DummyNickname2");

        assertThatThrownBy(() -> participantService.approveParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(ParticipantRealPlayerIsMaxException.class);

    }

    @Test
    @DisplayName("채널 참여 - 성공")
    void participantChannelSuccess() throws Exception {
        //given
        Member dummyMember = memberRepository.save(UserFixture.createCustomeMember("참가할사람"));
        UserFixture.setUpCustomAuth("참가할사람");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);

        //when
        Participant participant = participantService.participateChannel(channel.getChannelLink());
        //then

        assertThat(participant.getChannel().getChannelLink()).isEqualTo(channel.getChannelLink());
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.NO_REQUEST);
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
    }

    @Test
    @DisplayName("채널 중복 참여 - 실패")
    void participantChannelDuplicateFail() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);

        //then
        assertThatThrownBy(() -> participantService.participateChannel(channel.getChannelLink()))
                .isInstanceOf(ParticipantDuplicatedGameIdException.class);

    }

    @Test
    @DisplayName("채널 나가기 - 성공")
    void participantChannelLeaveSuccess() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, "master 100", null, 20);

        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        String personalId = userDetails.getUsername();
        Member member = memberService.validateMember(personalId);

        System.out.println("member nickname = " + member.getNickname());
        System.out.println("member nickname = " + member.getId());

        long count = participantRepository.count();

        //when
        participantService.leaveChannel(channel.getChannelLink());

        //then
        long deleteCount = participantRepository.count();

        assertThat(deleteCount).isEqualTo(count - 1);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (이메일 미인증) - 실패")
    void participateUnAuthMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Member guestMember = memberRepository.save(UserFixture.createGuestMember());
        UserFixture.setUpCustomGuest("idGuest");

        Channel channel = createCustomChannel(false, false, "Silver iv", null, 100);
        ParticipantDto responseDto = new ParticipantDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("urlGuestGameId");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(UnauthorizedEmailException.class);

    }


}