package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendMailForm;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.service.SignUpCustomerService;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpApplication {

	private final MailgunClient mailgunClient;
	private final SignUpCustomerService signUpCustomerService;

	public void customerVerify(String email, String code) {
		signUpCustomerService.verifyEmail(email,code);
	}

	public String customerSignUp(SignUpForm form) {
		if (signUpCustomerService.isEmailExist(form.getEmail())) {
			throw new CustomException((ErrorCode.ALREADY_REGISTER_USER));
		} else {
			Customer c = signUpCustomerService.signUp(form);
			LocalDateTime now = LocalDateTime.now();

			String code = getRandom();

			SendMailForm mailForm = SendMailForm.builder()
				.from("dksekfbs@gmail.com")
				.to(form.getEmail())
				.subject("이메일 인증 메일입니다!")
				.text(getVerificationEmailBody(c.getEmail(), c.getName(), code))
				.build();
			mailgunClient.sendEmail(mailForm);
			signUpCustomerService.changeCustomerValidateEmail(c.getId(),code);
			return "회원 가입에 성공하였습니다.";
		}
	}

	private String getRandom() {
		return RandomStringUtils.random(10, true, true);
	}

	private String getVerificationEmailBody(String email, String name, String code) {
		StringBuilder builder = new StringBuilder();
		return builder.append("안녕하세요. ").append(name).append("! 링크를 눌러주시면 인증이 완료됩니다.\n\n")
			.append("http://localhost:8080/custoner/verify/customer?email=")
			.append(email)
			.append("&code=")
			.append(code).toString();
	}
}
