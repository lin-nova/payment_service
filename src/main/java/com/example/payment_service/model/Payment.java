package com.example.payment_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedEntityGraph(
        name = "Payment.contract.client",
        attributeNodes = {
                @NamedAttributeNode("amount"),
                @NamedAttributeNode("type"),
                @NamedAttributeNode("date"),
                @NamedAttributeNode("client")
        }
//        ,
//        subgraphs = {
//                @NamedSubgraph(
//                        name = "contractWithClient",
//                        attributeNodes = {
//                                @NamedAttributeNode("contractNumber"),
//                                @NamedAttributeNode(value = "client", subgraph = "clientWithContracts"),
//                                @NamedAttributeNode(value = "payments")
//                        }
//                ),
//                @NamedSubgraph(
//                        name = "clientWithContracts",
//                        attributeNodes = {
//                                @NamedAttributeNode("username"),
//                                @NamedAttributeNode(value = "contracts"),
//                                @NamedAttributeNode(value = "payments")
//                        }
//                )
//        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Client client;

    public Payment(Double amount, PaymentType type, Date date, Contract contract, Client client) {
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.contract = contract;
        this.client = client;
    }
}
