package com.demo.controller;

import com.demo.api.CustomersApi;
import com.demo.model.Customer;
import java.util.Optional;
import org.springframework.http.ResponseEntity;

public class CustomerController implements CustomersApi {

    @Override
    public ResponseEntity<Void> createCustomer(Customer customer) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Customer> getCustomerById(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Customer> getCustomersList(Optional<Integer> limit, Optional<Integer> offset) {
        return null;
    }

    @Override
    public ResponseEntity<Customer> updateCustomer(String id) {
        return null;
    }
}
