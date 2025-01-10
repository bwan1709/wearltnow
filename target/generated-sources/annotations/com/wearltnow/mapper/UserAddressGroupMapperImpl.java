package com.wearltnow.mapper;

import com.wearltnow.dto.request.user.UserAddressGroupCreationRequest;
import com.wearltnow.dto.request.user.UserAddressGroupUpdationRequest;
import com.wearltnow.dto.response.user.UserAddressGroupResponse;
import com.wearltnow.model.User;
import com.wearltnow.model.UserAddressGroup;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:16+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserAddressGroupMapperImpl implements UserAddressGroupMapper {

    @Override
    public UserAddressGroup toUserAddressGroup(UserAddressGroupCreationRequest userAddressGroupCreationRequest) {
        if ( userAddressGroupCreationRequest == null ) {
            return null;
        }

        UserAddressGroup.UserAddressGroupBuilder userAddressGroup = UserAddressGroup.builder();

        userAddressGroup.user( userAddressGroupCreationRequestToUser( userAddressGroupCreationRequest ) );
        userAddressGroup.toName( userAddressGroupCreationRequest.getToName() );
        userAddressGroup.toPhone( userAddressGroupCreationRequest.getToPhone() );
        userAddressGroup.toWardCode( userAddressGroupCreationRequest.getToWardCode() );
        userAddressGroup.toAddress( userAddressGroupCreationRequest.getToAddress() );
        userAddressGroup.toDistrictId( userAddressGroupCreationRequest.getToDistrictId() );
        userAddressGroup.toWardName( userAddressGroupCreationRequest.getToWardName() );
        userAddressGroup.toDistrictName( userAddressGroupCreationRequest.getToDistrictName() );
        userAddressGroup.toProvinceId( userAddressGroupCreationRequest.getToProvinceId() );
        userAddressGroup.toProvinceName( userAddressGroupCreationRequest.getToProvinceName() );

        return userAddressGroup.build();
    }

    @Override
    public UserAddressGroupResponse toUserAddressGroupResponse(UserAddressGroup userAddressGroup) {
        if ( userAddressGroup == null ) {
            return null;
        }

        UserAddressGroupResponse userAddressGroupResponse = new UserAddressGroupResponse();

        userAddressGroupResponse.setUserId( userAddressGroupUserUserId( userAddressGroup ) );
        userAddressGroupResponse.setId( userAddressGroup.getId() );
        userAddressGroupResponse.setToName( userAddressGroup.getToName() );
        userAddressGroupResponse.setToPhone( userAddressGroup.getToPhone() );
        userAddressGroupResponse.setToWardCode( userAddressGroup.getToWardCode() );
        userAddressGroupResponse.setToAddress( userAddressGroup.getToAddress() );
        userAddressGroupResponse.setIsActive( userAddressGroup.getIsActive() );
        userAddressGroupResponse.setToDistrictId( userAddressGroup.getToDistrictId() );
        userAddressGroupResponse.setToWardName( userAddressGroup.getToWardName() );
        userAddressGroupResponse.setToDistrictName( userAddressGroup.getToDistrictName() );
        userAddressGroupResponse.setToProvinceId( userAddressGroup.getToProvinceId() );
        userAddressGroupResponse.setToProvinceName( userAddressGroup.getToProvinceName() );

        return userAddressGroupResponse;
    }

    @Override
    public List<UserAddressGroupResponse> toUserAddressGroupResponseList(List<UserAddressGroup> userAddressGroups) {
        if ( userAddressGroups == null ) {
            return null;
        }

        List<UserAddressGroupResponse> list = new ArrayList<UserAddressGroupResponse>( userAddressGroups.size() );
        for ( UserAddressGroup userAddressGroup : userAddressGroups ) {
            list.add( toUserAddressGroupResponse( userAddressGroup ) );
        }

        return list;
    }

    @Override
    public void updateUserAddressGroup(UserAddressGroup userAddressGroup, UserAddressGroupUpdationRequest request) {
        if ( request == null ) {
            return;
        }

        if ( userAddressGroup.getUser() == null ) {
            userAddressGroup.setUser( User.builder().build() );
        }
        userAddressGroupUpdationRequestToUser( request, userAddressGroup.getUser() );
        userAddressGroup.setToName( request.getToName() );
        userAddressGroup.setToPhone( request.getToPhone() );
        userAddressGroup.setToWardCode( request.getToWardCode() );
        userAddressGroup.setToAddress( request.getToAddress() );
        userAddressGroup.setToDistrictId( request.getToDistrictId() );
        userAddressGroup.setToWardName( request.getToWardName() );
        userAddressGroup.setToDistrictName( request.getToDistrictName() );
        userAddressGroup.setToProvinceId( request.getToProvinceId() );
        userAddressGroup.setToProvinceName( request.getToProvinceName() );
    }

    protected User userAddressGroupCreationRequestToUser(UserAddressGroupCreationRequest userAddressGroupCreationRequest) {
        if ( userAddressGroupCreationRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.userId( userAddressGroupCreationRequest.getUserId() );

        return user.build();
    }

    private Long userAddressGroupUserUserId(UserAddressGroup userAddressGroup) {
        if ( userAddressGroup == null ) {
            return null;
        }
        User user = userAddressGroup.getUser();
        if ( user == null ) {
            return null;
        }
        Long userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    protected void userAddressGroupUpdationRequestToUser(UserAddressGroupUpdationRequest userAddressGroupUpdationRequest, User mappingTarget) {
        if ( userAddressGroupUpdationRequest == null ) {
            return;
        }

        mappingTarget.setUserId( userAddressGroupUpdationRequest.getUserId() );
    }
}
