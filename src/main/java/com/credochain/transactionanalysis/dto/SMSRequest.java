package com.credochain.transactionanalysis.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class SMSRequest {

    @NonNull
    private String sender;
    @NonNull
    @NotBlank(message = "Message is mandatory")
    private String message;
    @NonNull
    @NotNull(message = "Date is mandatory")
    private Long date;

}
