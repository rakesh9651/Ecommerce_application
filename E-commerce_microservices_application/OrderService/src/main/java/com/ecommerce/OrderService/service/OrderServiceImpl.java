package com.ecommerce.OrderService.service;

import com.ecommerce.OrderService.entity.Order;
import com.ecommerce.OrderService.exception.CustomException;
import com.ecommerce.OrderService.external.client.PaymentService;
import com.ecommerce.OrderService.external.client.ProductService;
import com.ecommerce.OrderService.external.request.PaymentRequest;
import com.ecommerce.OrderService.external.response.PaymentResponse;
import com.ecommerce.OrderService.model.OrderRequest;
import com.ecommerce.OrderService.model.OrderResponse;
import com.ecommerce.OrderService.model.OrderResponse.ProductDetails;
import com.ecommerce.OrderService.repository.OrderRepository;
import com.ecommerce.productService.entity.Product;
import com.ecommerce.productService.model.ProductResponse;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;
    @Override
    public long placeOrder(OrderRequest orderRequest) {

        //Order entity -> Save the data with status order created
        //Product service - Block products (Reduce the quantity)
        //Payment service -> Payments -> success -> Complete Else canceled

        log.info("placing order request {}",orderRequest);

        //reducing product/blocking product quantity
        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating order with Status CREATED");
        Order order = Order.builder()
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .orderDate(Instant.now())
                .orderStatus("CREATED")
                .amount(orderRequest.getTotalAmount())
                .build();
        this.orderRepository.save(order);

        log.info("calling payment service to complete the payment ");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;
        try{
            paymentService.doPayment(paymentRequest);
            log.info("payment done successfully. changing the order status to PLACED ");
            orderStatus = "PLACED";
        }catch (Exception e){
            log.error("Error occurred in payment. changing the order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order placed successfully with order id : {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {

        log.info("Get order details for orderId {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new CustomException
                        ("Order not found for order id"+orderId, "NOT_FOUND", 404));

        log.info("Invoking product service to fetch the product data for product id {}",order.getProductId());
        ProductResponse productResponse =
                restTemplate.getForObject("http://PRODUCT-SERVICE/product/"+order.getProductId(),
                        ProductResponse.class);

        log.info("Getting payment information from the payment service ");
        PaymentResponse paymentResponse =
                restTemplate.getForObject(
                        "http://PAYMENT-SERVICE/payment/order/"+order.getId(),PaymentResponse.class);
        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails
                .builder()
                .paymentId(paymentResponse.getPaymentId())
                .status(paymentResponse.getStatus())
                .paymentMode(paymentResponse.getPaymentMode())
                .paymentDate(paymentResponse.getPaymentDate())
                .build();

        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails
                .builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getId())
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
