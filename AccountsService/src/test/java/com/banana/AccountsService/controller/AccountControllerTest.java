package com.banana.AccountsService.controller;

import com.banana.AccountsService.assembler.AccountModelAssembler;
import com.banana.AccountsService.exception.AccountNotfoundException;
import com.banana.AccountsService.model.Account;
import com.banana.AccountsService.persistence.AccountRepository;
import com.banana.AccountsService.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

@ExtendWith(SpringExtension.class)
@Import(AccountController.class)
class AccountControllerTest {

    @Autowired
    AccountController accountController;

    @MockBean
    AccountService accountService;

    @MockBean
    private AccountModelAssembler accountModelAssembler;

    @MockBean
    AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        Account account = new Account(1L, "Cuenta corriente", new Date(), 500, 1L, null);
        Mockito.when(accountService.addBalance(1L, 400, 1L))
                .thenReturn(account);
        Mockito.when(accountService.addBalance(-1L, 400, 1L))
                .thenThrow(new AccountNotfoundException(-1L));
    }

    @Test
    void givenAmountWhenValidThenBalanceAdded() {
        //given
        int amount = 400;
        //when
        ResponseEntity<Account> response = accountController.addBalance(1L, 1L, amount);
        //then
        assertThat(Objects.requireNonNull(response.getBody()).getBalance()).isEqualTo(500);
    }

    @Test
    void givenAmountWhenNotValidThenException() {
        //given
        int amount = 400;
        //when - then
        assertThrows(AccountNotfoundException.class, () -> {
           accountController.addBalance(-1L, 1L, amount);
        });
    }

    @Test
    void givenOwnerIdWhenValidThenDeleteAccounts() {
        //given
        Long ownerId = 1L;
        //when
        ResponseEntity<Void> response = accountController.deleteAccountsByOwnerId(ownerId);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void givenOwnerIdWhenDeleteAccountsThenException() {
        doThrow(new RuntimeException())
                .when(accountService).deleteAccountsUsingOwnerId(-1L);
        //given
        Long ownerId = -1L;
        //when - then
        assertThrows(RuntimeException.class, () -> {
           accountController.deleteAccountsByOwnerId(ownerId);
        });
    }
}