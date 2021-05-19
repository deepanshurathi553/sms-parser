package com.credochain.transactionanalysis.controller;

import com.credochain.transactionanalysis.dto.SMSRequest;
import com.credochain.transactionanalysis.dto.TransactionInfo;
import com.credochain.transactionanalysis.service.TransactionAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/phone")
@Validated
public class TransactionAnalysisRestController {

    @Autowired
    private TransactionAnalysisService service;

    @RequestMapping(value = "/{phoneNumber}/messages", method = RequestMethod.POST)
    public ResponseEntity<String> processMessages(@PathVariable("phoneNumber") Long phoneNumber,
                                                  @RequestBody List<@Valid SMSRequest> messages) {
        Integer messagesForProcessing = service.storeMessagesForProcessing(phoneNumber, messages);
        return new ResponseEntity<>("Analysing " + messagesForProcessing + " transactional messages. Please check for" +
                                            " status in some time", HttpStatus.OK);
    }

    @RequestMapping(value = "/{phoneNumber}/info", method = RequestMethod.GET)
    public ResponseEntity<TransactionInfo> getTransactionInfo(@PathVariable("phoneNumber") Long phoneNumber) {
        return new ResponseEntity<>(service.getTransactionInfo(phoneNumber), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleValidationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getLocalizedMessage());
        return errors;
    }

}
