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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
class AccountControllerTest_WebMvcTest {

    @Autowired
    private MockMvc mvc;

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
    void givenAmountWhenAddBalanceThenStatus202() throws Exception {
        // given
        int amount = 400;
        long id = 1L;
        long ownerId = 1L;

        // when - then
        mvc.perform(put("/accounts/" + id + "/owner/" + ownerId + "/addBalance?amount=" + amount).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.balance", equalTo(500)));
    }

    @Test
    void givenNotValidIdWhenAddBalanceThenStatus412() throws Exception {
        // given
        int amount = 400;
        long id = -1L;
        long ownerId = 1L;

        // when - then
        mvc.perform(put("/accounts/" + id + "/owner/" + ownerId + "/addBalance?amount=" + amount).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    void givenValidOwnerIdWhenDeleteAccountsThenStatus204() throws Exception {
        // given
        long ownerId = 2L;

        // when - then
        mvc.perform(delete("/accounts/owner/" + ownerId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNotValidOwnerIdWhenDeleteAccountsThenStatus412() throws Exception {
        // given
        long ownerId = -1L;

        // when - then
        mvc.perform(delete("/accounts/owner/" + ownerId).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isPreconditionFailed());
    }
}