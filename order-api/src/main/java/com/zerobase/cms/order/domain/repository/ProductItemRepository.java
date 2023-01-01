package com.zerobase.cms.order.domain.repository;

import com.zerobase.cms.order.domain.model.ProductItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
	ProductItem getById(Long id);
}
