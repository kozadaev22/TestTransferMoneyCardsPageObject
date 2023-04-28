package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPageV2;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

public class MoneyTransferTest {
    LoginPageV2 loginPage;

    @BeforeEach
    void setup() {
        loginPage = open("http://localhost:9999", LoginPageV2.class);
    }

    @Test
    void shouldTransferFromFirstToSecond() {
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    void shouldGetErrorMessageIfAmountMoreBalance(){
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCode();
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var firstCardInfo = getFirstCardInfo();
        var secondCardInfo = getSecondCardInfo();
        var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
        var amount = generateInvalidAmount(secondCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), firstCardInfo);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(firstCardBalance, actualBalanceFirstCard);
        assertEquals(secondCardBalance, actualBalanceSecondCard);
    }
}
