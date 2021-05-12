package com.credochain.transactionanalysis.service;

import java.util.Arrays;

public class InfoExtractionUtil {

    public static String getAccountNumber(String[] messageArray){
        for (int i = 0; i < messageArray.length; i++) {
            if(messageArray[i].equals("ac")){
                if(i+1 == messageArray.length) return null;
                String next = messageArray[i+1];
                if(next.matches(".*\\d.*")){
                    next = next.replaceAll("[.:?x]","");
                    if(next.length() >= 4){
                        return next.substring(next.length()-4);
                    }
                }
                return null;
            }
        }
        return null;
    }

    public static Double getBalance(String[] messageArray) {
        //I'm assuming every amount will have "rs." or "rs" or "inr" before it. Is that fair? It could ignore "your available balance is 67780.34".  It'd be easier to capture if the amount have at least inr, rs or rs. else it's as good as any number.
        //If i don't assume, "Balance on 14-01-2021 is Rs. 7890" would give me 14012021
        for (int i = 0; i < messageArray.length; i++) {
           if(messageArray[i].equals("balance")){
               if(i+1 == messageArray.length) return null;
               int j = i+1;
               while(j+1< messageArray.length){
                   String next = messageArray[j];
                   if(next.matches("inr")){
                       String amountString = messageArray[j+1].split("\\.")[0];
                       try {
                           return Double.valueOf(amountString.replaceAll(",", ""));
                       } catch (NumberFormatException e) {
                           return null;
                       }
                   }
                   else j++;
               }
               return null;
           }
        }
        return null;
    }

    public static String getTypeOfTransaction(String message) {
        if(message.matches(".*credit.*|.*deposit.*|.*recieved.*")) return "credit";
        else if (message.matches(".*debit.*|.*spent.*|.*deducted.*")) return "debit";
        else return null;
    }

    public static Double getTransactionAmount(String[] messageArray) {
        for (int i = 0; i < messageArray.length; i++) {
            if(messageArray[i].matches(".*credit.*|.*deposit.*|.*recieved.*")){
                if(i+1 < messageArray.length && messageArray[i + 1].equals("inr") && i+2 < messageArray.length){
                    String amountString = messageArray[i+2].split("\\.")[0];
                    try {
                        return Double.valueOf(amountString.replaceAll(",", ""));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else if (i-2 > -1 && messageArray[i-2].equals("inr")){
                    String amountString = messageArray[i-1].split("\\.")[0];
                    try {
                        return Double.valueOf(amountString.replaceAll(",", ""));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else return null;
            }

        }
        return null;
    }
}
