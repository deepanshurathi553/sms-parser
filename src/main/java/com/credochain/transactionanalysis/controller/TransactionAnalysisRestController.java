package com.credochain.transactionanalysis.controller;

import com.credochain.transactionanalysis.dto.SMSRequest;
import com.credochain.transactionanalysis.dto.TransactionInfo;
import com.credochain.transactionanalysis.service.TransactionAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/phone")
public class TransactionAnalysisRestController {

    @Autowired
    private TransactionAnalysisService service;

    @RequestMapping(value = "/{phoneNumber}/messages", method = RequestMethod.POST)
    public ResponseEntity<TransactionInfo> getTransactionInfo(@PathVariable("phoneNumber") Long phoneNumber,
                                                              @RequestBody List<SMSRequest> messages) {
        TransactionInfo transactionInfo = service.getTransactionInfo(phoneNumber, messages);
        return new ResponseEntity<>(transactionInfo, HttpStatus.OK);
    }


}
