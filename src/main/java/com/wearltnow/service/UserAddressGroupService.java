package com.wearltnow.service;

import com.wearltnow.dto.request.user.UserAddressGroupCreationRequest;
import com.wearltnow.dto.request.user.UserAddressGroupUpdationRequest;
import com.wearltnow.dto.response.user.UserAddressGroupResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.UserAddressGroupMapper;
import com.wearltnow.model.UserAddressGroup;
import com.wearltnow.repository.UserAddressGroupRepository;
import com.wearltnow.repository.UserRepository;
import com.wearltnow.specification.UserAddressGroupSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserAddressGroupService {

    @Autowired
    private UserAddressGroupRepository userAddressGroupRepository;
    @Autowired
    private UserAddressGroupMapper userAddressGroupMapper;
    @Autowired
    private UserRepository userRepository;

    public List<UserAddressGroupResponse> findAll(boolean isActive) {
        Specification<UserAddressGroup> spec = Specification.where(UserAddressGroupSpecification.isNotDeleted());
        if(isActive){
            spec = spec.and(UserAddressGroupSpecification.isActive());
        }
        return userAddressGroupRepository.findAll(spec).stream()
                .map(userAddressGroupMapper::toUserAddressGroupResponse)
                .collect(Collectors.toList());
    }

    public UserAddressGroupResponse create(UserAddressGroupCreationRequest request) {
        if (request.getUserId() == null) {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }
        List<UserAddressGroup> existingActiveGroups = userAddressGroupRepository.findAllByUser_UserIdAndIsActiveTrue(request.getUserId());
        for (UserAddressGroup activeGroup : existingActiveGroups) {
            activeGroup.setIsActive(false);
            userAddressGroupRepository.save(activeGroup);
        }

        UserAddressGroup userAddressGroup = userAddressGroupMapper.toUserAddressGroup(request);
        userAddressGroup.setIsActive(true);
        userAddressGroup = userAddressGroupRepository.save(userAddressGroup);
        return userAddressGroupMapper.toUserAddressGroupResponse(userAddressGroup);
    }

    public UserAddressGroupResponse update(Long id, UserAddressGroupUpdationRequest updatedAddressRequest) {
        userAddressGroupRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_ADDRESS_NOTFOUND)
        );
        boolean userExists = userRepository.existsById(updatedAddressRequest.getUserId());
        if (!userExists) {
            throw new AppException(ErrorCode.USER_NOTFOUND);
        }
        return userAddressGroupRepository.findById(id)
                .map(existingAddress -> {
                    List<UserAddressGroup> existingActiveGroups = userAddressGroupRepository.findAllByUser_UserIdAndIsActiveTrue(existingAddress.getUser().getUserId());
                    for (UserAddressGroup activeGroup : existingActiveGroups) {
                        activeGroup.setIsActive(false);
                        userAddressGroupRepository.save(activeGroup);
                    }
                    userAddressGroupMapper.updateUserAddressGroup(existingAddress, updatedAddressRequest);
                    existingAddress.setIsActive(true);
                    return userAddressGroupMapper.toUserAddressGroupResponse(
                            userAddressGroupRepository.save(existingAddress)
                    );
                })
                .orElseThrow(() -> new AppException(ErrorCode.USER_ADDRESS_NOTFOUND));

    }

    public UserAddressGroup updateStatus(Long id, Boolean status) {
        UserAddressGroup existingAddress = userAddressGroupRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_ADDRESS_NOTFOUND));

        if (status) {
            // Nếu trạng thái được cập nhật thành true, cần vô hiệu hóa tất cả các nhóm còn lại
            Long userId = existingAddress.getUser().getUserId();
            List<UserAddressGroup> existingActiveGroups = userAddressGroupRepository.findAllByUser_UserIdAndIsActiveTrue(userId);
            for (UserAddressGroup activeGroup : existingActiveGroups) {
                activeGroup.setIsActive(false);
                userAddressGroupRepository.save(activeGroup);
            }
        }

        // Cập nhật trạng thái cho nhóm địa chỉ
        existingAddress.setIsActive(status); // Cập nhật trạng thái
        return userAddressGroupRepository.save(existingAddress);
    }

    // Xóa địa chỉ theo ID
    public void deleteById(Integer id) {
        userAddressGroupRepository.deleteById(id);
    }

    public List<UserAddressGroupResponse> findByUserId(Long userId) {
        // Lấy danh sách UserAddressGroup từ repository
        List<UserAddressGroup> userAddressGroups = userAddressGroupRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_ADDRESS_NOTFOUND));

        // Chuyển đổi danh sách UserAddressGroup thành danh sách UserAddressGroupResponse
        return userAddressGroupMapper.toUserAddressGroupResponseList(userAddressGroups);
    }

    public UserAddressGroupResponse findByUserIdAndIsActive(Long id) {
        UserAddressGroup userAddressGroup = userAddressGroupRepository.findByUserUserIdAndIsActive(id, true)
                .orElseThrow(() -> new AppException(ErrorCode.USER_ADDRESS_NOTFOUND));
        return userAddressGroupMapper.toUserAddressGroupResponse(userAddressGroup);
    }
}
