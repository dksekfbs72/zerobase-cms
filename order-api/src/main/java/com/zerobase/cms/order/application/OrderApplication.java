package com.zerobase.cms.order.application;

import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

import com.zerobase.cms.order.client.MailgunClient;
import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.mailgun.SendMailForm;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import com.zerobase.cms.order.service.ProductItemService;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.domain.common.UserVo;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderApplication {

	private final CartApplication cartApplication;
	private final UserClient userClient;
	private final ProductItemService productItemService;
	private final MailgunClient mailgunClient;

	@Transactional
	public void order(String token, Cart cart) {
		Cart orderCart = cartApplication.refreshCart(cart);

		if (orderCart.getMessages().size() > 0) {
			throw new CustomException(ORDER_FAIL_CHECK_CART);
		}
		CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();

		int totalPrice = getTotalPrice(cart);
		if (customerDto.getBalance() < totalPrice) {
			throw new CustomException(ORDER_FAIL_NO_MONEY);
		}

		userClient.changeBalance(token,
				ChangeBalanceForm.builder()
				.from("USER")
				.message("Order")
				.money(customerDto.getBalance()-totalPrice)
			.build());

		for (Cart.Product product : orderCart.getProducts()) {
			for (Cart.ProductItem cartItem : product.getItems()) {
				ProductItem productItem = productItemService.getProductItem(cartItem.getId());
				productItem.setCount(productItem.getCount()-cartItem.getCount());
			}
		}

		SendMailForm mailForm = SendMailForm.builder()
			.from("dksekfbs@gmail.com")
			.to(customerDto.getEmail())
			.subject("[zerobase_cms] 주문내역입니다")
			.text(getVerificationEmailBody(orderCart))
			.build();
		mailgunClient.sendEmail(mailForm);
	}


	public Integer getTotalPrice(Cart cart) {
		return cart.getProducts().stream().flatMapToInt(
				product -> product.getItems().stream().flatMapToInt(
					productItem -> IntStream.of(productItem.getPrice() * productItem.getCount())))
			.sum();
	}

	private String getVerificationEmailBody(Cart cart) {
		StringBuilder builder = new StringBuilder();
		builder.append("주문해주셔서 감사합니다. 회원님의 주문 내역을 보내드립니다. \r\n");

		for (Cart.Product product : cart.getProducts()) {
			builder.append("\r\n\r\n상품명 : ").append(product.getName());
			for (Cart.ProductItem cartItem : product.getItems()) {
				builder.append("\r\n옵션 : ")
					.append(cartItem.getName())
					.append(" 가격 : ")
					.append(cartItem.getPrice()).append(" 원")
					.append(" 수량 : ")
					.append(cartItem.getCount()).append(" 개");
			}
		}
		return builder.toString();
	}
}

