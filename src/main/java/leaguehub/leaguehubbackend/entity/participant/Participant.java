package leaguehub.leaguehubbackend.entity.participant;

import jakarta.persistence.*;
import leaguehub.leaguehubbackend.entity.BaseTimeEntity;
import leaguehub.leaguehubbackend.entity.channel.Channel;
import leaguehub.leaguehubbackend.entity.constant.GlobalConstant;
import leaguehub.leaguehubbackend.entity.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public static Participant createHostChannel(Member member, Channel channel) {
        Participant participant = new Participant();
        participant.nickname = member.getNickname();
        participant.profileImageUrl = member.getProfileImageUrl();
        participant.role = Role.HOST;
        participant.member = member;
        participant.channel = channel;

        participant.requestStatus = RequestStatus.NOREQUEST;

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

        participant.requestStatus = RequestStatus.NOREQUEST;

        participant.gameId = GlobalConstant.NO_DATA.getData();
        participant.gameTier = GlobalConstant.NO_DATA.getData();

        return participant;
    }


    public Participant approveParticipantMatch(){
        this.role = Role.PLAYER;
        this.requestStatus = RequestStatus.DONE;

        return this;
    }


    public Participant rejectParticipantRequest(){
        this.requestStatus = RequestStatus.REJECT;

        return this;
    }


    public Participant updateParticipantStatus(String gameId, String gameTier){
        this.gameId = gameId;
        this.gameTier = gameTier;
        this.requestStatus = RequestStatus.REQUEST;

        return this;
    }
}
