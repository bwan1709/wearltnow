package com.wearltnow.mapper;

import com.wearltnow.dto.request.discount.DiscountCodeRequest;
import com.wearltnow.dto.response.auth.PermissionResponse;
import com.wearltnow.dto.response.auth.RoleResponse;
import com.wearltnow.dto.response.discount.DiscountCodeResponse;
import com.wearltnow.dto.response.user.UserGroupResponse;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.model.DiscountCode;
import com.wearltnow.model.Permission;
import com.wearltnow.model.Role;
import com.wearltnow.model.User;
import com.wearltnow.model.UserGroup;
import com.wearltnow.model.enums.DiscountStatus;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:15+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class DiscountCodeMapperImpl implements DiscountCodeMapper {

    @Override
    public DiscountCode toEntity(DiscountCodeRequest discountCodeRequest) {
        if ( discountCodeRequest == null ) {
            return null;
        }

        DiscountCode.DiscountCodeBuilder discountCode = DiscountCode.builder();

        discountCode.code( discountCodeRequest.getCode() );
        discountCode.type( discountCodeRequest.getType() );
        discountCode.amount( discountCodeRequest.getAmount() );
        discountCode.minOrderValue( discountCodeRequest.getMinOrderValue() );
        discountCode.startDate( discountCodeRequest.getStartDate() );
        discountCode.endDate( discountCodeRequest.getEndDate() );
        discountCode.usageLimit( discountCodeRequest.getUsageLimit() );

        discountCode.status( DiscountStatus.ACTIVE );

        return discountCode.build();
    }

    @Override
    public DiscountCodeResponse toDTO(DiscountCode discountCode) {
        if ( discountCode == null ) {
            return null;
        }

        DiscountCodeResponse.DiscountCodeResponseBuilder discountCodeResponse = DiscountCodeResponse.builder();

        discountCodeResponse.userGroupResponse( userGroupToUserGroupResponse( discountCode.getUserGroup() ) );
        discountCodeResponse.id( discountCode.getId() );
        discountCodeResponse.code( discountCode.getCode() );
        discountCodeResponse.amount( discountCode.getAmount() );
        discountCodeResponse.type( discountCode.getType() );
        discountCodeResponse.usageLimit( discountCode.getUsageLimit() );
        discountCodeResponse.startDate( discountCode.getStartDate() );
        discountCodeResponse.endDate( discountCode.getEndDate() );
        discountCodeResponse.status( discountCode.getStatus() );
        discountCodeResponse.minOrderValue( discountCode.getMinOrderValue() );

        return discountCodeResponse.build();
    }

    protected PermissionResponse permissionToPermissionResponse(Permission permission) {
        if ( permission == null ) {
            return null;
        }

        PermissionResponse.PermissionResponseBuilder permissionResponse = PermissionResponse.builder();

        permissionResponse.name( permission.getName() );
        permissionResponse.description( permission.getDescription() );

        return permissionResponse.build();
    }

    protected Set<PermissionResponse> permissionSetToPermissionResponseSet(Set<Permission> set) {
        if ( set == null ) {
            return null;
        }

        Set<PermissionResponse> set1 = new LinkedHashSet<PermissionResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Permission permission : set ) {
            set1.add( permissionToPermissionResponse( permission ) );
        }

        return set1;
    }

    protected RoleResponse roleToRoleResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.name( role.getName() );
        roleResponse.description( role.getDescription() );
        roleResponse.permissions( permissionSetToPermissionResponseSet( role.getPermissions() ) );

        return roleResponse.build();
    }

    protected Set<RoleResponse> roleSetToRoleResponseSet(Set<Role> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleResponse> set1 = new LinkedHashSet<RoleResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Role role : set ) {
            set1.add( roleToRoleResponse( role ) );
        }

        return set1;
    }

    protected UserResponse userToUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.userId( user.getUserId() );
        userResponse.username( user.getUsername() );
        userResponse.lastname( user.getLastname() );
        userResponse.firstname( user.getFirstname() );
        userResponse.phone( user.getPhone() );
        userResponse.email( user.getEmail() );
        userResponse.gender( user.getGender() );
        userResponse.dob( user.getDob() );
        userResponse.roles( roleSetToRoleResponseSet( user.getRoles() ) );

        return userResponse.build();
    }

    protected List<UserResponse> userListToUserResponseList(List<User> list) {
        if ( list == null ) {
            return null;
        }

        List<UserResponse> list1 = new ArrayList<UserResponse>( list.size() );
        for ( User user : list ) {
            list1.add( userToUserResponse( user ) );
        }

        return list1;
    }

    protected UserGroupResponse userGroupToUserGroupResponse(UserGroup userGroup) {
        if ( userGroup == null ) {
            return null;
        }

        UserGroupResponse userGroupResponse = new UserGroupResponse();

        userGroupResponse.setId( userGroup.getId() );
        userGroupResponse.setName( userGroup.getName() );
        userGroupResponse.setUsers( userListToUserResponseList( userGroup.getUsers() ) );

        return userGroupResponse;
    }
}
