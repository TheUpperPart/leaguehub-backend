package leaguehub.leaguehubbackend.service.participant;

import jakarta.transaction.Transactional;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.participant.ParticipantResponseDto;
import leaguehub.leaguehubbackend.dto.participant.RequestPlayerDto;
import leaguehub.leaguehubbackend.dto.participant.ResponseUserDetailDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.entity.member.Member;
import leaguehub.leaguehubbackend.entity.participant.Participant;
import leaguehub.leaguehubbackend.entity.participant.RequestStatus;
import leaguehub.leaguehubbackend.entity.participant.Role;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantGameIdNotFoundException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantInvalidPlayCountException;
import leaguehub.leaguehubbackend.exception.participant.exception.ParticipantInvalidRankException;
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

    Member getMemberId() throws Exception{

        UserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        String personalId = userDetails.getUsername();

        Member member = memberService.validateMember(personalId);

        return member;
    }


    Channel createCustomChannel(Boolean tier, Boolean playCount, String tierMax, String gradeMax,int playCountMin) throws Exception{
        Member member = memberRepository.save(UserFixture.createMember());
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
        participantRepository.save(Participant.participateChannel(unrankedMember, channel));
        participantRepository.save(Participant.participateChannel(ironMember, channel));
        participantRepository.save(Participant.participateChannel(platinumMember, channel));
        participantRepository.save(Participant.participateChannel(masterMember, channel));

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
        Channel channel = createCustomChannel(false, false, "Silver", "iv",100);
        UserFixture.setUpCustomAuth("서초임");

        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setNickname("서초임");

        ChannelRule channelRule = channelRuleRepository.findChannelRuleByChannel_ChannelLink(responseDto.getChannelLink());
        System.out.println("chnnelRule = " + channelRule);
        participantService.participateMatch(responseDto);

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        responseDto.getChannelLink());

        //then
        assertThat(participant.getGameTier()).isEqualToIgnoringCase("unranked");
        assertThat(participant.getRole()).isEqualTo(Role.PLAYER);
    }
    @Test
    @DisplayName("해당 채널의  경기 참가 테스트 (티어, 판수 제한 o) - 성공")
    void participatelimitedMatchSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, "diamond", "iv",100);
        UserFixture.setUpCustomAuth("손성한");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setNickname("손성한");

        participantService.participateMatch(responseDto);

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        responseDto.getChannelLink());

        //then
        assertThat(participant.getGameTier()).isEqualToIgnoringCase("platinum");
        assertThat(participant.getRole()).isEqualTo(Role.PLAYER);
    }
    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 마스터 20000점 이하, 판수 제한 o) - 성공")
    void participatelimitedMatchMasterSuccessTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, "master", "20000",20);
        UserFixture.setUpCustomAuth("채수채수밭");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setNickname("채수채수밭");

        participantService.participateMatch(responseDto);

        //when

        Participant participant = participantRepository.
                findParticipantByMemberIdAndChannel_ChannelLink(
                        getMemberId().getId(),
                        responseDto.getChannelLink());

        //then
        assertThat(participant.getGameTier()).isEqualToIgnoringCase("challenger");
        assertThat(participant.getRole()).isEqualTo(Role.PLAYER);
    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 제한 o) - 실패")
    void participatelimitedTierMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, false, "Silver", "iv",100);
        UserFixture.setUpCustomAuth("손성한");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setNickname("손성한");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidRankException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (판수 제한 o) - 실패")
    void participateLimitedPlayCountMatchFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(false, true, "Silver", "iv",100);
        UserFixture.setUpCustomAuth("서초임");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setNickname("서초임");

        assertThatThrownBy(() -> participantService.participateMatch(responseDto))
                .isInstanceOf(ParticipantInvalidPlayCountException.class);

    }

    @Test
    @DisplayName("해당 채널의 경기 참가 테스트 (티어 마스터 100점 이하, 판수 제한 o) - 실패")
    void participatelimitedMatchMasterFailTest() throws Exception {
        //given, 역할이 OBSERVER인 참가자, 해당 채널, 해당 채널 룰, 유저 디테일
        Channel channel = createCustomChannel(true, true, "master", "100",20);
        UserFixture.setUpCustomAuth("채수채수밭");
        ParticipantResponseDto responseDto = new ParticipantResponseDto();
        responseDto.setChannelLink(channel.getChannelLink());
        responseDto.setNickname("채수채수밭");

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


        //when
        List<RequestPlayerDto> requestPlayerDto = participantService.loadPlayers(channel.getChannelLink());

        //then
        assertThat(part2.getNickname()).isEqualTo(requestPlayerDto.get(0).getName());
        assertThat(part2.getGameId()).isEqualTo(requestPlayerDto.get(0).getGameId());

        assertThat(part3.getNickname()).isEqualTo(requestPlayerDto.get(1).getName());
        assertThat(part3.getGameId()).isEqualTo(requestPlayerDto.get(1).getGameId());


        assertThat(part2.getRequestStatus()).isEqualTo(RequestStatus.DONE);
        assertThat(part3.getRequestStatus()).isEqualTo(RequestStatus.DONE);
    }






}