package com.wearltnow.service;

import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.discount.DiscountCodeRequest;
import com.wearltnow.dto.response.discount.DiscountCodeResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.DiscountCodeMapper;
import com.wearltnow.model.DiscountCode;
import com.wearltnow.model.User;
import com.wearltnow.model.UserGroup;
import com.wearltnow.model.enums.DiscountStatus;
import com.wearltnow.model.enums.DiscountType;
import com.wearltnow.repository.DiscountCodeRepository;
import com.wearltnow.repository.UserGroupRepository;
import com.wearltnow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DiscountCodeService {

    @Autowired
    private DiscountCodeRepository discountCodeRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private DiscountCodeMapper discountCodeMapper;
    @Autowired
    private UserRepository userRepository;

    public PageResponse<DiscountCodeResponse> getAllDiscount(int page, int size){
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = discountCodeRepository.findAllByDeletedFalse(pageable);
        return PageResponse.<DiscountCodeResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(discountCodeMapper::toDTO).toList())
                .build();
    }
    // Kiểm tra mã giảm giá có hợp lệ không (theo nhóm khách hàng và thời gian)
    public boolean isDiscountValid(DiscountCode discountCode, User user) {
        if (LocalDateTime.now().isBefore(discountCode.getStartDate()) || LocalDateTime.now().isAfter(discountCode.getEndDate())) {
            return false;
        } else if(discountCode.getUserGroup() != null && "ALL".equals(discountCode.getUserGroup().getName())){
                return true;
        }else
            return isDiscountValidForUser(discountCode, user);
    }

    // Kiểm tra xem mã giảm giá có thể áp dụng cho nhóm khách hàng của người dùng không
    private boolean isDiscountValidForUser(DiscountCode discountCode, User user) {
//        if (discountCode.getUserGroup().getName().equals("ALL")) {
//            return true;
//        }
//        // Check if the discount code is assigned to a specific user group
        if (discountCode.getUserGroup() != null) {
            return discountCode.getUserGroup().equals(user.getUserGroup()) &&
                    discountCode.getUsers().contains(user);
        }
        return discountCode.getUsers().contains(user);
    }

    public Optional<DiscountCode> findDiscountCodeByCode(String code) {
        return discountCodeRepository.findByCode(code);
    }

    // Áp dụng mã giảm giá vào đơn hàng
    public BigDecimal applyDiscount(DiscountCode discountCode, BigDecimal orderTotal) {
        if (discountCode.getType().equals(DiscountType.PERCENTAGE)) {
            BigDecimal discountAmount;
            BigDecimal percentage = discountCode.getAmount();  // Let's assume discountCode.getAmount() is already in percentage form (e.g., 20 for 20%)
            discountAmount = orderTotal.multiply(percentage).divide(new BigDecimal(100));  // Apply the percentage directly to the orderTotal

            discountCode.setUsageLimit(discountCode.getUsageLimit() - 1);
            return discountAmount;
        } else if (discountCode.getType().equals(DiscountType.FIXED)) {
            discountCode.setUsageLimit(discountCode.getUsageLimit() - 1);
            return discountCode.getAmount();
        }
        return BigDecimal.ZERO;
    }

    public List<DiscountCodeResponse> findDiscountCodesForUser(User user) {
        return discountCodeRepository.findAll().stream()
                .filter(discountCode -> isDiscountValidForUser(discountCode, user))
                .map(DiscountCodeMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }


    public DiscountCodeResponse createDiscountCode(DiscountCodeRequest discountCodeRequest) {

        // Kiểm tra xem mã giảm giá có bị trùng lặp không
        Optional<DiscountCode> existingDiscountCode = discountCodeRepository.findByCode(discountCodeRequest.getCode());
        if (existingDiscountCode.isPresent()) {
            throw new AppException(ErrorCode.DISCOUNT_CODE_EXISTS);
        }

        // Kiểm tra nhóm người dùng hợp lệ
        UserGroup userGroup = null;
        if (discountCodeRequest.getUserGroupId() != null) {
            userGroup = userGroupRepository.findById(discountCodeRequest.getUserGroupId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_GROUP_NOTFOUND));
        }

        // Ánh xạ DiscountCodeRequest thành DiscountCode (Entity)
        DiscountCode discountCode = discountCodeMapper.toEntity(discountCodeRequest);
        discountCode.setStatus(DiscountStatus.ACTIVE);
        discountCode.setUserGroup(userGroup);

        // Lưu vào cơ sở dữ liệu
        DiscountCode savedDiscountCode = discountCodeRepository.save(discountCode);

        // Trả về DTO
        return discountCodeMapper.toDTO(savedDiscountCode);
    }

    public void addUsersToDiscountCode(Long discountCodeId, List<Long> userIds) {
        DiscountCode discountCode = discountCodeRepository.findById(discountCodeId)
                .orElseThrow(() -> new RuntimeException("Discount code not found"));

        Set<User> users = new HashSet<>(userRepository.findAllById(userIds));
        if (users.size() != userIds.size()) {
            throw new AppException(ErrorCode.SOME_USER_NOT_FOUND);
        }

        discountCode.getUsers().addAll(users);
        discountCodeRepository.save(discountCode);
    }

    public void delete(Long id){
        discountCodeRepository.delete(discountCodeRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_CODE_NOT_FOUND)));
    }
}
