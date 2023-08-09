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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "custom_channel_index")
    private int index;

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
        this.requestStatus = RequestStatus.DONE;
        this.role = Role.PLAYER;

        return this;
    }


    public Participant rejectParticipantRequest(){
        this.requestStatus = RequestStatus.REJECT;
        this.role = Role.OBSERVER;

        return this;
    }


    public Participant updateParticipantStatus(String gameId, String gameTier, String nickname){
        this.gameId = gameId;
        this.gameTier = gameTier;
        this.nickname = nickname;

        this.requestStatus = RequestStatus.REQUEST;

        return this;
    }

    public Participant updateHostRole(){
        this.requestStatus = RequestStatus.NOREQUEST;
        this.role = Role.HOST;

        return this;
    }

    public void createCustomChannelIndex(Integer index) {
        if(index != null) this.index = index + 1;
        else this.index = 0;
    }
}
