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
                    <html lang="en">
                    <head>
                    	<meta charset="UTF-8">
                    	<meta name="viewport" content="width=device-width, initial-scale=1.0">
                    	<title>Document</title>
                    	<style>
                    		.mc {
                    			color: #00CC8F;
                    		}
                    		.grey {
                    			color: #808080;
                    		}
                    		.fs14 {
                    			font-size: 14px;
                    		}
                    		.reset-btn {
                    			width: 200px;
                    			height: 50px;
                    			border-radius: 4px;
                    			background-color: #00CC8F;
                    			border-color: #FFF;
                    			border-width: 0px;
                    			font-size: 16px;
                    			color: #FFF;
                    		}
                    		.mt4 {
                    			margin-top: 4px;
                    		}
                    		.mt14 {
                    			margin-top: 14px;
                    		}
                    		.mt20 {
                    			margin-top: 20px;
                    		}
                    		.mt40 {
                    			margin-top: 40px;
                    		}
                    		.container {
                    			width: 640px;
                    			height: 360px;
                    		}
                    		.header {
                    			width: 120px;
                    		}
                    		.header > img {
                    			width: 100%;
                    		}
                    		.taco > img {
                    			position: fixed;
                    			left: 480px;
                    			top: 180px;
                    		}
                    	</style>
                    </head>
                    <body>
                    	<div class="container">
                    		<div class="header">
                    			<img src="{images-directory}/sikdorok_logo.png" alt="sikdorok">
                    			<div class="mc fs14 mt4">비밀번호 재설정</div>
                    		</div>
                    		<div class="mt20">
                    			<b>{nickname}</b>님 안녕하세요.<br/>
                    			아래 버튼을 선택하여 비밀번호를 재설정해주세요.
                    		</div>
                    		<div class="mt14">
                    			<button class="reset-btn" type="button">비밀번호 재설정하기</button>
                    		</div>
                    		<div class="mt14">
                    			<span class="grey">(비밀번호 재설정 링크는 1시간 동안 유효합니다.)</span>
                    		</div>
                    		<div class="taco">
                    			<img src="{images-directory}/taco.png" alt="taco">
                    		</div>
                    		<div class="mt40">
                    			<img src="{images-directory}/strap.png" alt="strap">
                    		</div>
                    		<div class="mt14">
                    			<span class="grey">문의사항은 team.sikdorok@gmail.com으로 연락 주시기 바랍니다.</span><br />
                    			<span class="grey">&copy; sikdorok All rights reserved.</span>
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
