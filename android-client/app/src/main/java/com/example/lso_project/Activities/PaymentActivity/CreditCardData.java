package com.example.lso_project.Activities.PaymentActivity;

public class CreditCardData {

    private String cardID;
    private String cardNumber;
    private String cardDate;
    private String cardCVC;

    public  CreditCardData()
    {
    }


    public CreditCardData(String cardID, String cardNumber, String cardDate, String cardCVC)
    {
        this.cardID = cardID;
        this.cardNumber = cardNumber;
        this.cardDate = cardDate;
        this.cardCVC = cardCVC;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardDate() {
        return cardDate;
    }

    public void setCardDate(String cardDate) {
        this.cardDate = cardDate;
    }

    public String getCardCVC() {
        return cardCVC;
    }

    public void setCardCVC(String cardCVC) {
        this.cardCVC = cardCVC;
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    @Override
    public String toString() {
        return "CreditCardData{" +
                "cardID='" + cardID + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardDate='" + cardDate + '\'' +
                ", cardCVC='" + cardCVC + '\'' +
                '}';
    }
}
