package leaguehub.leaguehubbackend.service.channel;

import leaguehub.leaguehubbackend.dto.channel.ChannelBoardDto;
import leaguehub.leaguehubbackend.dto.channel.ChannelBoardLoadDto;
import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.channel.ParticipantChannelDto;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.channel.ChannelBoard;
import leaguehub.leaguehubbackend.entity.channel.ChannelRule;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.domain.participant.entity.Participant;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelBoardNotFoundException;
import leaguehub.leaguehubbackend.exception.channel.exception.ChannelNotFoundException;
import leaguehub.leaguehubbackend.domain.participant.exception.exception.InvalidParticipantAuthException;
import leaguehub.leaguehubbackend.fixture.ChannelFixture;
import leaguehub.leaguehubbackend.fixture.UserFixture;
import leaguehub.leaguehubbackend.repository.channel.ChannelBoardRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRepository;
import leaguehub.leaguehubbackend.repository.channel.ChannelRuleRepository;
import leaguehub.leaguehubbackend.domain.member.repository.MemberRepository;
import leaguehub.leaguehubbackend.domain.participant.repository.ParticipantRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChannelBoardServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ChannelService channelService;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    ChannelBoardService channelBoardService;
    @Autowired
    ChannelBoardRepository channelBoardRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    ChannelRuleRepository channelRuleRepository;
    Member member;
    private String channelLink;

    Channel createCustomChannel(Boolean tier, Boolean playCount, Integer tierMax, Integer tierMin, int playCountMin) throws Exception {
        Member ironMember = memberRepository.save(UserFixture.createCustomeMember("썹맹구"));
        Member unrankedMember = memberRepository.save(UserFixture.createCustomeMember("서초임"));
        Member platinumMember = memberRepository.save(UserFixture.createCustomeMember("손성한"));
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

    @BeforeEach
    void setUp() {
        member = memberRepository.save(UserFixture.createMember());
        UserFixture.setUpAuth();
        CreateChannelDto createChannelDto = ChannelFixture.createChannelDto();
        ParticipantChannelDto participantChannelDto = channelService.createChannel(createChannelDto);
        Optional<Channel> channel = channelRepository.findByChannelLink(participantChannelDto.getChannelLink());
        channelLink = channel.get().getChannelLink();
    }

    @Test
    @DisplayName("게시판 만들기 테스트 - 성공")
    void createChannelBoard() {
        Optional<Channel> channel = channelRepository.findByChannelLink(channelLink);
        channelBoardService.createChannelBoard(channel.get().getChannelLink(), ChannelFixture.createChannelBoardDto());

        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());

        assertThat(channelBoards.size()).isEqualTo(4);
        assertThat(channelBoards.get(channelBoards.size() - 1).getIndex()).isEqualTo(4);
    }

    @Test
    @DisplayName("게시판 만들기 테스트 - 실패 (권한 없음)")
    void invalidMemberChannelBoard() throws Exception {
        Channel customChannel = createCustomChannel(false, false, 800, null, 100);
        Channel findChannel = channelRepository.save(customChannel);

        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");
        participantRepository.save(Participant.participateChannel(test, findChannel));


        Assertions.assertThatThrownBy(() -> channelBoardService.createChannelBoard(findChannel.getChannelLink(),
                        ChannelFixture.createChannelBoardDto()))
                .isInstanceOf(InvalidParticipantAuthException.class);
    }

    @Test
    @DisplayName("게시판 조회 테스트 - 성공")
    void loadBoard() {
        Optional<Channel> channel = channelRepository.findByChannelLink(channelLink);
        channelBoardService.createChannelBoard(channel.get().getChannelLink(), ChannelFixture.createChannelBoardDto());

        List<ChannelBoardLoadDto> findChannelBoards = channelBoardService.loadChannelBoards(channel.get().getChannelLink()).getChannelBoardLoadDtoList();

        ChannelBoardDto channelBoardDto = channelBoardService.getChannelBoard(channel.get().getChannelLink(), findChannelBoards.get(findChannelBoards.size() - 1).getBoardId());

        assertThat(findChannelBoards.size()).isEqualTo(4);
        assertThat(channelBoardDto.getContent()).isEqualTo("test");
    }

    @Test
    @DisplayName("게시판 조회 테스트 - 실패(해당 채널이 아닌 다른 채널의 게시판 ID 값으로 조회, 없는 게시판 ID로 조회)")
    void failLoadBoard() throws Exception {
        Optional<Channel> channel = channelRepository.findByChannelLink(channelLink);
        channelBoardService.createChannelBoard(channel.get().getChannelLink(), ChannelFixture.createChannelBoardDto());
        memberRepository.save(UserFixture.createCustomeMember("test2"));
        UserFixture.setUpCustomAuth("test2");
        Channel customChannel = createCustomChannel(false, false, 800, null, 100);
        Channel findChannel = channelRepository.save(customChannel);

        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(findChannel.getId());

        assertThatThrownBy(() -> channelBoardService.loadChannelBoards("NO_VALID"))
                .isInstanceOf(ChannelNotFoundException.class);

        assertThatThrownBy(() -> channelBoardService.getChannelBoard(channel.get().getChannelLink(), channelBoards.get(0).getId()))
                .isInstanceOf(ChannelBoardNotFoundException.class);
        assertThatThrownBy(() -> channelBoardService.getChannelBoard(channel.get().getChannelLink(), 1414141L))
                .isInstanceOf(ChannelBoardNotFoundException.class);
    }

    @Test
    @DisplayName("게시판 업데이트 테스트 - 성공")
    void updateTest() {
        Optional<Channel> channel = channelRepository.findByChannelLink(channelLink);
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());
        Long boardId = channelBoards.get(0).getId();
        ChannelBoardDto update = ChannelFixture.updateChannelBoardDto();
        channelBoardService.updateChannelBoard(channel.get().getChannelLink(), boardId, update);

        assertThat(channelBoards.get(0).getTitle()).isEqualTo(update.getTitle());
        assertThat(channelBoards.get(0).getContent()).isEqualTo(update.getContent());
    }

    @Test
    @DisplayName("게시판 업데이트 테스트 - 실패(권한없음)")
    void updateFailTest() {
        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");
        Optional<Channel> channel = channelRepository.findByChannelLink(channelLink);
        participantRepository.save(Participant.participateChannel(test, channel.get()));
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());
        Long boardId = channelBoards.get(0).getId();
        ChannelBoardDto update = ChannelFixture.updateChannelBoardDto();

        assertThatThrownBy(() -> channelBoardService.updateChannelBoard(channel.get().getChannelLink(), boardId, update))
                .isInstanceOf(InvalidParticipantAuthException.class);
    }

    @Test
    @DisplayName("게시판 삭제 테스트 - 성공")
    void deleteTest() {
        Optional<Channel> channel = channelRepository.findByChannelLink(channelLink);
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());
        ChannelBoard channelBoard = channelBoards.get(0);
        channelBoardService.deleteChannelBoard(channel.get().getChannelLink(), channelBoard.getId());
        List<ChannelBoard> flushBoard = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());

        assertThat(flushBoard.size()).isEqualTo(2);
        assertThat(flushBoard).doesNotContain(channelBoard);
        for (ChannelBoard board : flushBoard) {
            assertThat(board.getIndex()).isNotEqualTo(3);
        }
    }


    @Test
    @DisplayName("게시판 삭제 테스트 - 실패(권한없음)")
    void deleteFailTest() {
        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");
        Optional<Channel> channel = channelRepository.findByChannelLink(channelLink);
        participantRepository.save(Participant.participateChannel(test, channel.get()));
        List<ChannelBoard> channelBoards = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());
        ChannelBoard channelBoard = channelBoards.get(0);
        assertThatThrownBy(() -> channelBoardService.deleteChannelBoard(channel.get().getChannelLink(), channelBoard.getId()))
                .isInstanceOf(InvalidParticipantAuthException.class);
    }

    @Test
    @DisplayName("게시판 드래그앤드랍 인덱스 테스트 - 성공")
    void updateIndex() {
        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoardService.loadChannelBoards(channelLink).getChannelBoardLoadDtoList();
        channelBoardLoadDtoList.get(0).setBoardIndex(3);
        channelBoardLoadDtoList.get(2).setBoardIndex(1);

        channelBoardService.updateChannelBoardIndex(channelLink, channelBoardLoadDtoList);

        Optional<Channel> channel = channelRepository.findByChannelLink(channelLink);
        List<ChannelBoard> allByChannelId = channelBoardRepository.findAllByChannel_IdOrderByIndex(channel.get().getId());

        for (ChannelBoard channelBoard : allByChannelId) {
            if (channelBoard.getIndex() == 3) {
                assertThat(channelBoard.getTitle()).isEqualTo("리그 공지사항");
            }

            if (channelBoard.getIndex() == 1) {
                assertThat(channelBoard.getTitle()).isEqualTo("참여하기");
            }
        }
    }

    @Test
    @DisplayName("게시판 드래그앤드랍 인덱스 테스트 - 실패(권한없음)")
    void updateIndex_NoAuth() {
        List<ChannelBoardLoadDto> channelBoardLoadDtoList = channelBoardService.loadChannelBoards(channelLink).getChannelBoardLoadDtoList();
        channelBoardLoadDtoList.get(0).setBoardIndex(3);
        channelBoardLoadDtoList.get(2).setBoardIndex(1);

        Member test = UserFixture.createCustomeMember("test231");
        memberRepository.save(test);
        UserFixture.setUpCustomAuth("test231");

        assertThatThrownBy(() -> channelBoardService.updateChannelBoardIndex(channelLink, channelBoardLoadDtoList))
                .isInstanceOf(InvalidParticipantAuthException.class);
    }

}