package leaguehub.leaguehubbackend.domain.participant.entity;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.domain.channel.entity.Channel;
import leaguehub.leaguehubbackend.domain.member.entity.Member;
import leaguehub.leaguehubbackend.global.audit.BaseTimeEntity;
import leaguehub.leaguehubbackend.global.audit.GlobalConstant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Participant extends BaseTimeEntity {

    @Id
    @Column(name = "participant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String gameId;

    private String gameTier;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus participantStatus;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "custom_channel_index")
    private int index;

    private String puuid;

    public static Participant createHostChannel(Member member, Channel channel) {
        Participant participant = new Participant();
        participant.nickname = member.getNickname();
        participant.profileImageUrl = member.getProfileImageUrl();
        participant.role = Role.HOST;
        participant.member = member;
        participant.channel = channel;


        participant.requestStatus = RequestStatus.NO_REQUEST;

        participant.gameId = GlobalConstant.NO_DATA.getData();
        participant.gameTier = GlobalConstant.NO_DATA.getData();

        return participant;
    }

    public static Participant participateChannel(Member member, Channel channel) {
        Participant participant = new Participant();
        participant.nickname = member.getNickname();
        participant.profileImageUrl = member.getProfileImageUrl();
        participant.role = Role.OBSERVER;
        participant.member = member;
        participant.channel = channel;

        participant.requestStatus = RequestStatus.NO_REQUEST;

        participant.gameId = GlobalConstant.NO_DATA.getData();
        participant.gameTier = GlobalConstant.NO_DATA.getData();

        return participant;
    }


    public Participant approveParticipantMatch() {
        this.requestStatus = RequestStatus.DONE;
        this.role = Role.PLAYER;
        this.participantStatus = ParticipantStatus.PROGRESS;

        return this;
    }


    public Participant rejectParticipantRequest() {
        this.requestStatus = RequestStatus.REJECT;
        this.role = Role.OBSERVER;

        return this;
    }

    public Participant disqualificationParticipant(){
        this.participantStatus = ParticipantStatus.DISQUALIFICATION;

        return this;
    }


    public Participant updateParticipantStatus(String gameId, String gameTier, String nickname, String puuid) {
        this.gameId = gameId;
        this.gameTier = gameTier;
        this.nickname = nickname;
        this.puuid = puuid;

        this.requestStatus = RequestStatus.REQUEST;

        return this;
    }

    public Participant updateHostRole() {
        this.requestStatus = RequestStatus.NO_REQUEST;
        this.role = Role.HOST;

        return this;
    }

    public Participant dropoutParticipantStatus(){
        this.participantStatus = ParticipantStatus.DROPOUT;

        return this;
    }

    public void newCustomChannelIndex(Optional<Integer> index) {
        this.index = index.map(i -> i + 1).orElseGet(() -> 0);
    }

    public void updateCustomChannelIndex(Integer index) {
        this.index = index;
    }

    public void updateNickname(String newNickname) { this.nickname = newNickname; }

    public void deleteChannelAndMember() {
        this.channel = null;
        this.member = null;
    }
}
