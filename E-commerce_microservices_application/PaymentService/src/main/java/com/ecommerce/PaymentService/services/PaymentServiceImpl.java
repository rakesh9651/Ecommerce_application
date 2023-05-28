package com.ecommerce.PaymentService.services;

import com.ecommerce.PaymentService.entity.TransactionDetails;
import com.ecommerce.PaymentService.model.PaymentMode;
import com.ecommerce.PaymentService.model.PaymentRequest;
import com.ecommerce.PaymentService.model.PaymentResponse;
import com.ecommerce.PaymentService.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;
    @Override
    public long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment details {} ", paymentRequest);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .orderId(paymentRequest.getOrderId())
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .paymentStatus("SUCCESS")
                .amount(paymentRequest.getAmount())
                .build();
        this.transactionDetailsRepository.save(transactionDetails);
        log.info("Transaction Successful with id {}", transactionDetails.getId());
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(String orderId) {

        log.info("Fetching the transaction details by order id  {}",orderId);
        TransactionDetails transactionDetails =
                transactionDetailsRepository.findByOrderId(Long.valueOf(orderId));
        PaymentResponse paymentResponse = PaymentResponse
                .builder()
                .paymentId(transactionDetails.getId())
                .orderId(transactionDetails.getOrderId())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentDate(transactionDetails.getPaymentDate())
                .amount(transactionDetails.getAmount())
                .status(transactionDetails.getPaymentStatus())
                .build();
        log.info("fetched the transaction details by order id {}",orderId);
        return paymentResponse;
    }
}
