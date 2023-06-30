package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.entity.member.Member;

public class UserFixture {

    public static Member createMember() {
        Member member = Member.builder()
                .personalId("id").profileImageUrl("url")
                .nickname("test").build();

        return member;
    }
}
