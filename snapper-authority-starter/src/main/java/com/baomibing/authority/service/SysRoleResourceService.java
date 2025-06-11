
package com.baomibing.authority.service;



import com.baomibing.authority.constant.enums.MenuTypeEnum;
import com.baomibing.authority.constant.enums.ResourceTypeEnum;
import com.baomibing.authority.dto.ButtonDto;
import com.baomibing.authority.dto.MenuDto;
import com.baomibing.authority.dto.ResourceApiDto;
import com.baomibing.authority.dto.RoleResourceDto;
import com.baomibing.authority.vo.DataPermBusinessVo;
import com.baomibing.authority.vo.DataPermColumnVo;
import com.baomibing.authority.vo.DataPermFunctionVo;
import com.baomibing.core.base.MBaseService;

import java.util.List;
import java.util.Set;

public interface SysRoleResourceService extends MBaseService<RoleResourceDto> {

	/**
	 * 刷新数据资源权限，使之生效（设置完资源权限后）
	 */
	void refreshPrivileges(String... buttons);

	/**
	 * 超级管理员获取权限资源ID列表
	 * 
	 * @param resourceType 资源类型
	 * @return
	 */
	List<String> listPermResourceIdsBySuper(ResourceTypeEnum resourceType);

	/**
	 * 根据角色列表获取权限对应的资源ID列表
	 * 
	 * @param roleIds 角色ID列表
	 * @return
	 */
//	List<String> listPermResourceIdsByRoles(Set<String> roleIds);

	/**
	 * 根据角色列表及资源类型获取权限对应的资源ID列表
	 * 
	 * @param roleIds      角色列表
	 * @param resourceType 资源类型 {@link ResourceTypeEnum}
	 * @return
	 */
	List<String> listPermResourceIdsByRoles(Set<String> roleIds, ResourceTypeEnum resourceType);

	/**
	 * 根据角色列表获取权限对应的资源API列表-包括无权限的资源API
	 * 
	 * @param roleIds 角色列表
	 * @return
	 */
//	List<ResourceApiDto> listResourceApiByRoles(Set<String> roleIds);
	/**
	 * 根据角色列表和菜单ID获取对应的权限按钮列表-包括无权限的按钮
	 * 
	 * @param roleIds 角色ID列表
	 * @param menuId  菜单ID
	 * @return
	 */
	List<ButtonDto> listPermButtonsByRolesAndMenu(Set<String> roleIds, String menuId);

	/**
	 * 根据角色保存按钮的权限
	 * 
	 * @param roleId    角色ID
	 * @param menuId    菜单ID
	 * @param buttonIds 按钮ID列表
	 */
	void saveButtonPermsByMenuAndRole(String roleId, String menuId, Set<String> buttonIds);

	/**
	 * 根据角色保存菜单的权限
	 * 
	 * @param roleId  角色ID
	 * @param menuIds 菜单ID列表
	 */
	void saveMenusPermsByRole(String roleId, Set<String> menuIds);

	/**
	 * 获取角色列表对应的权限菜单-包括无权限的菜单
	 * 
	 * @param roleIds 角色列表
	 * @return
	 */
//	List<MenuDto> listAllPermMenusByRoles(Set<String> roleIds);

	/**
	 * 获取角色列表对应的某个类型的权限菜单-包括无权限的菜单
	 * 
	 * @param roleIds 角色列表
	 * @param type    菜单类型
	 * @return
	 */
	List<MenuDto> listAllPermPointMenusByRoles(Set<String> roleIds, MenuTypeEnum type);

	/**
	 * 超级管理员获取某个类型的菜单
	 * 
	 * @param type 菜单类型
	 * @return
	 */
	List<MenuDto> listAllPermMenusForSuper(MenuTypeEnum type);

	/**
	 * 展示所有菜单及菜单下的按钮(后台)-用来授权
	 * 
	 * @return //
	 */
//	List<MenuDto> listAllMenusAndButtonsForGrant();

	/**
	 * 展示所有菜单-用来授权
	 * 
	 * @return
	 */
	List<MenuDto> listAllMenusForGrant();

	/**
	 * 显示所有按钮-用来授权
	 * 
	 * @param menuId 菜单ID
	 * @return
	 */
	List<ButtonDto> listAllButtonsByMenuForGrant(String menuId);

	/**
	 * 获取所有已授权按钮对应的角色列表-用于授权缓存预热
	 * 
	 * @return
	 */
	List<ResourceApiDto> listAllPermButtonsForGroupRoleIds(Set<String> buttonIds);

	/**
	 * 获取所有未授权按钮对应的角色列表-用于授权缓存预热
	 * 
	 * @return
	 */
	List<ResourceApiDto> listAllUnPermButtonsForGroupRoleIds(Set<String> buttonIds);

	/**
	 * 根据用户获取所有权限按钮及其对应的业务权限范围
	 * 
	 * @param userId 用户ID
	 * @return
	 */
	List<ButtonDto> listPermButtonsForBusinessPermByGroupAndUser(String orgId, String userId);

	/**
	 * 根据用户ID获取其组织对应权限的菜单和按钮列表及其对应的数据权限范围
	 * 
	 * @param userId
	 * @return
	 */
	List<MenuDto> listPermMenusAndButtonsForBusinessPermByUser(String orgId, String userId);

	/**
	 * 根据角色删除对应的资源权限
	 *
	 * @param roles
	 */
	void deleteByRoles(Set<String> roles);

	/**
	 * 根据资源ID列表及对应的资源类型删除资源角色关系
	 *
	 * @param resourceIds 资源ID列表
	 * @param resourceType 资源类型
	 */
	void deleteByResources(Set<String> resourceIds, ResourceTypeEnum resourceType);

	/**
	 * 根据用户组ID获取权限的菜单和按钮列表及其对应的数据权限范围
	 * @param usetId 用户组ID
	 * @return
	 */
	List<MenuDto> listPermMenusAndButtonsForBusinessPermByUset(String usetId);

	/**
	 * 根据角色ID列表获取权限的菜单和按钮列表
	 * @param roleIds 角色ID列表
	 * @param beFilterPerm 是否过滤数据权限
	 * @param beFilterNoPerm 是否过滤无权限
	 * @return
	 */
	List<MenuDto> listPermMenusAndButtonsByRoleIds(Set<String> roleIds, boolean beFilterPerm, boolean beFilterNoPerm);

	/**
	 * 根据角色ID列表获取用友@ACTION注解的菜单和按钮列表
	 * @param roleIds
	 * @return
	 */
	List<MenuDto> listActionMenusAndButtonsByRoleIds(Set<String> roleIds);


	/**
	 * 获取按钮数据权限
	 * @param viewType 类型
	 * @param orgId    用户所在组织
	 * @param permId   权限ID
	 * @param usetId   用户组ID
	 * @return
	 */
	DataPermFunctionVo getFunctionDataPerm(String viewType, String orgId, String permId, String usetId);

	/**
	 * 获取业务数据权限
	 * @param viewType 类型
	 * @param orgId    用户所在组织
	 * @param permId   权限ID
	 * @param usetId   用户组ID
	 * @return
	 */
	List<DataPermBusinessVo> getBusinessDataPerm(String viewType, String orgId, String permId, String usetId);

	/**
	 * 获取列数据权限
	 * @param viewType  类型
	 * @param orgId     用户所在组织
	 * @param permId    权限ID
	 * @param usetId    用户组ID
	 * @return
	 */
	List<DataPermColumnVo> getColumnDataPerm(String viewType, String orgId, String permId, String usetId);
}
