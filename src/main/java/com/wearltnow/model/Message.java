package com.wearltnow.model;

import com.wearltnow.model.enums.Status;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {

    private String senderName;
    private String receiverName;
    private String message;
    private String date;
    private Status status;
}
