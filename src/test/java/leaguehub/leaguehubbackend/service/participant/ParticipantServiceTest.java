package leaguehub.leaguehubbackend.service.participant;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.domain.channel.dto.CreateChannelDto;
import leaguehub.leaguehubbackend.domain.channel.dto.ParticipantChannelDto;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelBoard;
import leaguehub.leaguehubbackend.domain.channel.entity.ChannelRule;
import leaguehub.leaguehubbackend.domain.channel.entity.GameCategory;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelBoardRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRepository;
import leaguehub.leaguehubbackend.domain.channel.repository.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.email.exception.exception.UnauthorizedEmailException;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.member.service.MemberService;
import leaguehub.leaguehubbackend.domain.participant.dto.ParticipantDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ResponseStatusPlayerDto;
import leaguehub.leaguehubbackend.domain.participant.dto.ResponseUserGameInfoDto;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.domain.participant.entity.RequestStatus;
import leaguehub.leaguehubbackend.domain.participant.entity.Role;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.*;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import leaguehub.leaguehubbackend.domain.participant.service.*;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.global.util.SecurityUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class ParticipantServiceTest {

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
    ChannelRuleRepository channelRuleRepository;

    @Autowired
    ParticipantWebClientService participantWebClientService;
    @Autowired
    ParticipantManagementService participantManagementService;
    @Autowired
    ParticipantQueryService participantQueryService;
    @Autowired
    ParticipantRoleAndPermissionService participantRoleAndPermissionService;

    Member getMemberId() throws Exception {

        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        String personalId = userDetails.getUsername();

        Member member = memberService.validateMember(personalId);

        return member;
    }


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
    private Participant getParticipant(String DummyName1, Channel channel, String DummyGameId1, String DummyNickname1) {
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember(DummyName1));
        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        dummy1.updateParticipantStatus(DummyGameId1, "platinum", DummyNickname1, "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        return dummy1;
    }


    @Test
    @DisplayName("티어, 플레이 횟수 검색 테스트 - 성공")
    void getDetailSuccessTest() throws Exception {

        ResponseUserGameInfoDto testDto1 = participantWebClientService.getTierAndPlayCount("손성한");
        ResponseUserGameInfoDto testDto2 = participantWebClientService.getTierAndPlayCount("서초임");
        ResponseUserGameInfoDto testDto3 = participantWebClientService.getTierAndPlayCount("채수채수밭");


        assertThat(testDto2.getTier()).isEqualTo("UNRANKED");

        assertThat(testDto2.getPlayCount()).isEqualTo(0);

        System.out.println(testDto3.getTier());
    }

    @Test
    @DisplayName("티어, 플레이 횟수 검색 테스트 - 실패")
    void getDetailFailTest() throws Exception {

        assertThatThrownBy(() -> participantWebClientService.getTierAndPlayCount("saovkovsk"))
                .isInstanceOf(ParticipantGameIdNotFoundException.class);
    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어, 판수 제한 x) - 성공")
    void participateDefaultMatchSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, 800, null, 100);
        UserFixture.setUpCustomAuth("서초임");

        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("서초임");

        participantManagementService.participateMatch(responseDto, channel.getChannelLink());

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        channel.getChannelLink()).get();

        //then
        assertThat(participant.getGameTier()).isEqualToIgnoringCase("unranked");
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);
    }

    @Test
    @DisplayName("해당 채널의  경기 참가 테스트 (티어, 판수 제한 o) - 성공")
    void participatelimitedMatchSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, 2100, null, 1);
        UserFixture.setUpCustomAuth("연습용아이디가됨");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("연습용아이디가됨");

        participantManagementService.participateMatch(responseDto, channel.getChannelLink());

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        channel.getChannelLink()).get();

        //then
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);
    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 마스터 20000점 이하, 판수 제한 o) - 성공")
    void participatelimitedMatchMasterSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, 2400, null, 1);
        UserFixture.setUpCustomAuth("연습용아이디가됨");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("연습용아이디가됨");

        participantManagementService.participateMatch(responseDto, channel.getChannelLink());

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        channel.getChannelLink()).get();

        //then
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);
    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (중복) - 실패")
    void participateDuplicatedMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, 800, null, 100);
        UserFixture.setUpCustomAuth("서초임");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("participantGameId2");

        assertThatThrownBy(() -> participantManagementService.participateMatch(responseDto, channel.getChannelLink()))
                .isInstanceOf(ParticipantDuplicatedGameIdException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (참가된사람) - 실패")
    void participateDoneMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, 800, null, 100);
        UserFixture.setUpCustomAuth("참가된사람1");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("participantGameId2");

        assertThatThrownBy(() -> participantManagementService.participateMatch(responseDto, channel.getChannelLink()))
                .isInstanceOf(ParticipantInvalidRoleException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (이미참가요청한사람) - 실패")
    void participateAlreadyMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, 800, null, 100);
        UserFixture.setUpCustomAuth("요청한사람");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("participantGameId1");

        assertThatThrownBy(() -> participantManagementService.participateMatch(responseDto, channel.getChannelLink()))
                .isInstanceOf(ParticipantAlreadyRequestedException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (거절된사람) - 실패")
    void participateRejectedMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, 800, null, 100);
        UserFixture.setUpCustomAuth("거절된사람");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("participantGameId4");

        assertThatThrownBy(() -> participantManagementService.participateMatch(responseDto, channel.getChannelLink()))
                .isInstanceOf(ParticipantRejectedRequestedException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 제한 o) - 실패")
    void participatelimitedTierMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, false, 400, null, 100);
        UserFixture.setUpCustomAuth("연습용아이디가됨");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("연습용아이디가됨");

        assertThatThrownBy(() -> participantManagementService.participateMatch(responseDto, channel.getChannelLink()))
                .isInstanceOf(ParticipantInvalidRankException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 최소제한 o) - 실패")
    void participatelimitedTierMatchFailTest_tierMin() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, false, null, 2400, 100);
        UserFixture.setUpCustomAuth("연습용아이디가됨");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("연습용아이디가됨");

        assertThatThrownBy(() -> participantManagementService.participateMatch(responseDto, channel.getChannelLink()))
                .isInstanceOf(ParticipantInvalidRankException.class);

    }


    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (판수 제한 o) - 실패")
    void participateLimitedPlayCountMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, true, 800, null, 100);
        UserFixture.setUpCustomAuth("서초임");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("서초임");

        assertThatThrownBy(() -> participantManagementService.participateMatch(responseDto, channel.getChannelLink()))
                .isInstanceOf(ParticipantInvalidPlayCountException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 마스터 100점 이하, 판수 제한 o) - 실패")
    void participatelimitedMatchMasterFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, 400, null, 20);
        UserFixture.setUpCustomAuth("연습용아이디가됨");
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("연습용아이디가됨");

        //when
        assertThatThrownBy(() -> participantManagementService.participateMatch(responseDto, channel.getChannelLink()))
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
                channelDto.getGameCategory(), channelDto.getMaxPlayer(),
                channelDto.getMatchFormat(), channelDto.getChannelImageUrl());
        ChannelRule channelRule = ChannelRule.createChannelRule(channel,channelDto.getTier(), channelDto.getTierMax(),
                channelDto.getTierMin(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelRuleRepository.save(channelRule);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));
        participantRepository.save(Participant.participateChannel(unrankedMember, channel));
        Participant part2 = participantRepository.save(Participant.participateChannel(platinumMember, channel));
        Participant part3 = participantRepository.save(Participant.participateChannel(ironMember, channel));

        part2.updateParticipantStatus("손성한", "platinum III", "손성한", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        part3.updateParticipantStatus("썹맹구", "iron III", "썹맹구", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");

        part2.approveParticipantMatch();
        part3.approveParticipantMatch();


        //when
        List<ResponseStatusPlayerDto> requestPlayerDto = participantQueryService.loadPlayers(channel.getChannelLink());

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
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));

        dummy1.updateParticipantStatus("DummyGameId1", "platinum III", "DummyNickname1", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");
        dummy2.updateParticipantStatus("DummyGameId2", "iron III", "DummyNickname2", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");

        //when
        assertThatThrownBy(() -> participantQueryService.loadRequestStatusPlayerList(channel.getChannelLink()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("요청된사람 조회 테스트 (관리자 o) - 성공")
    public void loadRequestStatusPlayerListTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));

        dummy1.updateParticipantStatus("DummyGameId1", "platinum III", "DummyNickname1", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");

        //when
        List<ResponseStatusPlayerDto> DtoList = participantQueryService.loadRequestStatusPlayerList(channel.getChannelLink());


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
        Channel channel = createCustomChannel(true, true, 2400, null, 20);

        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        //when
        participantRoleAndPermissionService.approveParticipantRequest(channel.getChannelLink(), dummy1.getId());

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
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        assertThatThrownBy(() -> participantRoleAndPermissionService.approveParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("요청된사람 거절 테스트 (관리자 o) - 성공")
    public void rejectedParticipantSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        //when
        participantRoleAndPermissionService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId());

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
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");


        assertThatThrownBy(() -> participantRoleAndPermissionService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(InvalidParticipantAuthException.class);


    }

    @Test
    @DisplayName("참가한사람 거절 테스트 (관리자 o) - 성공")
    public void rejectedPlayerSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");
        dummy1.approveParticipantMatch();

        //when
        participantRoleAndPermissionService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId());

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
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        assertThatThrownBy(() -> participantRoleAndPermissionService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("관전자 조회 테스트 (관리자 o) - 성공")
    public void loadObserverListTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));


        //when
        List<ResponseStatusPlayerDto> DtoList = participantQueryService.loadObserverPlayerList(channel.getChannelLink());

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
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));

        //when
        assertThatThrownBy(() -> participantQueryService.loadObserverPlayerList(channel.getChannelLink()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("관리자 권한 부여 테스트 (관리자 o) - 성공")
    public void updateHostParticipantSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("DummyName1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("DummyName2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));
        dummy1.updateParticipantStatus("DummyGameId1", "platinum", "DummyNickname1", "xKzO3XyPc7DLH5n6P-XC8z0DvQqhmZy8y8JZZxjXSSvPQ5qXqohUw1sehtNdSYIpsH0ckWagN5wnOQ");

        //when
        participantRoleAndPermissionService.updateHostRole(channel.getChannelLink(), dummy1.getId());
        participantRoleAndPermissionService.updateHostRole(channel.getChannelLink(), dummy2.getId());

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
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        Participant dummy1 = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");

        assertThatThrownBy(() -> participantRoleAndPermissionService.updateHostRole(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(InvalidParticipantAuthException.class);

    }

    @Test
    @DisplayName("요청된사람 승인 테스트 (최대 인원수 초과) - 실패")
    public void approveParticipantCountFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);
        String[] nickName = new String[13];

        for (int i = 0; i < nickName.length; i++) {
            nickName[i] = "더미" + i;
            Participant dummyParticipant = getParticipant(nickName[i], channel, nickName[i], nickName[i]);
            dummyParticipant.approveParticipantMatch();
        }

        Participant dummy = getParticipant("DummyName1", channel, "DummyGameId1", "DummyNickname1");
        participantRoleAndPermissionService.approveParticipantRequest(channel.getChannelLink(), dummy.getId());

        Participant dummy1 = getParticipant("DummyName2", channel, "DummyGameId2", "DummyNickname2");

        assertThatThrownBy(() -> participantRoleAndPermissionService.approveParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(ParticipantRealPlayerIsMaxException.class);

    }

    @Test
    @DisplayName("채널 참여 - 성공")
    void participantChannelSuccess() throws Exception {
        //given
        Member dummyMember = memberRepository.save(UserFixture.createCustomeMember("참가할사람"));
        UserFixture.setUpCustomAuth("참가할사람");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);

        //when
        ParticipantChannelDto participantChannelDto  = participantManagementService.participateChannel(channel.getChannelLink());
        //then

        assertThat(participantChannelDto.getChannelLink()).isEqualTo(channel.getChannelLink());
        assertThat(participantChannelDto.getGameCategory()).isEqualTo(GameCategory.TFT.getNum());
        assertThat(participantChannelDto.getTitle()).isEqualTo(channel.getTitle());
    }

    @Test
    @DisplayName("채널 중복 참여 - 실패")
    void participantChannelDuplicateFail() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);

        //then
        assertThatThrownBy(() -> participantManagementService.participateChannel(channel.getChannelLink()))
                .isInstanceOf(ParticipantDuplicatedGameIdException.class);

    }

    @Test
    @DisplayName("채널 나가기 - 성공")
    void participantChannelLeaveSuccess() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, 2400, null, 20);

        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        String personalId = userDetails.getUsername();
        Member member = memberService.validateMember(personalId);

        System.out.println("member nickname = " + member.getNickname());
        System.out.println("member nickname = " + member.getId());

        long count = participantRepository.count();

        //when
        participantManagementService.leaveChannel(channel.getChannelLink());

        //then
        long deleteCount = participantRepository.count();

        assertThat(deleteCount).isEqualTo(count - 1);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (이메일 미인증) - 실패")
    void participateUnAuthMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Member guestMember = memberRepository.save(UserFixture.createGuestMember());
        UserFixture.setUpCustomGuest("Guest");

        Channel channel = createCustomChannel(false, false, 800, null, 100);
        ParticipantDto responseDto = new ParticipantDto();

        responseDto.setGameId("urlGuestGameId");

        assertThatThrownBy(() -> participantManagementService.participateMatch(responseDto, channel.getChannelLink()))
                .isInstanceOf(UnauthorizedEmailException.class);

    }

    @Test
    @DisplayName("채널 커스텀 정렬")
    void channelCustomIndexOrder() {
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

        participantManagementService.updateCustomChannelIndex(participantChannelDtos);

        List<Participant> all = participantRepository.findAllByMemberIdOrderByIndex(findMember.getId());
        assertThat(all.get(0).getIndex()).isEqualTo(0);
        assertThat(all.get(0).getChannel()).isEqualTo(channels.get(2));
    }



}