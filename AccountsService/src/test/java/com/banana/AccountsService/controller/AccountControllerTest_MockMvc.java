package com.banana.AccountsService.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql("classpath:data_tests.sql")
class AccountControllerTest_MockMvc {

    @Autowired
    MockMvc mvc;

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
                .andExpect(jsonPath("$.balance", equalTo(1000 + amount)));
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