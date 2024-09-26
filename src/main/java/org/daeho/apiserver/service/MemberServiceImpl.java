package org.daeho.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.daeho.apiserver.domain.Member;
import org.daeho.apiserver.domain.MemberRole;
import org.daeho.apiserver.dto.MemberDTO;
import org.daeho.apiserver.repository.MemberRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDTO getKakaoMember(String accessToken) {
        //accesssToken 이용해서 사용자 정보 가져오기

        //카카오 연동 닉네임 -- 이메일 주소에 해당
        String nickname = getEmailFromKakaoAccessToken(accessToken);

        //기존에 db에 회원정보가 있는 경우 / 없는 경우

        Optional<Member> result = memberRepository.findById(nickname);

        if(result.isPresent()){
            MemberDTO memberDTO = entityToDTO(result.get());

            log.info("existed........................" + memberDTO);

            return memberDTO;
        }

        Member socialMember = makeMember(nickname);

        memberRepository.save(socialMember);
        MemberDTO memberDTO = entityToDTO(socialMember);

        return memberDTO;
    }


    private Member makeMember(String email){
        String tempPassword = makeTempPassword();
        log.info("tempPassword : " + tempPassword);

        Member member = Member.builder()
                .email(email)
                .pw(passwordEncoder.encode(tempPassword))
                .nickname("Social Member")
                .social(true)
                .build();

        member.addRole(MemberRole.USER);
        return member;
    }

    private String getEmailFromKakaoAccessToken(String accessToken){

        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        if(accessToken == null){
            throw new RuntimeException("Access Token is null");
        }
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type","application/x-www-form-urlencoded");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(
                        uriBuilder.toString(),
                        HttpMethod.GET,
                        entity,
                        LinkedHashMap.class);

        log.info(response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        log.info("------------------------------------");
        log.info(bodyMap);

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("kakao_account");

        log.info("kakaoAccount: " + kakaoAccount);

        Object profileObj = kakaoAccount.get("profile");
        LinkedHashMap<String, Object> profile = (LinkedHashMap<String, Object>) profileObj;
        String nickname = (String) profile.get("nickname");
        log.info("nickname: " + nickname);

        return nickname;

    }

    private String makeTempPassword(){
        StringBuffer buffer = new StringBuffer();

        for(int i=0; i<10; i++){
            buffer.append( (char) ( (int)(Math.random()*55) + 65));
        }
        return buffer.toString();
    }

}
