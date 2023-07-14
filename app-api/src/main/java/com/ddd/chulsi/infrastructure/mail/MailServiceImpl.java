package com.ddd.chulsi.infrastructure.mail;

import com.ddd.chulsi.domainCore.model.users.Users;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final HttpServletRequest httpServletRequest;
    private final JavaMailSender javaMailSender;

    @Override
    public boolean sendMail(List<Users> receiveList) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            String imagesDirectory = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() +":" + httpServletRequest.getServerPort() + "/assets/images";

            for (Users users : receiveList) {
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
                           <div style="width: 120px;">
                               <img src="{images-directory}/sikdorok_logo.png" alt="sikdorok" style="width: 100%;">
                               <div style="color: #00CC8F; font-size: 16px; margin-top: 4px;"><b>비밀번호 재설정</b></div>
                           </div>
                           <div style="margin-top: 40px;">
                               <b>{nickname}</b>님 안녕하세요.<br />
                               아래 버튼을 선택하여 비밀번호를 재설정해주세요.
                           </div>
                           <div style="margin-top: 20px;">
                               <button style="width: 200px; height: 50px; border-radius: 4px; background-color: #00CC8F; border-color: #FFF; border-width: 0px; font-size: 16px; color: #FFF;" type="button">비밀번호 재설정하기</button> </div>
                           <div style="margin-top: 20px;">
                               <span style="color: #808080;">(비밀번호 재설정 링크는 1시간 동안 유효합니다.)</span>
                           </div>
                           <div style="margin-top: 40px;">
                               <img src="{images-directory}/strap.png" alt="strap">
                           </div>
                           <div style="margin-top: 20px;">
                               <span style="color: #808080;">문의사항은 team.sikdorok@gmail.com으로 연락 주시기 바랍니다.</span><br />
                               <span style="color: #808080;">&copy; sikdorok All rights reserved.</span>
                           </div>
                       </div>
                   </body>
                   
                   </html>
                    """;
                content = content.replace("{images-directory}", imagesDirectory);
                content = content.replace("{nickname}", users.getName());
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
