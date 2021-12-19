package com.shubhankarsudip.a1tapdocs;

public class uploadCARDS {

   String CardSaverName, CardNumber, BankName, ExpDate , CVVno, uniqueKey ;

    public uploadCARDS() {
    }

    public uploadCARDS(String cardSaverName, String cardNumber, String bankName, String expDate, String CVVno, String uniqueKey) {
        CardSaverName = cardSaverName;
        CardNumber = cardNumber;
        BankName = bankName;
        ExpDate = expDate;
        this.CVVno = CVVno;
        this.uniqueKey = uniqueKey;
    }



    public String getCardSaverName() {
        return CardSaverName;
    }

    public void setCardSaverName(String cardSaverName) {
        CardSaverName = cardSaverName;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getExpDate() {
        return ExpDate;
    }

    public void setExpDate(String expDate) {
        ExpDate = expDate;
    }

    public String getCVVno() {
        return CVVno;
    }

    public void setCVVno(String CVVno) {
        this.CVVno = CVVno;
    }



    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
