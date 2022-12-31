package com.zerobase.cms.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.product.UpdateProductForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.order.domain.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceTest {

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductRepository productRepository;

	@Test
	void ADD_PRODUCT_TEST() {
		Long sellerId = 1L;

		AddProductForm form = makeProductForm("나이키 에어포스", "신발", 3);

		Product p = productService.addProduct(sellerId, form);

		Product result = productRepository.findById(p.getId()).get();

		assertNotNull(result);
		assertEquals(p.getName(),"나이키 에어포스");
		assertEquals(p.getSellerId(),1L);
		assertEquals(p.getDescription(),"신발");
		assertEquals(p.getProductItems().size(),3);
		assertEquals(p.getProductItems().get(0).getName(),"나이키 에어포스0");
		assertEquals(p.getProductItems().get(1).getPrice(),10000);
		assertEquals(p.getProductItems().get(2).getCount(),1);
	}

	@Test
	void UPDATE_PRODUCT_TEST() {
		Long sellerId = 1L;

		AddProductForm form = makeProductForm("나이키 에어포스", "신발", 3);

		UpdateProductForm updateProductForm = makeUpdateProductForm("나이끼","스리퍼",3);

		Product p = productService.addProduct(sellerId, form);
		updateProductForm.setId(p.getId());
		Product result = productService.updateProduct(sellerId, updateProductForm);

		assertNotNull(result);
		assertEquals(p.getName(),"나이키 에어포스");
		assertEquals(p.getSellerId(),1L);
		assertEquals(p.getDescription(),"신발");
		assertEquals(p.getProductItems().size(),3);
		assertEquals(p.getProductItems().get(0).getName(),"나이키 에어포스0");
		assertEquals(p.getProductItems().get(1).getPrice(),10000);
		assertEquals(p.getProductItems().get(2).getCount(),1);
		assertEquals(result.getDescription(), "스리퍼");
		assertEquals(result.getName(), "나이끼");
		assertEquals(result.getProductItems().get(0).getName(), "나이끼01");
		assertEquals(result.getProductItems().get(1).getPrice(), 130000);
		assertEquals(result.getProductItems().get(2).getCount(), 2);
	}

	private static AddProductForm makeProductForm(String name, String description, int itemCount) {
		List<AddProductItemForm> itemForms = new ArrayList<>();
		for (int i=0; i<itemCount; i++) {
			itemForms.add(makeProductItemForm(null,name+i));
		}
		return AddProductForm.builder()
			.name(name)
			.description(description)
			.items(itemForms)
			.build();
	}

	private static final AddProductItemForm makeProductItemForm(Long productId, String name) {
		return AddProductItemForm.builder()
			.productId(productId)
			.name(name)
			.price(10000)
			.count(1)
			.build();
	}

	private static UpdateProductForm makeUpdateProductForm(String name, String description, int itemCount) {
		List<UpdateProductItemForm> itemForms = new ArrayList<>();
		for (int i=0; i<itemCount; i++) {
			itemForms.add(makeUpdateProductItemForm(i+1L,name+i+1));
		}
		return UpdateProductForm.builder()
			.name(name)
			.description(description)
			.items(itemForms)
			.build();
	}

	private static UpdateProductItemForm makeUpdateProductItemForm(Long id,String name) {
		return UpdateProductItemForm.builder()
			.id(id)
			.name(name)
			.price(130000)
			.count(2)
			.build();
	}
}