package com.ddd.chulsi.infrastructure.mail;

import com.ddd.chulsi.domainCore.model.users.Users;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender;

    @Override
    public boolean sendMail(List<Users> receiveList, String redisCode) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            String passwordResetUrl = "https://sikdorok.page.link/?link={link}&apn=com.ddd.sikdorok";
            String link = "https://www.sikdorok.com/reset?userid={usersId}&code={code}";

            for (Users users : receiveList) {
                // 0. 유저 정보 치환
                link = link.replace("{usersId}", users.getUsersId().toString()).replace("{code}", redisCode);

                // URL Encode
                link = URLEncoder.encode(link, StandardCharsets.UTF_8);

                passwordResetUrl = passwordResetUrl.replace("{link}", link);

                // 1. 메일 수신자 설정
                messageHelper.setTo(users.getEmail());

                // 2. 메일 제목 설정
                messageHelper.setSubject("[식도록] 비밀번호를 재설정 해주세요.");

                // 3. 메일 내용 설정
                // HTML 적용됨
                String content = """
                    <!DOCTYPE html>
                   <html lang="ko">
                   
                   <head>
                      <meta charset="UTF-8">
                      <meta name="viewport" content="width=device-width, initial-scale=1.0">
                      <title>식도록</title>
                   </head>
                   
                   <body>
                      <div style="width: 640px; height: 460px;">
                          <div style="width: 240px;">
                              <div style="color: #3C3025; font-size: 28px; padding: 16px 0px;"><b>비밀번호 재설정</b></div>
                          </div>
                          <div style="margin-top: 40px;">
                              <b>{nickname}</b>님 안녕하세요.<br />
                              아래 버튼을 선택하여 비밀번호를 재설정해주세요.
                          </div>
                          <a href="{password-reset-url}" target="_blank" style="text-decoration: unset; display: inline-block;">
                              <div style="margin-top: 20px; width: 200px; height: 50px; border-radius: 4px; background-color: #3C3025; border-color: #FFF; border-width: 0px; font-size: 16px; color: #FFF; display: block; text-align: center; line-height: 50px;">비밀번호 재설정하기</div>
                          </a>
                          <div style="margin-top: 40px;">
                               <span style="color: #808080;">(비밀번호 재설정 링크는 1시간 동안 유효합니다.)</span><br />
                              <span style="color: #808080;">문의사항은 <a href="mailto:team.sikdorok@gmail.com" target="_blank">team.sikdorok@gmail.com</a>으로 연락 주시기 바랍니다.</span>
                          </div>
                      </div>
                   </body>
                   
                   </html>
                    """;
                content = content.replace("{nickname}", users.getName());
                content = content.replace("{password-reset-url}", passwordResetUrl);
                messageHelper.setText(content,true);

                // 4. 메일 전송
                javaMailSender.send(message);
            }

            return true;
        } catch (Exception e)  {
            return false;
        }
    }

}
