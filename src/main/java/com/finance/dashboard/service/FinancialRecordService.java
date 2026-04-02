package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.FinancialRecordRequest;
import com.finance.dashboard.dto.response.FinancialRecordResponse;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.model.User;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    public FinancialRecordService(FinancialRecordRepository recordRepository,
                                   UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    public FinancialRecordResponse createRecord(
            FinancialRecordRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .createdBy(user)
                .build();

        return mapToResponse(recordRepository.save(record));
    }

    public List<FinancialRecordResponse> getAllRecords(
            RecordType type, String category,
            LocalDate from, LocalDate to) {

        List<FinancialRecord> records;

        if (type != null && from != null && to != null) {
            records = recordRepository
                .findByTypeAndDateBetweenAndIsDeletedFalse(type, from, to);
        } else if (type != null) {
            records = recordRepository.findByTypeAndIsDeletedFalse(type);
        } else if (category != null) {
            records = recordRepository.findByCategoryAndIsDeletedFalse(category);
        } else if (from != null && to != null) {
            records = recordRepository.findByDateBetweenAndIsDeletedFalse(from, to);
        } else {
            records = recordRepository.findByIsDeletedFalse();
        }

        return records.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public FinancialRecordResponse getRecordById(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Record not found with id: " + id));
        if (record.isDeleted()) {
            throw new ResourceNotFoundException("Record not found with id: " + id);
        }
        return mapToResponse(record);
    }

    public FinancialRecordResponse updateRecord(
            Long id, FinancialRecordRequest request) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Record not found with id: " + id));

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());

        return mapToResponse(recordRepository.save(record));
    }

    public void deleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Record not found with id: " + id));
        record.setDeleted(true); // soft delete
        recordRepository.save(record);
    }

    public FinancialRecordResponse mapToResponse(FinancialRecord record) {
        return new FinancialRecordResponse(
                record.getId(),
                record.getAmount(),
                record.getType().name(),
                record.getCategory(),
                record.getDate(),
                record.getNotes(),
                record.getCreatedBy().getName(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}