package leaguehub.leaguehubbackend.service.participant;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.participant.ParticipantResponseDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseStatusPlayerDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserDetailDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.RequestStatus;
import leaguehub.leaguehubbackend.entity.participant.Role;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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


    Channel createCustomChannel(Boolean tier, Boolean playCount, String tierMax, String gradeMax, int playCountMin) throws Exception {
        Member member = memberRepository.save(UserFixture.createMember());
        Member ironMember = memberRepository.save(UserFixture.createCustomeMember("썹맹구"));
        Member unrankedMember = memberRepository.save(UserFixture.createCustomeMember("서초임"));
        Member platinumMember = memberRepository.save(UserFixture.createCustomeMember("손성한"));
        Member masterMember = memberRepository.save(UserFixture.createCustomeMember("채수채수밭"));
        Member alreadyMember = memberRepository.save(UserFixture.createCustomeMember("요청한사람"));
        Member rejectedMember = memberRepository.save(UserFixture.createCustomeMember("거절된사람"));
        Member doneMember1 = memberRepository.save(UserFixture.createCustomeMember("참가된사람1"));
        Member doneMember2 = memberRepository.save(UserFixture.createCustomeMember("참가된사람2"));

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

        Participant alreadyParticipant = participantRepository.save(Participant.participateChannel(alreadyMember, channel));
        Participant rejectedParticipant = participantRepository.save(Participant.participateChannel(rejectedMember, channel));
        Participant doneParticipant1 = participantRepository.save(Participant.participateChannel(doneMember1, channel));
        Participant doneParticipant2 = participantRepository.save(Participant.participateChannel(doneMember2, channel));

        alreadyParticipant.updateParticipantStatus("bronze", "bronze");
        rejectedParticipant.rejectParticipantRequest();
        doneParticipant1.updateParticipantStatus("참가된사람1", "platinum");
        doneParticipant2.updateParticipantStatus("참가된사람2", "iron");
        doneParticipant1.approveParticipantMatch();
        doneParticipant2.approveParticipantMatch();

        return channel;
    }


    @Test
    @DisplayName("티어, 플레이 횟수 검색 테스트 - 성공")
    void getDetailSuccessTest() throws Exception {

        ResponseUserDetailDto testDto1 = participantService.getTierAndPlayCount("손성한");
        ResponseUserDetailDto testDto2 = participantService.getTierAndPlayCount("서초임");
        ResponseUserDetailDto testDto3 = participantService.getTierAndPlayCount("채수채수밭");


        assertThat(testDto1.getTier()).isEqualTo("PLATINUM");
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
        Channel channel = createCustomChannel(false, false, "Silver", "iv", 100);
        UserFixture.setUpCustomAuth("서초임");

        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("서초임");

        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannel_ChannelLink(responseDto.getChannelLink());
        System.out.println("chnnelRule = " + channelRule);
        participantService.participateMatch(responseDto);

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        responseDto.getChannelLink()).get();

        //then
        assertThat(participant.getGameTier()).isEqualToIgnoringCase("unranked");
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);
    }

    @Test
    @DisplayName("해당 채널의  경기 참가 테스트 (티어, 판수 제한 o) - 성공")
    void participatelimitedMatchSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, "diamond", "iv", 100);
        UserFixture.setUpCustomAuth("손성한");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("손성한");

        participantService.participateMatch(responseDto);

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        responseDto.getChannelLink()).get();

        //then
        assertThat(participant.getGameTier()).isEqualToIgnoringCase("platinum");
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);
    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 마스터 20000점 이하, 판수 제한 o) - 성공")
    void participatelimitedMatchMasterSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, "master", "20000", 20);
        UserFixture.setUpCustomAuth("채수채수밭");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("채수채수밭");

        participantService.participateMatch(responseDto);

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        responseDto.getChannelLink()).get();

        //then
        assertThat(participant.getGameTier()).isEqualToIgnoringCase("challenger");
        assertThat(participant.getRole()).isEqualTo(Role.OBSERVER);
        assertThat(participant.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);
    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (중복) - 실패")
    void participateDuplicatedMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, false, "Silver", "iv", 100);
        UserFixture.setUpCustomAuth("서초임");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("참가된사람1");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantDuplicatedGameIdException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (참가된사람) - 실패")
    void participateDoneMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, false, "Silver", "iv", 100);
        UserFixture.setUpCustomAuth("참가된사람1");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("참가된사람1");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidRoleException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (이미참가요청한사람) - 실패")
    void participateAlreadyMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, false, "Silver", "iv", 100);
        UserFixture.setUpCustomAuth("요청한사람");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("요청한사람");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantAlreadyRequestedException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (거절된사람) - 실패")
    void participateRejectedMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, false, "Silver", "iv", 100);
        UserFixture.setUpCustomAuth("거절된사람");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("거절된사람");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantRejectedRequestedException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 제한 o) - 실패")
    void participatelimitedTierMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, false, "Silver", "iv", 100);
        UserFixture.setUpCustomAuth("손성한");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("손성한");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidRankException.class);

    }


    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (판수 제한 o) - 실패")
    void participateLimitedPlayCountMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, true, "Silver", "iv", 100);
        UserFixture.setUpCustomAuth("서초임");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setGameId("서초임");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidPlayCountException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 마스터 100점 이하, 판수 제한 o) - 실패")
    void participatelimitedMatchMasterFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, "master", "100", 20);
        UserFixture.setUpCustomAuth("채수채수밭");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
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
                channelDto.getTier(), channelDto.getTierMax(), channelDto.getGradeMax(),
                channelDto.getPlayCount(),
                channelDto.getPlayCountMin());
        channelRepository.save(channel);
        channelBoardRepository.saveAll(ChannelBoard.createDefaultBoard(channel));
        participantRepository.save(Participant.createHostChannel(member, channel));
        participantRepository.save(Participant.participateChannel(unrankedMember, channel));
        Participant part2 = participantRepository.save(Participant.participateChannel(platinumMember, channel));
        Participant part3 = participantRepository.save(Participant.participateChannel(ironMember, channel));

        part2.updateParticipantStatus("손성한", "platinum");
        part3.updateParticipantStatus("썹맹구", "iron");

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
        Channel channel = createCustomChannel(true, true, "master", "100", 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("더미1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("더미2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));

        dummy1.updateParticipantStatus("더미1", "platinum");
        dummy2.updateParticipantStatus("더미2", "iron");

        //when
        assertThatThrownBy(() -> participantService.loadRequestStatusPlayerList(channel.getChannelLink()))
                .isInstanceOf(ParticipantNotGameHostException.class);

    }

    @Test
    @DisplayName("요청된사람 조회 테스트 (관리자 o) - 성공")
    public void loadRequestStatusPlayerListTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master", "100", 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("더미1"));
        Member dummyMember2 = memberRepository.save(UserFixture.createCustomeMember("더미2"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        Participant dummy2 = participantRepository.save(Participant.participateChannel(dummyMember2, channel));

        dummy1.updateParticipantStatus("더미1", "platinum");
        dummy2.updateParticipantStatus("더미2", "iron");

        //when
        List<ResponseStatusPlayerDto> DtoList = participantService.loadRequestStatusPlayerList(channel.getChannelLink());


        //then
        assertThat(dummy1.getId()).isEqualTo(DtoList.get(0).getPk());
        assertThat(dummy1.getNickname()).isEqualTo(DtoList.get(0).getNickname());
        assertThat(dummy1.getGameId()).isEqualTo(DtoList.get(0).getGameId());
        assertThat(dummy1.getGameTier()).isEqualTo(DtoList.get(0).getTier());
        assertThat(dummy1.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);

        assertThat(dummy2.getId()).isEqualTo(DtoList.get(1).getPk());
        assertThat(dummy2.getNickname()).isEqualTo(DtoList.get(1).getNickname());
        assertThat(dummy2.getGameId()).isEqualTo(DtoList.get(1).getGameId());
        assertThat(dummy2.getGameTier()).isEqualTo(DtoList.get(1).getTier());
        assertThat(dummy2.getRequestStatus()).isEqualTo(RequestStatus.REQUEST);

    }


    @Test
    @DisplayName("요청된사람 승인 테스트 (관리자 o) - 성공")
    public void approveParticipantSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master", "100", 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("더미1"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        dummy1.updateParticipantStatus("더미1", "platinum");

        //when
        participantService.approveParticipantRequest(channel.getChannelLink(), dummy1.getId());

        Participant updateDummy = participantRepository.findParticipantByIdAndChannel_ChannelLink(dummy1.getId(), channel.getChannelLink());

        //then
        assertThat(updateDummy.getId()).isEqualTo(dummy1.getId());
        assertThat(updateDummy.getRole().getNum()).isEqualTo(Role.PLAYER.getNum());
        assertThat(updateDummy.getRequestStatus().getNum()).isEqualTo(RequestStatus.DONE.getNum());

    }

    @Test
    @DisplayName("요청된사람 승인 테스트 (관리자 x) - 실패")
    public void approveParticipantFailTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("서초임");
        Channel channel = createCustomChannel(true, true, "master", "100", 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("더미1"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        dummy1.updateParticipantStatus("더미1", "platinum");

        //

        assertThatThrownBy(() -> participantService.approveParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(ParticipantNotGameHostException.class);


    }

    @Test
    @DisplayName("요청된사람 거절 테스트 (관리자 o) - 성공")
    public void rejectedParticipantSuccessTest() throws Exception {
        //given
        UserFixture.setUpCustomAuth("id");
        Channel channel = createCustomChannel(true, true, "master", "100", 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("더미1"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        dummy1.updateParticipantStatus("더미1", "platinum");

        //when
        participantService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId());

        Participant updateDummy = participantRepository.findParticipantByIdAndChannel_ChannelLink(dummy1.getId(), channel.getChannelLink());

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
        Channel channel = createCustomChannel(true, true, "master", "100", 20);
        Member dummyMember1 = memberRepository.save(UserFixture.createCustomeMember("더미1"));

        Participant dummy1 = participantRepository.save(Participant.participateChannel(dummyMember1, channel));
        dummy1.updateParticipantStatus("더미1", "platinum");

        //

        assertThatThrownBy(() -> participantService.rejectedParticipantRequest(channel.getChannelLink(), dummy1.getId()))
                .isInstanceOf(ParticipantNotGameHostException.class);


    }


}