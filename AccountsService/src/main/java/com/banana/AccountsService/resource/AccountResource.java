package com.banana.AccountsService.resource;

import com.banana.AccountsService.constraints.OpeningDate;
import com.banana.AccountsService.model.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@Schema(name = "Modelo de cuenta", description = "Representa una cuenta bancaria.")
public class AccountResource extends RepresentationModel<AccountResource> {

    @Min(1)
    @Schema(name = "Identificador de la cuenta", description = "Indentificador de la cuenta basada en números entero positivos", example = "432")
    private Long id;

    @NotBlank
    @Size(min = 3, max = 10)
    @Schema(name = "El tipo de cuenta bancaria", description = "Categoriza la cuenta bancaria", example = "Cuenta corriente")
    private String type;

    @DateTimeFormat
    @OpeningDate
    @Schema(name = "Fecha de apertura", description = "Indica la fecha en la que la cuenta se hace efectiva", example = "2024-02-14T08:30:00")
    Date openingDate;

    @NotNull
    @Schema(name = "Los fondos de la cuenta", description = "Indica el importe de la cuenta bancaria", example = "17500")
    private int balance;

    @Min(1)
    @NotNull
    @Schema(name = "Identificador del propietario", description = "Indentificador del propietario asociado a la cuenta", example = "15631")
    private Long ownerId;

    @Transient
    Customer owner;

}
