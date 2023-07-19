package leaguehub.leaguehubbackend.fixture;

import leaguehub.leaguehubbackend.dto.channel.CreateChannelDto;
import leaguehub.leaguehubbackend.dto.member.LoginMemberResponse;
import leaguehub.leaguehubbackend.entity.member.BaseRole;
import leaguehub.leaguehubbackend.entity.member.LoginProvider;
import leaguehub.leaguehubbackend.entity.member.Member;

public class UserFixture {

    public static Member createMember() {
        Member member = Member.builder()
                .personalId("id").profileImageUrl("url")
                .nickname("test").refreshToken("refreshToken")
                .loginProvider(LoginProvider.KAKAO).baseRole(BaseRole.USER)
                .build();

        return member;
    }

    public static LoginMemberResponse createLoginResponse() {
        LoginMemberResponse loginMemberResponse = LoginMemberResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
        return loginMemberResponse;

    }
}
