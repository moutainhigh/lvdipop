package com.enation.app.b2b2c.core.member.service;

import com.enation.app.b2b2c.core.member.model.MemberCollect;
import com.enation.framework.database.Page;

/**
 * 店铺收藏接口
 * @author xulipeng
 *	2015年1月12日14:16:43
 */

public interface IStoreCollectManager {
	
	/**
	 * 添加店铺
	 * @param collect
	 */
	public void addCollect(MemberCollect collect);
	
	/**
	 * 删除收藏的店铺
	 * @param collect_id
	 */
	public void delCollect(Integer collect_id);
	
	/**
	 * 根据会员ID和成员ID删除已收藏的店铺
	 * @param store_id
	 * @param member_id
	 */
	public void delCollect(Integer store_id,Integer member_id);
	
	/**
	 * 获取收藏店铺的集合
	 * @param memberid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page getList(Integer memberid,int page,int pageSize);

	/**
	 * 是否已经收藏
	 * @param memberId 会员id
	 * @param storeId 店铺id
	 * @return
	 */
	public boolean isCollect(int memberId, int storeId);
}
