package com.uab.taller.store.usecase.transaction;

import com.uab.taller.store.domain.Transaction;
import com.uab.taller.store.domain.dto.request.CreateTransactionRequest;
import com.uab.taller.store.domain.dto.request.DepositRequest;
import com.uab.taller.store.domain.dto.request.WithdrawalRequest;
import com.uab.taller.store.domain.dto.request.TransferRequest;
import com.uab.taller.store.domain.dto.response.TransactionDetailResponse;
import com.uab.taller.store.service.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso mejorado para crear transacciones usando DTOs
 * Maneja correctamente las actualizaciones de saldo según el tipo de transacción
 */
@Service
public class CreateTransactionWithDtoUseCase {

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private TransactionMappingUseCase mappingUseCase;

    @Autowired
    private ProcessDepositUseCase processDepositUseCase;

    @Autowired
    private ProcessWithdrawalUseCase processWithdrawalUseCase;

    @Autowired
    private ProcessTransferUseCase processTransferUseCase;

    @Transactional
    public TransactionDetailResponse createTransaction(CreateTransactionRequest request) {
        Transaction savedTransaction;
          // Procesar según el tipo de transacción para manejar correctamente los saldos
        switch (request.getTransactionType().toUpperCase()) {
            case "DEPOSIT":
                // DEPÓSITO: Incrementa el saldo de la cuenta origen
                // Crea una solicitud de depósito y la procesa aumentando el balance de la cuenta
                DepositRequest depositRequest = DepositRequest.builder()
                    .accountId(request.getSourceAccountId())
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .reference(request.getReference())
                    .build();
                savedTransaction = processDepositUseCase.processDeposit(depositRequest);
                break;
                
            case "WITHDRAWAL":
                // RETIRO: Disminuye el saldo de la cuenta origen
                // Valida que haya fondos suficientes antes de procesar el retiro
                WithdrawalRequest withdrawalRequest = WithdrawalRequest.builder()
                    .accountId(request.getSourceAccountId())
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .reference(request.getReference())
                    .build();
                savedTransaction = processWithdrawalUseCase.processWithdrawal(withdrawalRequest);
                break;
                
            case "TRANSFER":
                // TRANSFERENCIA: Mueve dinero de una cuenta origen a una cuenta destino
                // Valida que existe cuenta destino, fondos suficientes en origen,
                // disminuye saldo de origen e incrementa saldo de destino
                if (request.getTargetAccountId() == null) {
                    throw new IllegalArgumentException("La cuenta destino es obligatoria para transferencias");
                }
                TransferRequest transferRequest = TransferRequest.builder()
                    .sourceAccountId(request.getSourceAccountId())
                    .targetAccountId(request.getTargetAccountId())
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .reference(request.getReference())
                    .build();
                savedTransaction = processTransferUseCase.processTransfer(transferRequest);
                break;
                
            case "PAYMENT":
                // PAGO: Similar a un retiro pero con propósito específico de pago
                // Disminuye el saldo de la cuenta origen como un retiro,
                // pero actualiza el tipo de transacción para distinguirlo como pago
                WithdrawalRequest paymentRequest = WithdrawalRequest.builder()
                    .accountId(request.getSourceAccountId())
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .reference(request.getReference())
                    .build();
                savedTransaction = processWithdrawalUseCase.processWithdrawal(paymentRequest);
                // Actualizar el tipo de transacción para reflejar que es un pago
                savedTransaction.setTransactionType("PAYMENT");
                savedTransaction = transactionService.update(savedTransaction);
                break;
                
            default:
                throw new IllegalArgumentException("Tipo de transacción no soportado: " + request.getTransactionType());
        }

        // Convertir entidad a DTO de respuesta
        return mappingUseCase.toDetailResponse(savedTransaction);
    }
}
