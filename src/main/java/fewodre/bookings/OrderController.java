package fewodre.bookings;

import org.aspectj.weaver.ast.Or;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManagement;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.OneToOne;

@RestController
public class OrderController {

	private final OrderManagement<Order> orderManagement;

	OrderController(OrderManagement<Order> orderManagement) {
		Assert.notNull(orderManagement, "OrderManagement must not be null!");
		this.orderManagement = orderManagement;

	}

	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

}
