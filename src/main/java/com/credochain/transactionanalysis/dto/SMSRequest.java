package com.credochain.transactionanalysis.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class SMSRequest {

    @NonNull
    private String sender;
    @NonNull
    private String message;
    @NonNull
    private Long date;

}
