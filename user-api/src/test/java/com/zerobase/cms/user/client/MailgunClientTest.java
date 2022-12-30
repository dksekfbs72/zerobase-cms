package com.zerobase.cms.user.client;

import com.zerobase.cms.user.client.mailgun.SendMailForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MailgunClientTest {

	@Autowired
	private MailgunClient mailgunClient;

	@Test
	void sendMail() {
		mailgunClient.sendEmail(SendMailForm.builder()
			.to("dksekfbs72@naver.com")
			.from("dksekfbs@gmail.com")
			.text("테스트입니다.")
			.subject("메일 테스트")
			.build());
	}
}