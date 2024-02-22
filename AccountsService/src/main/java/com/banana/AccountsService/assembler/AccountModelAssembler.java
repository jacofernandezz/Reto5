package com.banana.AccountsService.assembler;

import com.banana.AccountsService.controller.AccountController;
import com.banana.AccountsService.model.Account;
import com.banana.AccountsService.resource.AccountResource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountModelAssembler extends RepresentationModelAssemblerSupport<Account, AccountResource> {
    public AccountModelAssembler() {
        super(AccountController.class, AccountResource.class);
    }

    @Override
    public AccountResource toModel(Account entity) {
        AccountResource accountModel = instantiateModel(entity);

        accountModel = AccountResource.builder().
                id(entity.getId()).
                type(entity.getType()).
                openingDate(entity.getOpeningDate()).
                balance(entity.getBalance()).
                ownerId(entity.getOwnerId()).
                build();

        accountModel.add(linkTo(
                methodOn(AccountController.class)
                        .getAccount(entity.getId()))
                .withSelfRel());

        accountModel.add(linkTo(
                methodOn(AccountController.class)
                        .getAccounts())
                .withRel("products"));

        return accountModel;
    }

    public Collection<AccountResource> toModel(Collection<Account> accounts) {
        if (accounts.isEmpty()) return Collections.emptyList();

        return accounts.stream()
                .map(account -> AccountResource.builder().
                                id(account.getId()).
                                type(account.getType()).
                                openingDate(account.getOpeningDate()).
                                balance(account.getBalance()).
                                ownerId(account.getOwnerId()).
                                build()
                        .add(linkTo(
                                methodOn(AccountController.class)
                                        .getAccount(account.getId()))
                                .withSelfRel()))
                .collect(Collectors.toList());
    }

    @Override
    public CollectionModel<AccountResource> toCollectionModel(Iterable<? extends Account> entities) {
        CollectionModel<AccountResource> accountModels = super.toCollectionModel(entities);

        accountModels.add(linkTo(methodOn(AccountController.class).getAccounts()).withSelfRel());

        return accountModels;
    }

}

