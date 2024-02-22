package com.banana.AccountsService.controller;

import com.banana.AccountsService.assembler.AccountModelAssembler;
import com.banana.AccountsService.model.Account;
import com.banana.AccountsService.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@Validated
@Tag(name = "API de cuentas", description = "Endpoints para consumir cuentas")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountModelAssembler accountModelAssembler;

    @PostMapping( consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Para crear una nueva cuenta", description = "Devuelve la cuenta creada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuando la cuenta se crea correctamente."),
            @ApiResponse(responseCode = "412", description = "Cuando hay un error en una precondición.")
    })
    public ResponseEntity<?> createAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la cuenta a crear", required = true,
                    content = @Content(schema = @Schema(implementation = Account.class)))
            @RequestBody @Valid Account account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountModelAssembler.toModel(accountService.create(account)));
    }

    @GetMapping
    @Operation(summary = "Para pedir todos las cuentas", description = "Devuelve todas las cuentas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuando hay cuentas a devolver."),
            @ApiResponse(responseCode = "404", description = "Cuando no hay cuentas a devolver.")
    })
    public ResponseEntity<?> getAccounts() {
        return ResponseEntity.status(HttpStatus.OK).body(accountModelAssembler.toCollectionModel(accountService.getAccounts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable @Min(1) Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccount(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Account>> getAccountsByOwnerId(@PathVariable @Min(1) Long ownerId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.getAccountByOwnerId(ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable @Min(1) Long id, @RequestBody @Valid Account account) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(accountService.updateAccount(id, account));
    }

    @PutMapping("/{id}/owner/{ownerId}/addBalance")
    public ResponseEntity<Account> addBalance(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long ownerId, @RequestParam(required = true) @Min(1) int amount ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(accountService.addBalance(id, amount, ownerId));
    }

    @PutMapping("/{id}/owner/{ownerId}/withdrawBalance")
    public ResponseEntity<Account> withdrawBalance(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long ownerId, @RequestParam(required = true) @Min(1) int amount) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(accountService.withdrawBalance(id, amount, ownerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable @Min(1) Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/owner/{ownerId}")
    public ResponseEntity<Void> deleteAccountsByOwnerId(@PathVariable @Min(1) Long ownerId) {
        accountService.deleteAccountsUsingOwnerId(ownerId);
        return ResponseEntity.noContent().build();
    }
}
